package ar.edu.itba.paw.usecases.user;

public class CreateUserCommand {
    private final String username;
    private final String email;
    private final String password;

    public CreateUserCommand(String username, String email, String password) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username required");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email required");
        }
        if (password == null || password.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters");
        }
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public String username() {
        return username;
    }

    public String email() {
        return email;
    }

    public String password() {
        return password;
    }
}
