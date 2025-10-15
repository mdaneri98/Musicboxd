package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.Notification;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.reviews.Review;
import ar.edu.itba.paw.models.FilterType;
import org.springframework.stereotype.Repository;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
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
    public Optional<Notification> findById(Long id) {
        return Optional.ofNullable(em.find(Notification.class, id));
    }

    @Override
    public List<Notification> findAll() {
        TypedQuery<Notification> query = em.createQuery(
            "FROM Notification n ORDER BY n.createdAt DESC", 
            Notification.class
        );
        return query.getResultList();
    }

    @Override
    public List<Notification> findPaginated(FilterType filterType, Integer page, Integer pageSize) {
        // Query 1: SQL nativo para obtener IDs paginados
        Query nativeQuery = em.createNativeQuery(
            "SELECT notification_id FROM notifications" + filterType.getFilter()
        );
        
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
        TypedQuery<Notification> query = em.createQuery(
            "FROM Notification n WHERE n.id IN :ids ORDER BY n.createdAt DESC", 
            Notification.class
        );
        query.setParameter("ids", notificationIds);
        
        return query.getResultList();
    }

    @Override
    public List<Notification> findBySubstring(String substring, Integer page, Integer pageSize) {
        // Buscar por mensaje o nombre de usuario que disparó la notificación
        Query nativeQuery = em.createNativeQuery(
            "SELECT n.notification_id FROM notifications n " +
            "LEFT JOIN cuser u ON n.trigger_user_id = u.user_id " +
            "WHERE n.message LIKE :substring OR u.username LIKE :substring " +
            "ORDER BY n.created_at DESC"
        );
        
        nativeQuery.setParameter("substring", "%" + substring + "%");
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
        
        TypedQuery<Notification> query = em.createQuery(
            "FROM Notification n WHERE n.id IN :ids ORDER BY n.createdAt DESC", 
            Notification.class
        );
        query.setParameter("ids", notificationIds);
        
        return query.getResultList();
    }

    @Override
    public Notification create(Notification notification) {
        if (notification.getId() == null) {
            em.persist(notification);
            return notification;
        }
        return em.merge(notification);
    }

    @Override
    public Boolean delete(Long id) {
        Notification notification = em.find(Notification.class, id);
        if (notification != null) {
            em.remove(notification);
            return true;
        }
        return false;
    }

    @Override
    public Long countAll() {
        Query query = em.createQuery("SELECT COUNT(n) FROM Notification n");
        return (Long) query.getSingleResult();
    }

    @Override
    public Long countByUserId(Long userId) {
        Query query = em.createQuery(
            "SELECT COUNT(n) FROM Notification n WHERE n.recipientUser.id = :userId"
        );
        query.setParameter("userId", userId);
        return (Long) query.getSingleResult();
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

    @Override
    public Notification update(Notification notification) {
        return em.merge(notification);
    }
} 