package ar.edu.itba.paw.persistence;


import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.VerificationType;

public interface UserVerificationDao {

    void startVerification(VerificationType type, User user, String code);
    Long verify(VerificationType type, String code);

}
