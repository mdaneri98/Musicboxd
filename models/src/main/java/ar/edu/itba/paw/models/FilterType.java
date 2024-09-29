package ar.edu.itba.paw.models;

public enum FilterType {
    RATING("avg_rating"),
    //LIKES("likes"),
    NEWEST("created_at"),
    OLDEST("created_at");

    private final String displayName;

    // Constructor para inicializar el String asociado
    FilterType(String displayName) {
        this.displayName = displayName;
    }

    // Método para obtener el String asociado
    public String getDisplayName() {
        return displayName;
    }
}
