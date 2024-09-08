package ar.edu.itba.paw.webapp.form;


import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class UserForm {


    @Size(min = 8)
    @Pattern(regexp = "[a-zA-Z][a-zA-Z0-9]*")
    private String username;
    @Email
    private String email;
    @Size(min = 8)
    private String password;
    @Size(min = 8)
    private String repeatPassword;

    public UserForm() {}

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
