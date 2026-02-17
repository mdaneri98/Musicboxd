package ar.edu.itba.paw.webapp.utils;

import ar.edu.itba.paw.models.reviews.ReviewType;

/**
 * Utility class for controller methods
 */
public final class ControllerUtils {

    // Constants
    public static final Long FAVORITE_COUNT = 5L;
    public static final Integer FAVORITE_SIZE = 5;

    // Variables
    public static final String PAGE_PARAM_NAME = "page";
    public static final String SIZE_PARAM_NAME = "size";
    public static final String SEARCH_PARAM_NAME = "search";
    public static final String FILTER_PARAM_NAME = "filter";
    public static final String STATUS_PARAM_NAME = "status";
    public static final String ID_PARAM_NAME = "id";
    public static final String ARTIST_ID_PARAM_NAME = "artistId";
    public static final String ALBUM_ID_PARAM_NAME = "albumId";
    public static final String SONG_ID_PARAM_NAME = "songId";
    public static final String USER_ID_PARAM_NAME = "userId";
    public static final String TARGET_USER_ID_PARAM_NAME = "targetUserId";


    // Default values
    public static final Integer FIRST_PAGE = 1;
    public static final Integer DEFAULT_SIZE = 20;
    public static final String FIRST_PAGE_STRING = "1";
    public static final String DEFAULT_SIZE_STRING = "20";
    public static final String FIRST_FILTER_STRING = "FIRST";
    public static final String POPULAR_FILTER_STRING = "POPULAR";
    public static final String DEFAULT_STATUS_STRING = "ALL";
    public static final String READ_STATUS_STRING = "READ";
    public static final String UNREAD_STATUS_STRING = "UNREAD";
    public static final String ALL_STATUS_STRING = "ALL";

    // Item types
    public static final String ITEM_TYPE_SONG = ReviewType.SONG.toString();
    public static final String ITEM_TYPE_ALBUM = ReviewType.ALBUM.toString();
    public static final String ITEM_TYPE_ARTIST = ReviewType.ARTIST.toString();
    public static final String ITEM_TYPE_REVIEW = "Review";
    public static final String ITEM_TYPE_COMMENT = "Comment";
    public static final String ITEM_TYPE_IMAGE = "Image";
    public static final String ITEM_TYPE_USER = "User";

    // Auth actions
    public static final String ACTION_LOGIN = "Login";
    public static final String ACTION_REGISTER = "Register";
    public static final String ACTION_REFRESH = "Refresh";
    public static final String ACTION_LOGOUT = "Logout";

    // Methods
    public static final String METHOD_GET = "GET";
    public static final String METHOD_POST = "POST";
    public static final String METHOD_PUT = "PUT";
    public static final String METHOD_DELETE = "DELETE";
    public static final String METHOD_PATCH = "PATCH";

    // Relations
    public static final String RELATION_ARTIST = "artist";
    public static final String RELATION_ALBUM = "album";
    public static final String RELATION_ALBUMS = "albums";
    public static final String RELATION_SONGS = "songs";
    public static final String RELATION_REVIEW = "review";
    public static final String RELATION_REVIEWS = "reviews";
    public static final String RELATION_COMMENTS = "comments";
    public static final String RELATION_LIKES = "likes";
    public static final String RELATION_BLOCK = "block";
    public static final String RELATION_FAVORITE = "favorite";
    public static final String RELATION_FOLLOWERS = "followers";
    public static final String RELATION_FOLLOWINGS = "followings";
    public static final String RELATION_FAVORITE_ARTISTS = "favorite-artists";
    public static final String RELATION_FAVORITE_ALBUMS = "favorite-albums";
    public static final String RELATION_FAVORITE_SONGS = "favorite-songs";
    public static final String RELATION_READ = "read";
    public static final String RELATION_READ_ALL = "read-all";
    public static final String RELATION_UNREAD_COUNT = "unread-count";
    public static final String RELATION_IMAGE = "image";
    public static final String RELATION_TRIGGER_USER = "trigger-user";
    public static final String RELATION_RECIPIENT_USER = "recipient-user";
    public static final String RELATION_PAGINATED_FIRST = "first";
    public static final String RELATION_PAGINATED_PREV = "prev";
    public static final String RELATION_PAGINATED_NEXT = "next";
    public static final String RELATION_PAGINATED_LAST = "last";
    public static final String RELATION_ITEM = "item";

    private ControllerUtils() {
        throw new AssertionError("Cannot instantiate utility class");
    }
}
