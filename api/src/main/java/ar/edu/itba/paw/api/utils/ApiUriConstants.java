package ar.edu.itba.paw.api.utils;

public class ApiUriConstants {

    /* Use at class level path */
    public static final String EMPTY = "";

    /* Base */
    public static final String API_BASE = EMPTY + "/api";
    public static final String ID = "/{" + ControllerUtils.ID_PARAM_NAME + ":\\d+}";

    /* AUTH */
    public static final String AUTH_BASE = API_BASE + "/auth";
    public static final String LOGIN = "/login";
    public static final String REGISTER = "/register";
    public static final String LOGOUT = "/logout";
    public static final String REFRESH = "/refresh";
    public static final String ME = "/me";

    /* USERS */
    public static final String USERS_BASE = API_BASE + "/users";
    public static final String USER_REVIEWS = ID + "/reviews";
    public static final String USER_FOLLOWERS = ID + "/followers";
    public static final String USER_FOLLOWINGS = ID + "/followings";
    public static final String USER_FAVORITE_ARTISTS = ID + "/favorite-artists";
    public static final String USER_FAVORITE_ALBUMS = ID + "/favorite-albums";
    public static final String USER_FAVORITE_SONGS = ID + "/favorite-songs";

    /* ARTISTS */
    public static final String ARTISTS_BASE = API_BASE + "/artists";
    public static final String ARTIST_REVIEWS = ID + "/reviews";
    public static final String ARTIST_ALBUMS = ID + "/albums";
    public static final String ARTIST_SONGS = ID + "/songs";
    public static final String ARTIST_FAVORITE = ID + "/favorite";

    /* ALBUMS */
    public static final String ALBUMS_BASE = API_BASE + "/albums";
    public static final String ALBUM_REVIEWS = ID + "/reviews";
    public static final String ALBUM_SONGS = ID + "/songs";
    public static final String ALBUM_FAVORITE = ID + "/favorite";

    /* SONGS */
    public static final String SONGS_BASE = API_BASE + "/songs";
    public static final String SONG_REVIEWS = ID + "/reviews";
    public static final String SONG_FAVORITE = ID + "/favorite";

    /* REVIEWS */
    public static final String REVIEWS_BASE = API_BASE + "/reviews";
    public static final String REVIEW_COMMENTS = ID + "/comments";
    public static final String REVIEW_LIKES = ID + "/likes";
    public static final String REVIEW_BLOCK = ID + "/block";
    public static final String REVIEW_UNBLOCK = ID + "/unblock";

    /* COMMENTS */
    public static final String COMMENTS_BASE = API_BASE + "/comments";

    /* IMAGES */
    public static final String IMAGES_BASE = API_BASE + "/images";

    /* NOTIFICATIONS */
    public static final String NOTIFICATIONS_BASE = API_BASE + "/notifications";
    public static final String NOTIFICATION_READ = ID + "/read";
    public static final String NOTIFICATIONS_READ_ALL = "/read-all";
    public static final String NOTIFICATIONS_UNREAD_COUNT = "/unread-count";

}
