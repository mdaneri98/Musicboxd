package ar.edu.itba.paw.services;

import javax.mail.MessagingException;

public interface EmailService {

    void sendVerification(String email, String code) throws MessagingException;

}
