package ar.edu.itba.paw.usecases.artist;

public record UpdateArtistCommand(
    Long artistId,
    String name,
    String bio,
    Long imageId
) {
    public UpdateArtistCommand {
        if (artistId == null || artistId <= 0) {
            throw new IllegalArgumentException("Artist ID is required");
        }
    }
}
