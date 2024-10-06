package ar.edu.itba.paw.webapp.form.validation;

public class EmailAlreadyInUse {

    /**
     * Validates that an email is not already in use by a user. Returns true if there is a registered user, with said email
     * and a non-null password, in the database.
     * <br>Can be applied to: String.
     */
    @Constraint(validatedBy = EmailNotInUseValidator.class)
    @Target({ElementType.FIELD, ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface EmailNotInUse {
        String message() default "{EmailNotInUse.email}";

        Class<?>[] groups() default {};

        Class<? extends Payload>[] payload() default {};
    }

}
