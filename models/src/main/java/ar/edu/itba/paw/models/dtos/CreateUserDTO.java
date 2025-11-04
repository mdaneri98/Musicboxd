package ar.edu.itba.paw.models.dtos;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class CreateUserDTO {

    @Size(min = 4, max = 50, message = "Username must be between 4 and 50 characters")
    @Pattern(regexp = "[a-zA-Z][a-zA-Z0-9]*", message = "Username must start with a letter and contain only letters and numbers")
    private String username;

    @Email(message = "Email must be valid")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String email;

    private String password;

    public CreateUserDTO() {  }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
