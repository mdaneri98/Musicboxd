package ar.edu.itba.paw.services.utils;

import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.dtos.UserDTO;
import ar.edu.itba.paw.models.reviews.Review;
import ar.edu.itba.paw.models.dtos.ReviewDTO;

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
        setFieldIfNotNull(existingUser::setImageId, userDTO.getImageId());
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

    private static <T> void setFieldIfNotNull(java.util.function.Consumer<T> setter, T value) {
        if (value != null) {
            setter.accept(value);
        }
    }
}
