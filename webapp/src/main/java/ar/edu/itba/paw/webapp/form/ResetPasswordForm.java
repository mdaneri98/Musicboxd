package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.form.validation.passwords.PasswordConfirmation;
import ar.edu.itba.paw.webapp.form.validation.passwords.PasswordMatch;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@PasswordMatch(message = "{validation.user.password.match}")
public class ResetPasswordForm implements PasswordConfirmation {

    @NotNull(message = "{validation.resetpassword.code.notnull}")
    private String code;
    @Size(min = 8, message = "${validation.resetpassword.password.size}")
    private String password;
    @Size(min = 8, message = "${validation.resetpassword.password.size}")
    private String repeatPassword;

    public ResetPasswordForm(String code, String password, String repeatPassword) {
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
