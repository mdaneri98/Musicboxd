package ar.edu.itba.paw.services.utils;

import ar.edu.itba.paw.infrastructure.jpa.UserJpaEntity;
import ar.edu.itba.paw.models.Comment;
import ar.edu.itba.paw.models.reviews.Review;
import ar.edu.itba.paw.models.Notification;

import java.time.LocalDateTime;

public class MergeUtils {

    private MergeUtils() {
        // throw new AssertionError("Utility class");
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
