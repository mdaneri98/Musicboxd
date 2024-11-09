package ar.edu.itba.paw.models;

public enum FilterType {
    RATING("avg_rating", "DESC"),
    POPULAR("rating_amount", "DESC"),
    LIKES("likes", "DESC"),
    NEWEST("release_date", "DESC"),
    OLDEST("release_date", "ASC"),
    RECENT("created_at", "DESC"),
    FIRST("created_at", "ASC");

    private final String criteria;
    private final String order;

    // Constructor para inicializar el String asociado
    FilterType(String criteria, String order) {
        this.criteria = criteria;
        this.order = order;
    }

    // Método para obtener el String asociado
    public String getFilter() {
        return " ORDER BY " + criteria + " " + order + " ";
    }
}
