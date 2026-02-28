package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.domain.user.User;
import ar.edu.itba.paw.domain.user.UserId;
import ar.edu.itba.paw.domain.user.UserRepository;
import ar.edu.itba.paw.infrastructure.jpa.UserJpaEntity;
import ar.edu.itba.paw.models.UserVerification;
import ar.edu.itba.paw.models.VerificationType;
import ar.edu.itba.paw.persistence.mappers.LegacyUserMapper;
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
    private UserRepository userRepository;

    private final LegacyUserMapper userMapper = new LegacyUserMapper();


    @Override
    public Void startVerification(VerificationType type, User user, String code) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expireDateTime = now.plusHours(6);

        Optional<User> optionalUser = userRepository.findById(user.getId());
        if (optionalUser.isPresent()) {
            UserJpaEntity userEntity = userMapper.toJpaEntity(user);
            final UserVerification vf = new UserVerification();
            vf.setCode(code);
            vf.setUser(userEntity);
            vf.setExpireDate(expireDateTime);
            vf.setVerificationType(type.toString());

            em.persist(vf);
        }
        return null;
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
                UserJpaEntity userEntity = verification.getUser();
                userEntity.setVerified(true);
                em.merge(userEntity);

                em.remove(verification);
                return userEntity.getId();
            }
        } catch (NoResultException e) {
            return 0L;
        }
        return 0L;
    }




}

