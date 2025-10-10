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

    /* ARTISTS */
    public static final String ARTISTS_BASE = API_BASE + "/artists";
    public static final String ARTIST_BY_ID = "/{id}";
    public static final String ARTIST_REVIEWS = "/{id}/reviews";

    /* ALBUMS */
    public static final String ALBUMS_BASE = API_BASE + "/albums";
    public static final String ALBUM_BY_ID = "/{id}";
    public static final String ALBUM_REVIEWS = "/{id}/reviews";
    public static final String ALBUM_SONGS = "/{id}/songs";

    /* SONGS */
    public static final String SONGS_BASE = API_BASE + "/songs";
    public static final String SONG_BY_ID = "/{id}";
    public static final String SONG_REVIEWS = "/{id}/reviews";

    /* REVIEWS */
    public static final String REVIEWS_BASE = API_BASE + "/reviews";
    public static final String REVIEW_BY_ID = "/{id}";
    public static final String REVIEW_COMMENTS = "/{id}/comments";
    public static final String REVIEW_LIKES = "/{id}/likes";

    /* COMMENTS */
    public static final String COMMENTS_BASE = API_BASE + "/comments";
    public static final String COMMENT_BY_ID = "/{id}";

    /* IMAGES */
    public static final String IMAGES_BASE = API_BASE + "/images";
    public static final String IMAGE_BY_ID = "/{id}";

}
