package ar.edu.itba.paw.services;

import ar.edu.itba.paw.User;

import javax.mail.MessagingException;

public interface EmailService {

    void sendVerificationForUser(User user) throws MessagingException;

}
