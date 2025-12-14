package ar.edu.itba.paw.webapp.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

/**
 * DTO for requesting a new verification email.
 * Used when user needs to resend the verification email.
 */
public class ResendVerificationRequestDTO {

    @NotBlank(message = "{verification.email.NotBlank}")
    @Email(message = "{verification.email.Email}")
    private String email;

    public ResendVerificationRequestDTO() {}

    public ResendVerificationRequestDTO(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
