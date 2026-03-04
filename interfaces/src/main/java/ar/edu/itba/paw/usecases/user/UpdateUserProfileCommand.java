package ar.edu.itba.paw.usecases.user;

public class UpdateUserProfileCommand {
    private final Long userId;
    private final String name;
    private final String bio;
    private final Long imageId;

    public UpdateUserProfileCommand(Long userId, String name, String bio, Long imageId) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("User ID required");
        }
        this.userId = userId;
        this.name = name;
        this.bio = bio;
        this.imageId = imageId;
    }

    public Long userId() {
        return userId;
    }

    public String name() {
        return name;
    }

    public String bio() {
        return bio;
    }

    public Long imageId() {
        return imageId;
    }
}
