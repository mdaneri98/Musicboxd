package ar.edu.itba.paw.api.utils;

public class ApiUriConstants {

    /* Use at class level path */
    public static final String EMPTY = "";

    /* Base */
    public static final String API_BASE = EMPTY + "/api";

    /* AUTH */
    public static final String AUTH_BASE = API_BASE + "/auth";
    public static final String LOGIN = AUTH_BASE + "/login";
    public static final String REGISTER = AUTH_BASE + "/register";
    public static final String LOGOUT = AUTH_BASE + "/logout";

    /* USERS */
    public static final String USERS_BASE = API_BASE + "/users";
    public static final String USER_BY_ID = USERS_BASE + "/{id}";
    public static final String USER_REVIEWS = USER_BY_ID + "/reviews";
    public static final String USER_FOLLOWERS = USER_BY_ID + "/followers";
    public static final String USER_FOLLOWING = USER_BY_ID + "/following";

    /* ARTISTS */
    public static final String ARTISTS_BASE = API_BASE + "/artists";
    public static final String ARTIST_BY_ID = ARTISTS_BASE + "/{id:\\d+}";
    public static final String ARTIST_REVIEWS = ARTIST_BY_ID + "/reviews";

    /* ALBUMS */
    public static final String ALBUMS_BASE = API_BASE + "/albums";
    public static final String ALBUM_BY_ID = ALBUMS_BASE + "/{id:\\d+}";
    public static final String ALBUM_REVIEWS = ALBUM_BY_ID + "/reviews";
    public static final String ALBUM_SONGS = ALBUM_BY_ID + "/songs";

    /* SONGS */
    public static final String SONGS_BASE = API_BASE + "/songs";
    public static final String SONG_BY_ID = SONGS_BASE + "/{id:\\d+}";
    public static final String SONG_REVIEWS = SONG_BY_ID + "/reviews";

    /* REVIEWS */
    public static final String REVIEWS_BASE = API_BASE + "/reviews";
    public static final String REVIEW_BY_ID = REVIEWS_BASE + "/{id:\\d+}";
    public static final String REVIEW_COMMENTS = REVIEW_BY_ID + "/comments";
    public static final String REVIEW_LIKES = REVIEW_BY_ID + "/likes";

    /* COMMENTS */
    public static final String COMMENTS_BASE = API_BASE + "/comments";
    public static final String COMMENT_BY_ID = COMMENTS_BASE + "/{id:\\d+}";

    /* IMAGES */
    public static final String IMAGES_BASE = API_BASE + "/images";
    public static final String IMAGE_BY_ID = IMAGES_BASE + "/{id:\\d+}";

}
