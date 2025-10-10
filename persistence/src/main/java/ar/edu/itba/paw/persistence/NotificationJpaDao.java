package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.Notification;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.reviews.Review;
import org.springframework.stereotype.Repository;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class NotificationJpaDao implements NotificationDao {
    
    @PersistenceContext
    private EntityManager em;

    @Override
    public Notification create(Notification.NotificationType type, User recipientUser,
                               User triggerUser, Review review, String message) {
        final Notification notification = new Notification(null, type, recipientUser,
                triggerUser, review, LocalDateTime.now(), false, message);
        em.persist(notification);
        return notification;
    }

    @Override
    public List<Notification> getNotificationsForUser(Long userId, Integer page, Integer pageSize) {
        // Query 1: SQL nativo para obtener IDs paginados (garantiza paginación en BD)
        Query nativeQuery = em.createNativeQuery(
            "SELECT notification_id FROM notifications " +
            "WHERE recipient_user_id = :userId " +
            "ORDER BY created_at DESC");
        
        nativeQuery.setParameter("userId", userId);
        nativeQuery.setFirstResult((page - 1) * pageSize);
        nativeQuery.setMaxResults(pageSize);
        
        @SuppressWarnings("unchecked")
        List<Object> rawResults = nativeQuery.getResultList();
        List<Long> notificationIds = rawResults.stream()
                .map(n -> ((Number)n).longValue())
                .collect(Collectors.toList());
        
        if (notificationIds.isEmpty()) {
            return Collections.emptyList();
        }
        
        // Query 2: JPQL para obtener entidades completas
        final TypedQuery<Notification> query = em.createQuery(
            "FROM Notification n WHERE n.id IN :ids ORDER BY n.createdAt DESC", 
            Notification.class);
        query.setParameter("ids", notificationIds);
        
        return query.getResultList();
    }

    @Override
    public Void markAsRead(Long notificationId) {
        Notification notification = em.find(Notification.class, notificationId);
        if (notification != null) {
            notification.setRead(true);
            em.merge(notification);
        }
        return null;
    }

    @Override
    public Void markAllAsRead(Long userId) {
        em.createQuery(
            "UPDATE Notification n SET n.read = true " +
            "WHERE n.recipientUser.id = :userId AND n.read = false")
            .setParameter("userId", userId)
            .executeUpdate();
        return null;
    }

    @Override
    public Integer getUnreadCount(Long userId) {
        return ((Number) em.createQuery(
            "SELECT COUNT(n) FROM Notification n " +
            "WHERE n.recipientUser.id = :userId AND n.read = false")
            .setParameter("userId", userId)
            .getSingleResult()).intValue();
    }
} 