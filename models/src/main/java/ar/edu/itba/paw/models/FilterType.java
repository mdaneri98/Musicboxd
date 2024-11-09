package ar.edu.itba.paw.models;

public enum FilterType {
    RATING("avgRating", "DESC"),
    POPULAR("ratingCount", "DESC"),
    LIKES("likes", "DESC"),
    NEWEST("createdAt", "DESC"),
    OLDEST("createdAt", "ASC");

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
