package ar.edu.itba.paw.usecases.user;

public class UpdateUserCommand {
    private final Long userId;
    private final String newPassword;

    public UpdateUserCommand(Long userId, String newPassword) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("User ID required");
        }
        if (newPassword != null && newPassword.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters");
        }
        this.userId = userId;
        this.newPassword = newPassword;
    }

    public Long userId() {
        return userId;
    }

    public String newPassword() {
        return newPassword;
    }
}
