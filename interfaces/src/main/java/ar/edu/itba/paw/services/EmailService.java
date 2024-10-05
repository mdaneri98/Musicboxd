package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.VerificationType;

import javax.mail.MessagingException;

public interface EmailService {

    void sendVerification(VerificationType type, String email, String code) throws MessagingException;

}
