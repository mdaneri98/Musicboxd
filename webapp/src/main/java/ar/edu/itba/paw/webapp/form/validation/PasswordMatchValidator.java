package ar.edu.itba.paw.webapp.form.validation;

import ar.edu.itba.paw.webapp.form.UserForm;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordMatchValidator implements ConstraintValidator<PasswordMatch, UserForm> {

    @Override
    public boolean isValid(UserForm userForm, ConstraintValidatorContext context) {
        if (userForm.getPassword() == null || userForm.getRepeatPassword() == null) {
            return false;
        }
        return userForm.getPassword().equals(userForm.getRepeatPassword());
    }
}

