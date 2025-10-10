package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.ReviewAcknowledgementType;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.VerificationType;


import javax.mail.MessagingException;

public interface EmailService {

    Void sendVerification(VerificationType type, User to, String code) throws MessagingException;
    Void sendReviewAcknowledgement(ReviewAcknowledgementType type, User to, String reviewTitle, String reviewName, String reviewType) throws MessagingException;

}
