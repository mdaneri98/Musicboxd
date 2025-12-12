package ar.edu.itba.paw.api.dto;

import javax.validation.constraints.NotBlank;

/**
 * DTO for email verification request.
 * Used when user submits a verification code to verify their email.
 */
public class EmailVerificationRequestDTO {

    @NotBlank(message = "{verification.code.NotBlank}")
    private String code;

    public EmailVerificationRequestDTO() {}

    public EmailVerificationRequestDTO(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
