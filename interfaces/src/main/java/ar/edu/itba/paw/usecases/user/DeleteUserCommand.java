package ar.edu.itba.paw.usecases.user;

public class DeleteUserCommand {
    private final Long userId;

    public DeleteUserCommand(Long userId) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("User ID required");
        }
        this.userId = userId;
    }

    public Long userId() {
        return userId;
    }
}
