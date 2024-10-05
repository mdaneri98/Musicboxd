package ar.edu.itba.paw.models;

public enum VerificationType {
    VERIFY_EMAIL("check_email"),
    VERIFY_FORGOT_PASSWORD("forgot_password");

    private final String value;

    VerificationType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }

    // Método opcional para obtener el enum a partir de un string
    public static VerificationType fromString(String value) {
        for (VerificationType type : VerificationType.values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No enum constant with value " + value);
    }
}
