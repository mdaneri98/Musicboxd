package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.RefreshToken;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public class RefreshTokenJpaDao implements RefreshTokenDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public RefreshToken save(RefreshToken refreshToken) {
        entityManager.persist(refreshToken);
        return refreshToken;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RefreshToken> findByToken(String token) {
        TypedQuery<RefreshToken> query = entityManager.createQuery(
                "SELECT rt FROM RefreshToken rt WHERE rt.token = :token", 
                RefreshToken.class);
        query.setParameter("token", token);
        
        try {
            RefreshToken result = query.getSingleResult();
            return Optional.of(result);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    @Transactional
    public boolean revokeToken(String token) {
        Query query = entityManager.createQuery(
                "UPDATE RefreshToken rt SET rt.isRevoked = true WHERE rt.token = :token");
        query.setParameter("token", token);
        
        int updatedRows = query.executeUpdate();
        return updatedRows > 0;
    }

    @Override
    @Transactional
    public int revokeAllUserTokens(Long userId) {
        Query query = entityManager.createQuery(
                "UPDATE RefreshToken rt SET rt.isRevoked = true WHERE rt.userId = :userId");
        query.setParameter("userId", userId);
        
        return query.executeUpdate();
    }

    @Override
    @Transactional
    public int deleteExpired() {
        Query query = entityManager.createQuery(
                "DELETE FROM RefreshToken rt WHERE rt.expiresAt < :now");
        query.setParameter("now", LocalDateTime.now());
        
        return query.executeUpdate();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isValidToken(String token) {
        TypedQuery<RefreshToken> query = entityManager.createQuery(
                "SELECT rt FROM RefreshToken rt WHERE rt.token = :token " +
                "AND rt.isRevoked = false AND rt.expiresAt > :now", 
                RefreshToken.class);
        query.setParameter("token", token);
        query.setParameter("now", LocalDateTime.now());
        
        try {
            RefreshToken result = query.getSingleResult();
            return result != null;
        } catch (Exception e) {
            return false;
        }
    }
}
