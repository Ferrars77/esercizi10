package it.unibo.mvc;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Application entry point for DrawNumber game.
 */
public final class DrawNumberApp implements DrawNumberViewObserver {
    private static final int MIN = 0;
    private static final int MAX = 100;
    private static final int ATTEMPTS = 10;

    private final DrawNumber model;
    private final List<DrawNumberView> views;

    private final static String aFile = "src/main/resources/config.yml";
    public static final String SEP = File.separator ;
    public static final String FILE_NAME = "src" + SEP + "main" + SEP + "resources" + SEP + "config.yml";


   


   private int[] extractDatas(final String fileName) {
        final int[] datas = new int[3];
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                final String[] parts = line.split(";");
                for (final String part : parts) {
                    final String[] keyValue = part.split(":");
                    if (keyValue.length == 2) {
                        final String key = keyValue[0].trim();
                        final int value = Integer.parseInt(keyValue[1].trim());
                        if ("minimum".equals(key)) {
                            datas[0] = value;
                        } else if ("maximum".equals(key)) {
                            datas[1] = value;
                        } else if ("attempts".equals(key)) {
                            datas[2] = value;
                        }
                    }
                }
            }
        } catch (final IOException e) {
            
        }
        return datas;
    }

   

    /**
     * Constructor.
     *
     * @param views
     *            the views to attach
     */
    public DrawNumberApp(final DrawNumberView... views) {
        /*
         * Side-effect proof
         */
        this.views = Arrays.asList(Arrays.copyOf(views, views.length));
        for (final DrawNumberView view: views) {
            view.setObserver(this);
            view.start();
        }
        int[] datas = extractDatas(FILE_NAME);  
        this.model = new DrawNumberImpl(datas[0], datas[1], datas[2]);
    }

    @Override
    public void newAttempt(final int n) {
        try {
            final DrawResult result = model.attempt(n);
            for (final DrawNumberView view: views) {
                view.result(result);
            }
        } catch (final IllegalArgumentException e) {
            for (final DrawNumberView view: views) {
                view.numberIncorrect();
            }
        }
    }

    @Override
    public void resetGame() {
        this.model.reset();
    }

    @Override
    @SuppressFBWarnings(
        value = "DM_EXIT",
        justification = "Acceptable for exercising purposes."
    )
    public void quit() {
        /*
         * A bit harsh. A good application should configure the graphics to exit by
         * natural termination when closing is hit. To do things more cleanly, attention
         * should be paid to alive threads, as the application would continue to persist
         * until the last thread terminates.
         */
        System.exit(0);
    }

    /**
     * Application entry point.
     *
     * @throws FileNotFoundException if the configuration file cannot be fetched
     */
    public static void main() throws FileNotFoundException {
        new DrawNumberApp(new DrawNumberViewImpl());
        new DrawNumberApp(new DrawNumberViewImpl());
        new DrawNumberApp(new PrintStream());

    }

}
