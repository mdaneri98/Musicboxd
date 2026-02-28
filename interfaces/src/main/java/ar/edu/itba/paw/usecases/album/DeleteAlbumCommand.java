package ar.edu.itba.paw.usecases.album;

public record DeleteAlbumCommand(Long albumId) {
    public DeleteAlbumCommand {
        if (albumId == null || albumId <= 0) {
            throw new IllegalArgumentException("Album ID is required");
        }
    }
}
