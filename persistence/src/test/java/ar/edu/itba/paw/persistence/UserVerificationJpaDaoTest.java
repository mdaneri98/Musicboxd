package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.persistence.config.TestConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Sql(scripts = "classpath:userVerification_setUp.sql")
@Transactional
@Rollback
public class UserVerificationJpaDaoTest {

    private static final long PRE_EXISTING_USER_ID = 200;
    private static final long PRE_EXISTING_USER_2_ID = 201;
    private static final long PRE_EXISTING_USER_3_ID = 202;
    private static final String VALID_EMAIL_CODE = "valid_code_1";
    private static final String VALID_PASSWORD_CODE = "valid_code_2";
    private static final String EXPIRED_CODE = "expired_code";
    private static final String NEW_CODE = "new_verification_code";

    @Autowired
    private UserVerificationDao userVerificationDao;

    @Autowired
    private UserDao userDao;

    @PersistenceContext
    private EntityManager em;

    @Test
    public void test_startVerification_NewEmailVerification() {
        // 1. Pre-conditions
        User user = em.find(User.class, PRE_EXISTING_USER_ID);

        // Clear any existing verifications for this user
        em.createQuery("DELETE FROM UserVerification v WHERE v.user.id = :userId")
                .setParameter("userId", PRE_EXISTING_USER_ID)
                .executeUpdate();
        em.flush();

        // 2. Execute
        userVerificationDao.startVerification(VerificationType.VERIFY_EMAIL, user, NEW_CODE);

        // 3. Post-conditions
        TypedQuery<UserVerification> query = em.createQuery(
                "FROM UserVerification v WHERE v.user.id = :userId AND v.code = :code",
                UserVerification.class);
        query.setParameter("userId", PRE_EXISTING_USER_ID);
        query.setParameter("code", NEW_CODE);

        UserVerification verification = query.getSingleResult();
        assertNotNull(verification);
        assertEquals(NEW_CODE, verification.getCode());
        assertEquals("check_email", verification.getVerificationType());
        assertEquals(user.getId(), verification.getUser().getId());

        // Verify expiration date is approximately 6 hours in the future
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expireDate = verification.getExpireDate();
        assertTrue(expireDate.isAfter(now.plusHours(5)));
        assertTrue(expireDate.isBefore(now.plusHours(7)));
    }

    @Test
    public void test_startVerification_NewPasswordReset() {
        // 1. Pre-conditions
        User user = em.find(User.class, PRE_EXISTING_USER_ID);

        // 2. Execute
        userVerificationDao.startVerification(VerificationType.VERIFY_FORGOT_PASSWORD, user, NEW_CODE);

        // 3. Post-conditions
        TypedQuery<UserVerification> query = em.createQuery(
                "FROM UserVerification v WHERE v.user.id = :userId AND v.code = :code",
                UserVerification.class);
        query.setParameter("userId", PRE_EXISTING_USER_ID);
        query.setParameter("code", NEW_CODE);

        UserVerification verification = query.getSingleResult();
        assertNotNull(verification);
        assertEquals(NEW_CODE, verification.getCode());
        assertEquals("forgot_password", verification.getVerificationType());
    }

    @Test
    public void test_startVerification_UserNotFound() {
        // 1. Pre-conditions
        User nonExistentUser = new User();
        nonExistentUser.setId(999L);

        // 2. Execute
        userVerificationDao.startVerification(VerificationType.VERIFY_EMAIL, nonExistentUser, NEW_CODE);

        // 3. Post-conditions
        TypedQuery<UserVerification> query = em.createQuery(
                "FROM UserVerification v WHERE v.code = :code",
                UserVerification.class);
        query.setParameter("code", NEW_CODE);

        List<UserVerification> results = query.getResultList();
        assertTrue(results.isEmpty());
    }

    @Test
    public void test_verify_ValidEmailCode() {
        // 1. Pre-conditions
        User user = em.find(User.class, PRE_EXISTING_USER_ID);
        assertFalse(user.isVerified());

        // 2. Execute
        Long userId = userVerificationDao.verify(VerificationType.VERIFY_EMAIL, VALID_EMAIL_CODE);

        // 3. Post-conditions
        assertEquals(PRE_EXISTING_USER_ID, userId.longValue());

        // Verify user is now verified
        em.flush();
        em.clear();
        User verifiedUser = em.find(User.class, PRE_EXISTING_USER_ID);
        assertTrue(verifiedUser.isVerified());

        // Verify verification record was deleted
        TypedQuery<UserVerification> query = em.createQuery(
                "FROM UserVerification v WHERE v.code = :code",
                UserVerification.class);
        query.setParameter("code", VALID_EMAIL_CODE);
        List<UserVerification> results = query.getResultList();
        assertTrue(results.isEmpty());
    }

    @Test
    public void test_verify_ValidPasswordCode() {
        // 1. Pre-conditions
        User user = em.find(User.class, PRE_EXISTING_USER_2_ID);

        // 2. Execute
        Long userId = userVerificationDao.verify(VerificationType.VERIFY_FORGOT_PASSWORD, VALID_PASSWORD_CODE);

        // 3. Post-conditions
        assertEquals(PRE_EXISTING_USER_2_ID, userId.longValue());

        // Verify verification record was deleted
        TypedQuery<UserVerification> query = em.createQuery(
                "FROM UserVerification v WHERE v.code = :code",
                UserVerification.class);
        query.setParameter("code", VALID_PASSWORD_CODE);
        List<UserVerification> results = query.getResultList();
        assertTrue(results.isEmpty());
    }

    @Test
    public void test_verify_ExpiredCode() {
        // 1. Pre-conditions
        User user = em.find(User.class, PRE_EXISTING_USER_3_ID);
        assertFalse(user.isVerified());

        // 2. Execute
        Long userId = userVerificationDao.verify(VerificationType.VERIFY_EMAIL, EXPIRED_CODE);

        // 3. Post-conditions
        assertEquals(0L, userId.longValue());

        // Verify user is still not verified
        em.flush();
        em.clear();
        User nonVerifiedUser = em.find(User.class, PRE_EXISTING_USER_3_ID);
        assertFalse(nonVerifiedUser.isVerified());

        // Verify expired verification record still exists
        TypedQuery<UserVerification> query = em.createQuery(
                "FROM UserVerification v WHERE v.code = :code",
                UserVerification.class);
        query.setParameter("code", EXPIRED_CODE);
        List<UserVerification> results = query.getResultList();
        assertFalse(results.isEmpty());
    }

    @Test
    public void test_verify_InvalidCode() {
        // 1. Pre-conditions
        String invalidCode = "invalid_code";

        // 2. Execute
        Long userId = userVerificationDao.verify(VerificationType.VERIFY_EMAIL, invalidCode);

        // 3. Post-conditions
        assertEquals(0L, userId.longValue());
    }

    @Test
    public void test_verify_WrongVerificationType() {
        // 1. Pre-conditions
        // VALID_PASSWORD_CODE is for password reset

        // 2. Execute
        Long userId = userVerificationDao.verify(VerificationType.VERIFY_EMAIL, VALID_PASSWORD_CODE);

        // 3. Post-conditions
        assertEquals(0L, userId.longValue());

        // Verify user is still not verified
        em.flush();
        em.clear();
        User nonVerifiedUser = em.find(User.class, PRE_EXISTING_USER_2_ID);
        assertFalse(nonVerifiedUser.isVerified());
    }
}
