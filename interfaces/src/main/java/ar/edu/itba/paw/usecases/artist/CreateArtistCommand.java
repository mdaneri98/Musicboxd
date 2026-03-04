package ar.edu.itba.paw.usecases.artist;

public record CreateArtistCommand(
    String name,
    String bio,
    Long imageId
) {
    public CreateArtistCommand {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Artist name is required");
        }
        if (imageId == null || imageId <= 0) {
            throw new IllegalArgumentException("Image ID is required");
        }
    }
}
