package ar.edu.itba.paw.models.dtos;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class CreateUserDTO {

    @Size(min = 4, max = 50, message = "{validation.user.username.size}")
    @Pattern(regexp = "[a-zA-Z][a-zA-Z0-9]*", message = "{validation.user.username.pattern}")
    private String username;

    @Email(message = "{validation.user.email.invalid}")
    @Size(max = 100, message = "{validation.user.email.size}")
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
