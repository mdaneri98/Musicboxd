package ar.edu.itba.paw.webapp.utils;

public class ApiUriConstants {

        /* Use at class level path */
        public static final String EMPTY = "";

        /* Base */
        public static final String API_BASE = EMPTY;
        public static final String ID = "/{" + ControllerUtils.ID_PARAM_NAME + ":\\d+}";

        /* USERS */
        public static final String USERS_BASE = API_BASE + "/users";
        public static final String USER_REVIEWS = ID + "/reviews";
        public static final String USER_FOLLOWERS = ID + "/followers";
        public static final String USER_FOLLOWINGS = ID + "/followings";
        public static final String USER_FOLLOWING_DETAIL = USER_FOLLOWINGS + "/{"
                        + ControllerUtils.TARGET_USER_ID_PARAM_NAME + ":\\d+}";
        public static final String USER_FOLLOWING_REVIEWS = USER_FOLLOWINGS + "/reviews";
        public static final String USER_FAVORITES = ID + "/favorites";
        public static final String USER_FAVORITE_ARTISTS = USER_FAVORITES + "/artists";
        public static final String USER_FAVORITE_ALBUMS = USER_FAVORITES + "/albums";
        public static final String USER_FAVORITE_SONGS = USER_FAVORITES + "/songs";

        public static final String USER_FAVORITE_ARTIST_DETAIL = USER_FAVORITE_ARTISTS + "/{"
                        + ControllerUtils.ARTIST_ID_PARAM_NAME + ":\\d+}";
        public static final String USER_FAVORITE_ALBUM_DETAIL = USER_FAVORITE_ALBUMS + "/{"
                        + ControllerUtils.ALBUM_ID_PARAM_NAME + ":\\d+}";
        public static final String USER_FAVORITE_SONG_DETAIL = USER_FAVORITE_SONGS + "/{"
                        + ControllerUtils.SONG_ID_PARAM_NAME + ":\\d+}";

        public static final String USER_RECOMMENDED = ID + "/recommended";
        public static final String USER_NOTIFICATIONS = ID + "/notifications";
        public static final String USER_LIKES = ID + "/likes";

        /* ARTISTS */
        public static final String ARTISTS_BASE = API_BASE + "/artists";
        public static final String ARTIST_REVIEWS = ID + "/reviews";
        public static final String ARTIST_ALBUMS = ID + "/albums";
        public static final String ARTIST_SONGS = ID + "/songs";

        /* ALBUMS */
        public static final String ALBUMS_BASE = API_BASE + "/albums";
        public static final String ALBUM_REVIEWS = ID + "/reviews";
        public static final String ALBUM_SONGS = ID + "/songs";

        /* SONGS */
        public static final String SONGS_BASE = API_BASE + "/songs";
        public static final String SONG_REVIEWS = ID + "/reviews";

        /* REVIEWS */
        public static final String REVIEWS_BASE = API_BASE + "/reviews";
        public static final String REVIEW_COMMENTS = ID + "/comments";
        public static final String REVIEW_LIKES = ID + "/likes";
        public static final String REVIEW_LIKE_DETAIL = REVIEW_LIKES + "/{" + ControllerUtils.USER_ID_PARAM_NAME
                        + ":\\d+}";

        /* COMMENTS */
        public static final String COMMENTS_BASE = API_BASE + "/comments";

        /* IMAGES */
        public static final String IMAGES_BASE = API_BASE + "/images";

        /* NOTIFICATIONS */
        public static final String NOTIFICATIONS_BASE = API_BASE + "/notifications";

}
