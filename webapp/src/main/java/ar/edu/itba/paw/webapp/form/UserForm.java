package ar.edu.itba.paw.webapp.form;


import ar.edu.itba.paw.webapp.form.validation.EmailNotInUse;
import ar.edu.itba.paw.webapp.form.validation.UsernameNotInUse;
import ar.edu.itba.paw.webapp.form.validation.passwords.PasswordConfirmation;
import ar.edu.itba.paw.webapp.form.validation.passwords.PasswordMatch;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@PasswordMatch(message = "{validation.user.password.match}")
public class UserForm implements PasswordConfirmation {

    @JsonProperty("username")
    @NotBlank(message = "{validation.user.username.notblank}")
    @Size(min = 4, max = 50, message = "{validation.user.username.size}")
    @Pattern(regexp = "[a-zA-Z][a-zA-Z0-9]*", message = "{validation.user.username.pattern}")
    @UsernameNotInUse(message = "{validation.user.username.in.use}")
    private String username;

    @JsonProperty("email")
    @NotBlank(message = "{validation.user.email.notblank}")
    @EmailNotInUse(message = "{validation.user.email.in.use}")
    @Email(message = "{validation.user.email.invalid}")
    private String email;

    @JsonProperty("password")
    @NotBlank(message = "{validation.user.password.notblank}")
    @Size(min = 8, message = "{validation.user.password.size}")
    private String password;

    @JsonProperty("repeatPassword")
    @NotBlank(message = "{validation.user.password.notblank}")
    @Size(min = 8, message = "{validation.user.password.size}")
    private String repeatPassword;

    public UserForm() {}

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
