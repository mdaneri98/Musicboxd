package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.UserVerification;
import ar.edu.itba.paw.models.VerificationType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Optional;

@Primary
@Repository
public class UserVerificationJpaDao implements UserVerificationDao {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private UserDao userDao;


    @Override
    public void startVerification(VerificationType type, User user, String code) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expireDateTime = now.plusHours(6);

        Optional<User> optionalUser = userDao.find(user.getId());
        if (optionalUser.isPresent()) {
            final UserVerification vf = new UserVerification();
            vf.setCode(code);
            vf.setUser(user);
            vf.setExpireDate(expireDateTime);
            vf.setVerificationType(type.toString());

            em.persist(vf);
        }
    }

    @Override
    public Long verify(VerificationType type, String code) {
        LocalDateTime now = LocalDateTime.now();

        TypedQuery<UserVerification> query = em.createQuery("FROM UserVerification v WHERE v.code = :code AND v.verificationType = :type", UserVerification.class);
        query.setParameter("code", code);
        query.setParameter("type", type.toString());

        try {
            UserVerification verification = query.getSingleResult();

            if (verification.getExpireDate().isAfter(now)) {
                User user = verification.getUser();
                user.setVerified(true);
                em.merge(user);  // Actualiza el usuario en la base de datos

                em.remove(verification);
                return user.getId();
            }
        } catch (NoResultException e) {
            // TODO: Manejar exception
        }
        return 0L;
    }




}

