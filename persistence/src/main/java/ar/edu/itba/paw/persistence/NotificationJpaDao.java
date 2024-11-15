package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.Notification;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.reviews.Review;
import org.springframework.stereotype.Repository;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;

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
    public List<Notification> getNotificationsForUser(Long userId, int page, int pageSize) {
        final TypedQuery<Notification> query = em.createQuery(
            "FROM Notification n WHERE n.recipientUser.id = :userId " +
            "ORDER BY n.createdAt DESC", Notification.class);
        
        query.setParameter("userId", userId);
        query.setFirstResult((page - 1) * pageSize);
        query.setMaxResults(pageSize);
        
        return query.getResultList();
    }

    @Override
    public void markAsRead(Long notificationId) {
        Notification notification = em.find(Notification.class, notificationId);
        if (notification != null) {
            notification.setRead(true);
            em.merge(notification);
        }
    }

    @Override
    public void markAllAsRead(Long userId) {
        em.createQuery(
            "UPDATE Notification n SET n.read = true " +
            "WHERE n.recipientUser.id = :userId AND n.read = false")
            .setParameter("userId", userId)
            .executeUpdate();
    }

    @Override
    public int getUnreadCount(Long userId) {
        return ((Number) em.createQuery(
            "SELECT COUNT(n) FROM Notification n " +
            "WHERE n.recipientUser.id = :userId AND n.read = false")
            .setParameter("userId", userId)
            .getSingleResult()).intValue();
    }
} 