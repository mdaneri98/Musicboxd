package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.VerificationType;

import javax.mail.MessagingException;

public interface EmailService {

    void sendVerification(VerificationType type, User to, String code) throws MessagingException;

}
