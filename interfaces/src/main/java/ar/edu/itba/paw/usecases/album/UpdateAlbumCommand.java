package ar.edu.itba.paw.usecases.album;

import java.time.LocalDate;

public record UpdateAlbumCommand(
    Long albumId,
    String title,
    String genre,
    LocalDate releaseDate,
    Long imageId,
    Long artistId
) {
    public UpdateAlbumCommand {
        if (albumId == null || albumId <= 0) {
            throw new IllegalArgumentException("Album ID is required");
        }
    }
}
