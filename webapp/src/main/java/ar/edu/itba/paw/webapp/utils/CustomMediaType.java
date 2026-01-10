package ar.edu.itba.paw.webapp.utils;


public final class CustomMediaType {

    // User operations
    public static final String USER = "application/vnd.musicboxd.user.v1+json";
    public static final String USER_PASSWORD = "application/vnd.musicboxd.user.password.v1+json";
    public static final String USER_VERIFICATION = "application/vnd.musicboxd.user.verification.v1+json";

    // Other resources (for future use)
    public static final String ARTIST = "application/vnd.musicboxd.artist.v1+json";
    public static final String ALBUM = "application/vnd.musicboxd.album.v1+json";
    public static final String SONG = "application/vnd.musicboxd.song.v1+json";
    public static final String REVIEW = "application/vnd.musicboxd.review.v1+json";
    public static final String COMMENT = "application/vnd.musicboxd.comment.v1+json";
    public static final String NOTIFICATION = "application/vnd.musicboxd.notification.v1+json";
    public static final String IMAGE = "application/vnd.musicboxd.image.v1+json";

    private CustomMediaType() {
        throw new AssertionError("Cannot instantiate utility class");
    }
}
