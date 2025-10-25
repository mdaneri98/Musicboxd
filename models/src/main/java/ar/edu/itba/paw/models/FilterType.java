package ar.edu.itba.paw.models;

public enum FilterType {
    RATING("avg_rating", "DESC"),
    POPULAR("rating_amount", "DESC"),
    LIKES("likes", "DESC"),
    NEWEST("release_date", "DESC"),
    OLDEST("release_date", "ASC"),
    RECENT("created_at", "DESC"),
    FIRST("created_at", "ASC"),
    USERNAME("username", "ASC"),
    EMAIL("email", "ASC"),
    NAME("name", "ASC"),
    BIO("bio", "ASC"),
    IMAGE_ID("image_id", "ASC"),
    FOLLOWERS_AMOUNT("followers_amount", "ASC"),
    FOLLOWING_AMOUNT("following_amount", "ASC"),
    REVIEW_AMOUNT("review_amount", "ASC"),
    UPDATED_AT("updated_at", "ASC");

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
