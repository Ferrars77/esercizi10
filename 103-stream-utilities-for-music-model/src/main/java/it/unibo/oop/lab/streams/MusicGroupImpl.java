package it.unibo.oop.lab.streams;

import static java.util.stream.Collectors.*;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.Set;
import java.util.stream.Stream;

/**
 * This class implements the MusicGroup interface, representing a music group with albums and songs.
 */
public final class MusicGroupImpl implements MusicGroup {

    private final Map<String, Integer> albums = new LinkedHashMap<>();
    private final Set<Song> songs = new LinkedHashSet<>();

    @Override
    public void addAlbum(final String albumName, final int year) {
        this.albums.put(albumName, year);
    }

    @Override
    public void addSong(final String songName, final Optional<String> albumName, final double duration) {
        if (albumName.isPresent() && !this.albums.containsKey(albumName.get())) {
            throw new IllegalArgumentException("invalid album name");
        }
        this.songs.add(new MusicGroupImpl.Song(songName, albumName, duration));
    }

    @Override
    public Stream<String> orderedSongNames() {
        return this.songs.stream()
            .map(s -> s.getSongName())
            .sorted();
    }

    @Override
    public Stream<String> albumNames() {
        return this.albums.keySet().stream();
    }

    @Override
    public Stream<String> albumInYear(final int year) {
        return this.albums.entrySet().stream()
        .filter(s -> s.getValue() == year)
        .map(s -> s.getKey());
    }

    @Override
    public int countSongs(final String albumName) {
        return (int) this.songs.stream()
        .filter(s -> s.getAlbumName().isPresent())
        .filter(s -> s.getAlbumName().get().equals(albumName))
        .count();
    }

    @Override
    public int countSongsInNoAlbum() {
        return (int) this.songs.stream()
        .filter(s -> !s.getAlbumName().isPresent())
        .count();
    }

    @Override
    public OptionalDouble averageDurationOfSongs(final String albumName) {
        return this.songs.stream()
        .filter(s -> s.getAlbumName().isPresent())
        .filter(s -> s.getAlbumName().get().equals(albumName))
        .mapToDouble(s -> s.getDuration())
        .average();
    }

    @Override
    public Optional<String> longestSong() {
        return this.songs.stream()
            .reduce((s1, s2) -> s1.getDuration() > s2.getDuration() ? s1 : s2)
            .map(s -> s.getSongName());
    }

    @Override
    public Optional<String> longestAlbum() {
        return this.songs.stream()
            .filter(s -> s.getAlbumName().isPresent())
            .collect(groupingBy(s -> s.getAlbumName().get(), summingDouble(Song::getDuration)))
            .entrySet().stream()
            .reduce((e1, e2) -> e1.getValue() > e2.getValue() ? e1 : e2)
            .map(Map.Entry::getKey);
    }

    private static final class Song {

        private final String songName;
        private final Optional<String> albumName;
        private final double duration;
        private int hash;

        Song(final String name, final Optional<String> album, final double len) {
            super();
            this.songName = name;
            this.albumName = album;
            this.duration = len;
        }

        String getSongName() {
            return songName;
        }

        Optional<String> getAlbumName() {
            return albumName;
        }

        double getDuration() {
            return duration;
        }

        @Override
        public int hashCode() {
            if (hash == 0) {
                hash = Objects.hash(songName, albumName, duration);
            }
            return hash;
        }

        @Override
        public boolean equals(final Object obj) {
            return obj instanceof Song other
                && albumName.equals(other.albumName)
                && songName.equals(other.songName)
                && duration == other.duration;
        }

        @Override
        public String toString() {
            return "Song [songName=" + songName + ", albumName=" + albumName + ", duration=" + duration + "]";
        }

    }

}
