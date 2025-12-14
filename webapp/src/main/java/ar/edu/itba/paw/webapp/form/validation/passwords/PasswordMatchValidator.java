package ar.edu.itba.paw.webapp.form.validation.passwords;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordMatchValidator implements ConstraintValidator<PasswordMatch, PasswordConfirmation> {

    @Override
    public void initialize(PasswordMatch constraintAnnotation) {}

    @Override
    public boolean isValid(PasswordConfirmation form, ConstraintValidatorContext context) {
        if (form == null) {
            return true;
        }

        String password = form.getPassword();
        String confirmPassword = form.getRepeatPassword();

        // If either is null, let @NotBlank handle it
        if (password == null || confirmPassword == null) {
            return true;
        }

        return password.equals(confirmPassword);
    }
}
