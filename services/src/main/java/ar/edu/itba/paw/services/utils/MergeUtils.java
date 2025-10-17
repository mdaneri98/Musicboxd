package ar.edu.itba.paw.services.utils;

import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.dtos.CommentDTO;
import ar.edu.itba.paw.models.Comment;
import ar.edu.itba.paw.models.dtos.UserDTO;
import ar.edu.itba.paw.models.reviews.Review;
import ar.edu.itba.paw.models.dtos.ReviewDTO;
import ar.edu.itba.paw.models.dtos.ArtistDTO;
import ar.edu.itba.paw.models.Artist;
import ar.edu.itba.paw.models.Album;
import ar.edu.itba.paw.models.dtos.AlbumDTO;
import ar.edu.itba.paw.models.Song;
import ar.edu.itba.paw.models.dtos.SongDTO;
import ar.edu.itba.paw.models.Notification;
import ar.edu.itba.paw.models.dtos.NotificationDTO;

import java.time.LocalDateTime;

public class MergeUtils {
    
    private MergeUtils() {
        // throw new AssertionError("Utility class");
    }

    // User fields merge
    public static void mergeUserFields(User existingUser, UserDTO userDTO) {
        mergeBasicFields(existingUser, userDTO);
        mergeNotificationSettings(existingUser, userDTO);
        existingUser.setUpdatedAt(LocalDateTime.now());
    }

    private static void mergeBasicFields(User existingUser, UserDTO userDTO) {
        setFieldIfNotNull(existingUser::setUsername, userDTO.getUsername());
        setFieldIfNotNull(existingUser::setEmail, userDTO.getEmail());
        setFieldIfNotNull(existingUser::setName, userDTO.getName());
        setFieldIfNotNull(existingUser::setBio, userDTO.getBio());
        setFieldIfNotNull(existingUser::setPreferredLanguage, userDTO.getPreferredLanguage());
        setFieldIfNotNull(existingUser::setTheme, userDTO.getPreferredTheme());
    }

    private static void mergeNotificationSettings(User existingUser, UserDTO userDTO) {
        setFieldIfNotNull(existingUser::setFollowNotificationsEnabled, userDTO.getHasFollowNotificationsEnabled());
        setFieldIfNotNull(existingUser::setLikeNotificationsEnabled, userDTO.getHasLikeNotificationsEnabled());
        setFieldIfNotNull(existingUser::setCommentNotificationsEnabled, userDTO.getHasCommentsNotificationsEnabled());
        setFieldIfNotNull(existingUser::setReviewNotificationsEnabled, userDTO.getHasReviewsNotificationsEnabled());
    }

    // Review fields merge
    public static void mergeReviewFields(Review existingReview, ReviewDTO reviewDTO) {
        mergeBasicFields(existingReview, reviewDTO);
        existingReview.setCreatedAt(LocalDateTime.now());
    }

    private static void mergeBasicFields(Review existingReview, ReviewDTO reviewDTO) {
        setFieldIfNotNull(existingReview::setTitle, reviewDTO.getTitle());
        setFieldIfNotNull(existingReview::setDescription, reviewDTO.getDescription());
        setFieldIfNotNull(existingReview::setRating, reviewDTO.getRating());
        setFieldIfNotNull(existingReview::setCreatedAt, reviewDTO.getCreatedAt());
        setFieldIfNotNull(existingReview::setLikes, reviewDTO.getLikes());
        setFieldIfNotNull(existingReview::setBlocked, reviewDTO.getIsBlocked());
        setFieldIfNotNull(existingReview::setCommentAmount, reviewDTO.getCommentAmount());
    }

    // Comment fields merge
    public static void mergeCommentFields(Comment existingComment, CommentDTO commentDTO) {
        mergeBasicFields(existingComment, commentDTO);
        existingComment.setCreatedAt(LocalDateTime.now());
    }

    private static void mergeBasicFields(Comment existingComment, CommentDTO commentDTO) {
        setFieldIfNotNull(existingComment::setContent, commentDTO.getContent());
    }

    private static <T> void setFieldIfNotNull(java.util.function.Consumer<T> setter, T value) {
        if (value != null) {
            setter.accept(value);
        }
    }

    // Artist fields merge
    public static void mergeArtistFields(Artist existingArtist, ArtistDTO artistDTO) {
        mergeBasicFields(existingArtist, artistDTO);
        existingArtist.setUpdatedAt(LocalDateTime.now());
    }

    private static void mergeBasicFields(Artist existingArtist, ArtistDTO artistDTO) {
        setFieldIfNotNull(existingArtist::setName, artistDTO.getName());
        setFieldIfNotNull(existingArtist::setBio, artistDTO.getBio());
    }

    // Album fields merge
    public static void mergeAlbumFields(Album existingAlbum, AlbumDTO albumDTO) {
        mergeBasicFields(existingAlbum, albumDTO);
        existingAlbum.setUpdatedAt(LocalDateTime.now());
    }

    private static void mergeBasicFields(Album existingAlbum, AlbumDTO albumDTO) {
        setFieldIfNotNull(existingAlbum::setTitle, albumDTO.getTitle());
        setFieldIfNotNull(existingAlbum::setGenre, albumDTO.getGenre());
        setFieldIfNotNull(existingAlbum::setReleaseDate, albumDTO.getReleaseDate());
    }

    // Song fields merge
    public static void mergeSongFields(Song existingSong, SongDTO songDTO) {
        mergeBasicFields(existingSong, songDTO);
        existingSong.setUpdatedAt(LocalDateTime.now());
    }

    private static void mergeBasicFields(Song existingSong, SongDTO songDTO) {
        setFieldIfNotNull(existingSong::setTitle, songDTO.getTitle());
        setFieldIfNotNull(existingSong::setDuration, songDTO.getDuration());
        setFieldIfNotNull(existingSong::setTrackNumber, songDTO.getTrackNumber());
    }

    // Notification fields merge
    public static void mergeNotificationFields(Notification existingNotification, NotificationDTO notificationDTO) {
        mergeBasicFields(existingNotification, notificationDTO);
        existingNotification.setCreatedAt(LocalDateTime.now());
    }

    private static void mergeBasicFields(Notification existingNotification, NotificationDTO notificationDTO) {
        setFieldIfNotNull(existingNotification::setMessage, notificationDTO.getMessage());
        setFieldIfNotNull(existingNotification::setRead, notificationDTO.getIsRead());
    }
}
