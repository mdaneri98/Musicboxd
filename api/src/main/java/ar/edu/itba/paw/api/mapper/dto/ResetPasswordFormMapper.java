package ar.edu.itba.paw.api.mapper.dto;

import ar.edu.itba.paw.api.form.ResetPasswordForm;
import org.springframework.stereotype.Component;

/**
 * Mapper to extract data from ResetPasswordForm
 * Note: ResetPasswordForm doesn't map to a specific DTO, but this mapper
 * provides utility methods to extract validated data from the form
 */
@Component
public class ResetPasswordFormMapper {

    /**
     * Extracts the verification code from the form
     */
    public String getCode(ResetPasswordForm form) {
        return form != null ? form.getCode() : null;
    }

    /**
     * Extracts the new password from the form
     */
    public String getPassword(ResetPasswordForm form) {
        return form != null ? form.getPassword() : null;
    }
}

