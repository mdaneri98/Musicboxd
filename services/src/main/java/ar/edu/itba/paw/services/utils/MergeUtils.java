package ar.edu.itba.paw.services.utils;

import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.Comment;
import ar.edu.itba.paw.models.reviews.Review;
import ar.edu.itba.paw.models.Artist;
import ar.edu.itba.paw.models.Album;
import ar.edu.itba.paw.models.Song;
import ar.edu.itba.paw.models.Notification;

import java.time.LocalDateTime;

public class MergeUtils {
    
    private MergeUtils() {
        // throw new AssertionError("Utility class");
    }

    // User fields merge (Model to Model)
    public static void mergeUserFields(User existingUser, User userUpdate) {
        mergeBasicFields(existingUser, userUpdate);
        mergeNotificationSettings(existingUser, userUpdate);
        existingUser.setUpdatedAt(LocalDateTime.now());
    }

    private static void mergeBasicFields(User existingUser, User userUpdate) {
        setFieldIfNotNull(existingUser::setEmail, userUpdate.getEmail());
        setFieldIfNotNull(existingUser::setName, userUpdate.getName());
        setFieldIfNotNull(existingUser::setBio, userUpdate.getBio());
        setFieldIfNotNull(existingUser::setImageId, userUpdate.getImageId());
        setFieldIfNotNull(existingUser::setPreferredLanguage, userUpdate.getPreferredLanguage());
        setFieldIfNotNull(existingUser::setTheme, userUpdate.getTheme());
    }

    private static void mergeNotificationSettings(User existingUser, User userUpdate) {
        setFieldIfNotNull(existingUser::setFollowNotificationsEnabled, userUpdate.getFollowNotificationsEnabled());
        setFieldIfNotNull(existingUser::setLikeNotificationsEnabled, userUpdate.getLikeNotificationsEnabled());
        setFieldIfNotNull(existingUser::setCommentNotificationsEnabled, userUpdate.getCommentNotificationsEnabled());
        setFieldIfNotNull(existingUser::setReviewNotificationsEnabled, userUpdate.getReviewNotificationsEnabled());
    }

    // Review fields merge (Model to Model)
    public static void mergeReviewFields(Review existingReview, Review reviewUpdate) {
        mergeBasicFields(existingReview, reviewUpdate);
        existingReview.setCreatedAt(LocalDateTime.now());
    }

    private static void mergeBasicFields(Review existingReview, Review reviewUpdate) {
        setFieldIfNotNull(existingReview::setTitle, reviewUpdate.getTitle());
        setFieldIfNotNull(existingReview::setDescription, reviewUpdate.getDescription());
        setFieldIfNotNull(existingReview::setRating, reviewUpdate.getRating());
        setFieldIfNotNull(existingReview::setCreatedAt, reviewUpdate.getCreatedAt());
        setFieldIfNotNull(existingReview::setLikes, reviewUpdate.getLikes());
        setBooleanFieldIfNotNull(existingReview::setBlocked, reviewUpdate.isBlocked());
        setFieldIfNotNull(existingReview::setCommentAmount, reviewUpdate.getCommentAmount());
    }

    // Comment fields merge (Model to Model)
    public static void mergeCommentFields(Comment existingComment, Comment commentUpdate) {
        mergeBasicFields(existingComment, commentUpdate);
        existingComment.setCreatedAt(LocalDateTime.now());
    }

    private static void mergeBasicFields(Comment existingComment, Comment commentUpdate) {
        setFieldIfNotNull(existingComment::setContent, commentUpdate.getContent());
    }

    private static <T> void setFieldIfNotNull(java.util.function.Consumer<T> setter, T value) {
        if (value != null) {
            setter.accept(value);
        }
    }

    private static void setBooleanFieldIfNotNull(java.util.function.Consumer<Boolean> setter, boolean value) {
        setter.accept(value);
    }

    // Artist fields merge (Model to Model)
    public static void mergeArtistFields(Artist existingArtist, Artist artistUpdate) {
        mergeBasicFields(existingArtist, artistUpdate);
        existingArtist.setUpdatedAt(LocalDateTime.now());
    }

    private static void mergeBasicFields(Artist existingArtist, Artist artistUpdate) {
        setFieldIfNotNull(existingArtist::setName, artistUpdate.getName());
        setFieldIfNotNull(existingArtist::setBio, artistUpdate.getBio());
    }

    // Album fields merge (Model to Model)
    public static void mergeAlbumFields(Album existingAlbum, Album albumUpdate) {
        mergeBasicFields(existingAlbum, albumUpdate);
        existingAlbum.setUpdatedAt(LocalDateTime.now());
    }

    private static void mergeBasicFields(Album existingAlbum, Album albumUpdate) {
        setFieldIfNotNull(existingAlbum::setTitle, albumUpdate.getTitle());
        setFieldIfNotNull(existingAlbum::setGenre, albumUpdate.getGenre());
        setFieldIfNotNull(existingAlbum::setReleaseDate, albumUpdate.getReleaseDate());
    }

    // Song fields merge (Model to Model)
    public static void mergeSongFields(Song existingSong, Song songUpdate) {
        mergeBasicFields(existingSong, songUpdate);
        existingSong.setUpdatedAt(LocalDateTime.now());
    }

    private static void mergeBasicFields(Song existingSong, Song songUpdate) {
        setFieldIfNotNull(existingSong::setTitle, songUpdate.getTitle());
        setFieldIfNotNull(existingSong::setDuration, songUpdate.getDuration());
        setFieldIfNotNull(existingSong::setTrackNumber, songUpdate.getTrackNumber());
    }

    // Notification fields merge (Model to Model)
    public static void mergeNotificationFields(Notification existingNotification, Notification notificationUpdate) {
        mergeBasicFields(existingNotification, notificationUpdate);
        existingNotification.setCreatedAt(LocalDateTime.now());
    }

    private static void mergeBasicFields(Notification existingNotification, Notification notificationUpdate) {
        setFieldIfNotNull(existingNotification::setMessage, notificationUpdate.getMessage());
        setBooleanFieldIfNotNull(existingNotification::setRead, notificationUpdate.isRead());
    }
}
