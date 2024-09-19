package ar.edu.itba.paw.persistence;


import ar.edu.itba.paw.models.User;

public interface UserVerificationDao {

    int startVerification(User user, String code);
    boolean verify(String code);

}
