package ar.edu.itba.paw.usecases.album;

import java.time.LocalDate;

public record CreateAlbumCommand(
    String title,
    String genre,
    LocalDate releaseDate,
    Long imageId,
    Long artistId
) {
    public CreateAlbumCommand {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Album title is required");
        }
        if (imageId == null || imageId <= 0) {
            throw new IllegalArgumentException("Image ID is required");
        }
    }
}
