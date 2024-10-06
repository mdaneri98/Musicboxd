package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.form.validation.PasswordMatch;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@PasswordMatch(message = "Las contraseñas deben ser iguales")
public class CreatePasswordForm {

    @NotNull
    private String code;
    @Size(min = 8)
    private String password;
    @Size(min = 8)
    private String repeatPassword;

    public CreatePasswordForm(String code, String password, String repeatPassword) {
        this.password = password;
        this.repeatPassword = repeatPassword;
        this.code = code;
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
