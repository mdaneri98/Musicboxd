package ar.edu.itba.paw.persistence;


import ar.edu.itba.paw.domain.user.User;
import ar.edu.itba.paw.models.VerificationType;

public interface UserVerificationDao {

    Void startVerification(VerificationType type, User user, String code);
    Long verify(VerificationType type, String code);

}
