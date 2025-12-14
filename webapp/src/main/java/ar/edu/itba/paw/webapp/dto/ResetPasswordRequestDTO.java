package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.webapp.form.validation.passwords.PasswordConfirmation;
import ar.edu.itba.paw.webapp.form.validation.passwords.PasswordMatch;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * DTO for reset password request.
 * Used when user submits a new password with verification code.
 */
@PasswordMatch(message = "{validation.user.password.match}")
public class ResetPasswordRequestDTO implements PasswordConfirmation {

    @NotBlank(message = "{validation.resetpassword.code.notblank}")
    private String code;

    @NotBlank(message = "{validation.user.password.notblank}")
    @Size(min = 8, message = "{validation.user.password.size}")
    private String password;

    @NotBlank(message = "{validation.user.password.notblank}")
    @Size(min = 8, message = "{validation.user.password.size}")
    private String repeatPassword;

    public ResetPasswordRequestDTO() {}

    public ResetPasswordRequestDTO(String code, String password, String repeatPassword) {
        this.code = code;
        this.password = password;
        this.repeatPassword = repeatPassword;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String getRepeatPassword() {
        return repeatPassword;
    }

    public void setRepeatPassword(String repeatPassword) {
        this.repeatPassword = repeatPassword;
    }
}
