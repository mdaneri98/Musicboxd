package ar.edu.itba.paw.webapp.form.validation;

import ar.edu.itba.paw.webapp.form.ResetPasswordForm;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;


public class ResetPasswordMatchValidator implements ConstraintValidator<ResetPasswordMatch, ResetPasswordForm> {

    @Override
    public void initialize(ResetPasswordMatch constraintAnnotation) {}

    @Override
    public boolean isValid(ResetPasswordForm resetPasswordForm, ConstraintValidatorContext context) {
        if (resetPasswordForm.getPassword() == null || resetPasswordForm.getRepeatPassword() == null) {
            return false;
        }
        return resetPasswordForm.getPassword().equals(resetPasswordForm.getRepeatPassword());
    }

}

