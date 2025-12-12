package ar.edu.itba.paw.api.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

/**
 * DTO for forgot password request.
 * Used when user requests a password reset email.
 */
public class ForgotPasswordRequestDTO {

    @NotBlank(message = "{validation.user.email.notblank}")
    @Email(message = "{validation.user.email.invalid}")
    private String email;

    public ForgotPasswordRequestDTO() {}

    public ForgotPasswordRequestDTO(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
