package ar.edu.itba.paw.usecases.artist;

public record DeleteArtistCommand(Long artistId) {
    public DeleteArtistCommand {
        if (artistId == null || artistId <= 0) {
            throw new IllegalArgumentException("Artist ID is required");
        }
    }
}
