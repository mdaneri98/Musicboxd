package ar.edu.itba.paw.webapp.form.validation;

import ar.edu.itba.paw.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UsernameNotInUseValidator implements ConstraintValidator<UsernameNotInUse, String> {

    @Autowired
    private UserService userService;

    @Override
    public void initialize(UsernameNotInUse constraintAnnotation) {
    }

    @Override
    public boolean isValid(String username, ConstraintValidatorContext context) {
        if (username == null) {
            return true;
        }
        return !userService.usernameExists(username);
    }
}