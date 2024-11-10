package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.models.reviews.Review;
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

@Transactional
@Rollback
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Sql(scripts = {"classpath:notification_setUp.sql"})
public class NotificationJpaDaoTest {

    private static final long USER_ID = 200;
    private static final long USER_2_ID = 201;
    private static final long TRIGGER_USER_ID = 202;
    private static final long RESOURCE_ID = 400;
    private static final long NOTIFICATION_ID = 900;

    private static final String NOTIFICATION_MESSAGE = "Test notification message";

    @Autowired
    private NotificationDao notificationDao;

    @PersistenceContext
    private EntityManager em;

    @Test
    public void test_create() {
        // 1. Pre-conditions
        User user = em.find(User.class, USER_ID);
        User userTrigger = em.find(User.class, TRIGGER_USER_ID);
        Review review = em.find(Review.class, RESOURCE_ID);

        // 2. Execute
        Notification notification = notificationDao.create(
                Notification.NotificationType.LIKE,
                user,
                userTrigger,
                review,
                NOTIFICATION_MESSAGE
        );

        // 3. Post-conditions
        assertNotNull(notification);
        assertNotNull(notification.getId());
        assertEquals(Notification.NotificationType.LIKE, notification.getType());
        assertEquals(USER_ID, notification.getRecipientUser().getId().longValue());
        assertEquals(TRIGGER_USER_ID, notification.getTriggerUser().getId().longValue());
        assertEquals(RESOURCE_ID, notification.getReview().getId().longValue());
        assertEquals(NOTIFICATION_MESSAGE, notification.getMessage());
        assertFalse(notification.isRead());

        // Verify creation date is recent
        LocalDateTime now = LocalDateTime.now();
        assertTrue(notification.getCreatedAt().isAfter(now.minusMinutes(1)));
        assertTrue(notification.getCreatedAt().isBefore(now.plusMinutes(1)));

        // Verify in database
        Notification dbNotification = em.find(Notification.class, notification.getId());
        assertEquals(notification.getType(), dbNotification.getType());
        assertEquals(notification.getMessage(), dbNotification.getMessage());
    }

    @Test
    public void test_getNotificationsForUser_FirstPage() {
        // 1. Pre-conditions
        int page = 1;
        int pageSize = 3;

        // 2. Execute
        List<Notification> notifications = notificationDao.getNotificationsForUser(USER_ID, page, pageSize);

        // 3. Post-conditions
        assertNotNull(notifications);
        assertEquals(3, notifications.size());

        // Verify order (by createdAt DESC)
        assertEquals(904L, notifications.get(0).getId().longValue()); // Most recent
        assertEquals(903L, notifications.get(1).getId().longValue());
        assertEquals(902L, notifications.get(2).getId().longValue());

        // Verify ordering by date
        for (int i = 0; i < notifications.size() - 1; i++) {
            assertTrue(notifications.get(i).getCreatedAt()
                    .isAfter(notifications.get(i + 1).getCreatedAt()));
        }
    }

    @Test
    public void test_getNotificationsForUser_SecondPage() {
        // 1. Pre-conditions
        int page = 2;
        int pageSize = 3;

        // 2. Execute
        List<Notification> notifications = notificationDao.getNotificationsForUser(USER_ID, page, pageSize);

        // 3. Post-conditions
        assertNotNull(notifications);
        assertEquals(2, notifications.size()); // Only 2 remaining notifications

        // Verify correct notifications
        assertEquals(901L, notifications.get(0).getId().longValue());
        assertEquals(900L, notifications.get(1).getId().longValue());

        // Verify ordering by date
        for (int i = 0; i < notifications.size() - 1; i++) {
            assertTrue(notifications.get(i).getCreatedAt()
                    .isAfter(notifications.get(i + 1).getCreatedAt()));
        }
    }

    @Test
    public void test_markAsRead() {
        // 1. Pre-conditions

        // 2. Execute
        notificationDao.markAsRead(NOTIFICATION_ID);

        // 3. Post-conditions
        Notification updatedNotification = em.find(Notification.class, NOTIFICATION_ID);
        assertNotNull(updatedNotification);
        assertTrue(updatedNotification.isRead());
    }

    @Test
    public void test_markAllAsRead() {
        // 1. Pre-conditions

        // 2. Execute
        notificationDao.markAllAsRead(USER_ID);

        // 3. Post-conditions

        // Verify all notifications are now read
        TypedQuery<Notification> query = em.createQuery(
                "FROM Notification n WHERE n.recipientUser.id = :userId",
                Notification.class);
        query.setParameter("userId", USER_ID);

        List<Notification> notifications = query.getResultList();
        for (Notification notification : notifications) {
            assertTrue(notification.isRead());
        }
    }

    @Test
    public void test_getUnreadCount() {
        // 1. Pre-conditions - user has 3 unread notifications

        // 2. Execute
        int unreadCount = notificationDao.getUnreadCount(USER_ID);

        // 3. Post-conditions
        assertEquals(3, unreadCount);
    }

    @Test
    public void test_getUnreadCount_NoUnread() {
        // 1. Pre-conditions

        // 2. Execute
        int unreadCount = notificationDao.getUnreadCount(USER_2_ID);

        // 3. Post-conditions
        assertEquals(0, unreadCount);
    }
}
