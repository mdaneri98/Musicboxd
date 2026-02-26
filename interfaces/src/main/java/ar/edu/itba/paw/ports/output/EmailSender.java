package ar.edu.itba.paw.ports.output;

import ar.edu.itba.paw.models.Notification;
import ar.edu.itba.paw.models.ReviewAcknowledgementType;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.VerificationType;

import javax.mail.MessagingException;

/**
 * Output port for email sending infrastructure.
 * This interface represents the contract that the domain/application layer expects
 * from the email infrastructure adapter.
 *
 * Part of the Hexagonal Architecture migration (Phase 1).
 */
public interface EmailSender {

    /**
     * Sends a verification email to a user.
     *
     * @param type The type of verification (email, password reset, etc.)
     * @param to The recipient user
     * @param code The verification code
     * @return Void
     * @throws MessagingException if email sending fails
     */
    Void sendVerification(VerificationType type, User to, String code) throws MessagingException;

    /**
     * Sends a review acknowledgement email (blocked/unblocked).
     *
     * @param type The type of acknowledgement (blocked or unblocked)
     * @param to The recipient user
     * @param reviewTitle The title of the review
     * @param reviewName The name of the reviewed item
     * @param reviewType The type of the reviewed item (album, song, artist)
     * @return Void
     * @throws MessagingException if email sending fails
     */
    Void sendReviewAcknowledgement(ReviewAcknowledgementType type, User to, String reviewTitle,
                                    String reviewName, String reviewType) throws MessagingException;

    /**
     * Sends a notification email to a user.
     *
     * @param type The type of notification (like, comment, follow, new review)
     * @param recipientUser The recipient of the notification
     * @param triggerUser The user who triggered the notification
     * @param reviewId The ID of the review (if applicable)
     * @param reviewTitle The title of the review (if applicable)
     * @param itemName The name of the item (album, song, artist)
     * @param itemType The type of the item
     * @param rating The rating given (if applicable)
     * @return Void
     * @throws MessagingException if email sending fails
     */
    Void sendNotificationEmail(Notification.NotificationType type, User recipientUser, User triggerUser,
                                Long reviewId, String reviewTitle, String itemName, String itemType,
                                Integer rating) throws MessagingException;
}
