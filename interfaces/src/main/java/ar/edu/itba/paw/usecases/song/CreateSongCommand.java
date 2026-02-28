package ar.edu.itba.paw.usecases.song;

import java.util.List;

public record CreateSongCommand(
    String title,
    String duration,
    Integer trackNumber,
    Long albumId,
    List<Long> artistIds
) {
    public CreateSongCommand {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Song title is required");
        }
        if (duration == null || duration.isBlank()) {
            throw new IllegalArgumentException("Duration is required");
        }
        if (albumId == null || albumId <= 0) {
            throw new IllegalArgumentException("Album ID is required");
        }
    }
}
