package ar.edu.itba.paw.api.utils;

import ar.edu.itba.paw.api.models.links.managers.CollectionLinkManager;
import ar.edu.itba.paw.models.reviews.ReviewType;

import javax.ws.rs.core.*;
import java.util.function.Supplier;

/**
 * Utility class for controller methods
 */
public final class ControllerUtils {

    // Cache max-age constants (in seconds)
    public static final int IMAGE_MAX_AGE = 2592000; // 30 days
    public static final int DATA_MAX_AGE = 3600; // 1 hour

    // Constants
    public static final Long FAVORITE_COUNT = 5L;
    public static final Integer FAVORITE_SIZE = 5;

    // Variables
    public static final String PAGE_PARAM_NAME = "page";
    public static final String SIZE_PARAM_NAME = "size";
    public static final String SEARCH_PARAM_NAME = "search";
    public static final String FILTER_PARAM_NAME = "filter";
    public static final String ID_PARAM_NAME = "id";

    // Default values
    public static final Integer FIRST_PAGE = 1;
    public static final Integer DEFAULT_SIZE = 20;
    public static final String FIRST_PAGE_STRING = "1";
    public static final String DEFAULT_SIZE_STRING = "20";
    public static final String FIRST_FILTER_STRING = "FIRST";
    public static final String POPULAR_FILTER_STRING = "POPULAR";

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


    // User endpoints
    public static final CollectionLinkManager usersCollectionLinks = new CollectionLinkManager(true, false, false, true, true);
    public static final CollectionLinkManager followingsCollectionLinks = new CollectionLinkManager(false, false, false, false, true);
    public static final CollectionLinkManager followersCollectionLinks = new CollectionLinkManager(true, true, false, false, true);
    public static final CollectionLinkManager userReviewsCollectionLinks = new CollectionLinkManager(false, false, false, false, true);
    public static final CollectionLinkManager userFavoriteCollectionLinks = new CollectionLinkManager(false, false, false, false, false);

    // Song endpoints
    public static final CollectionLinkManager songsCollectionLinks = new CollectionLinkManager(true, false, false, true, true);

    // Album endpoints
    public static final CollectionLinkManager albumsCollectionLinks = new CollectionLinkManager(true, false, false, true, true);
    public static final CollectionLinkManager albumSongsCollectionLinks = new CollectionLinkManager(true, false, false, false, true);


    // Artist endpoints
    public static final CollectionLinkManager artistsCollectionLinks = new CollectionLinkManager(true, false, false, true, true);
    public static final CollectionLinkManager artistAlbumsCollectionLinks = new CollectionLinkManager(true, false, false, false, true);
    public static final CollectionLinkManager artistSongsCollectionLinks = new CollectionLinkManager(false, false, false, false, true);

    // Review endpoints
    public static final CollectionLinkManager reviewsCollectionLinks = new CollectionLinkManager(true, false, false, true, true);
    public static final CollectionLinkManager commentsCollectionLinks = new CollectionLinkManager(true, false, false, true, true);
    public static final CollectionLinkManager likesCollectionLinks = new CollectionLinkManager(true, true, false, false, true);
    public static final CollectionLinkManager itemReviewsCollectionLinks = new CollectionLinkManager(true, false, false, false, true);

    // Notification endpoints
    public static final CollectionLinkManager notificationsCollectionLinks = new CollectionLinkManager(true, false, false, false, true);

}
