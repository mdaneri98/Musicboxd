package ar.edu.itba.paw.api.utils;

public class ApiUriConstants {
    
    public static final String API_BASE = "/api";
    public static final String USERS_BASE = API_BASE + "/users";
    public static final String ARTISTS_BASE = API_BASE + "/artists";
    public static final String ALBUMS_BASE = API_BASE + "/albums";
    public static final String SONGS_BASE = API_BASE + "/songs";
    public static final String REVIEWS_BASE = API_BASE + "/reviews";
    public static final String NOTIFICATIONS_BASE = API_BASE + "/notifications";
    public static final String AUTH_BASE = API_BASE + "/auth";
    
    public static final String USER_BY_ID = USERS_BASE + "/{id}";
    public static final String USER_REVIEWS = USER_BY_ID + "/reviews";
    public static final String USER_FOLLOWERS = USER_BY_ID + "/followers";
    public static final String USER_FOLLOWING = USER_BY_ID + "/following";
    
    public static final String ARTIST_BY_ID = ARTISTS_BASE + "/{id}";
    public static final String ALBUM_BY_ID = ALBUMS_BASE + "/{id}";
    public static final String SONG_BY_ID = SONGS_BASE + "/{id}";
    public static final String REVIEW_BY_ID = REVIEWS_BASE + "/{id}";
    
    public static final String LOGIN = AUTH_BASE + "/login";
    public static final String REGISTER = AUTH_BASE + "/register";
    public static final String LOGOUT = AUTH_BASE + "/logout";
}
