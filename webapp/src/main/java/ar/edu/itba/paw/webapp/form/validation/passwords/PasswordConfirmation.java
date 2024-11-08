package ar.edu.itba.paw.webapp.form.validation.passwords;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public interface PasswordConfirmation {
    String getPassword();
    String getRepeatPassword();
}
