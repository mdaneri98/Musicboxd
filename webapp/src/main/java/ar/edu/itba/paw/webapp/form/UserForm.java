package ar.edu.itba.paw.webapp.form;


import ar.edu.itba.paw.webapp.form.validation.EmailNotInUse;
import ar.edu.itba.paw.webapp.form.validation.PasswordMatch;
import ar.edu.itba.paw.webapp.form.validation.UsernameNotInUse;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@PasswordMatch
public class UserForm {

    @Size(min = 4, max = 50, message = "Username must be between 4 and 50 characters long")
    @Pattern(regexp = "[a-zA-Z][a-zA-Z0-9]*", message = "Username must start with a letter and can only contain letters and numbers")
    @UsernameNotInUse(message = "This username is already taken")
    private String username;

    @EmailNotInUse(message = "This email is already registered")
    @Email(message = "Please enter a valid email address")
    private String email;

    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;

    @Size(min = 8, message = "Repeated password must be at least 8 characters long")
    private String repeatPassword;

    public UserForm(String username, String email, String password, String repeatPassword) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.repeatPassword = repeatPassword;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRepeatPassword() {
        return repeatPassword;
    }

    public void setRepeatPassword(String repeatPassword) {
        this.repeatPassword = repeatPassword;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
