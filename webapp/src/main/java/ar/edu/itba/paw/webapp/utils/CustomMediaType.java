package ar.edu.itba.paw.webapp.utils;


public final class CustomMediaType {

    // User operations
    public static final String USER = "application/vnd.musicboxd.user.v1+json";
    public static final String USER_LIST = "application/vnd.musicboxd.userList.v1+json";
    public static final String USER_PUBLIC = "application/vnd.musicboxd.user.public.v1+json";
    public static final String USER_PASSWORD = "application/vnd.musicboxd.user.password.v1+json";
    public static final String USER_VERIFICATION = "application/vnd.musicboxd.user.verification.v1+json";

    // Artist
    public static final String ARTIST = "application/vnd.musicboxd.artist.v1+json";
    public static final String ARTIST_LIST = "application/vnd.musicboxd.artistList.v1+json";
    public static final String ARTIST_PUBLIC = "application/vnd.musicboxd.artist.public.v1+json";

    // Album
    public static final String ALBUM = "application/vnd.musicboxd.album.v1+json";
    public static final String ALBUM_LIST = "application/vnd.musicboxd.albumList.v1+json";
    public static final String ALBUM_PUBLIC = "application/vnd.musicboxd.album.public.v1+json";

    // Song
    public static final String SONG = "application/vnd.musicboxd.song.v1+json";
    public static final String SONG_LIST = "application/vnd.musicboxd.songList.v1+json";
    public static final String SONG_PUBLIC = "application/vnd.musicboxd.song.public.v1+json";

    // Review
    public static final String REVIEW = "application/vnd.musicboxd.review.v1+json";
    public static final String REVIEW_LIST = "application/vnd.musicboxd.reviewList.v1+json";

    // Comment
    public static final String COMMENT = "application/vnd.musicboxd.comment.v1+json";
    public static final String COMMENT_LIST = "application/vnd.musicboxd.commentList.v1+json";

    // Notification
    public static final String NOTIFICATION = "application/vnd.musicboxd.notification.v1+json";
    public static final String NOTIFICATION_LIST = "application/vnd.musicboxd.notificationList.v1+json";

    // Other
    public static final String IMAGE = "application/vnd.musicboxd.image.v1+json";
public static final String API_INFO = "application/vnd.musicboxd.apiInfo.v1+json";
    public static final String ERROR = "application/vnd.musicboxd.error.v1+json";

    private CustomMediaType() {
        throw new AssertionError("Cannot instantiate utility class");
    }
}
