package ar.edu.itba.paw.usecases.song;

public record DeleteSongCommand(Long id) {
    public DeleteSongCommand {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Song ID is required");
        }
    }
}
