package ar.edu.itba.paw.webapp.dto;

/**
 * DTO for email verification response.
 * Returned when email verification is successful.
 */
public class EmailVerificationResponseDTO {

    private Long userId;
    private String message;
    private boolean verified;

    public EmailVerificationResponseDTO() {}

    public EmailVerificationResponseDTO(Long userId, String message, boolean verified) {
        this.userId = userId;
        this.message = message;
        this.verified = verified;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }
}
