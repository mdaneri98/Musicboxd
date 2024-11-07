package ar.edu.itba.paw.webapp.form;


import ar.edu.itba.paw.webapp.form.validation.EmailNotInUse;
import ar.edu.itba.paw.webapp.form.validation.PasswordMatch;
import ar.edu.itba.paw.webapp.form.validation.UsernameNotInUse;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@PasswordMatch(message = "{validation.user.password.match}")
public class UserForm {

    @Size(min = 4, max = 50, message = "{validation.user.username.size}")
    @Pattern(regexp = "[a-zA-Z][a-zA-Z0-9]*", message = "{validation.user.username.pattern}")
    @UsernameNotInUse(message = "{validation.user.username.in.use}")
    private String username;

    @EmailNotInUse(message = "validation.user.email.in.use")
    @Email(message = "validation.user.email.invalid")
    private String email;

    @Size(min = 8, message = "{validation.user.password.size}")
    private String password;

    @Size(min = 8, message = "{validation.user.password.size}")
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
