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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Sql(scripts = "classpath:comment_setUp.sql")
@Transactional
@Rollback
public class CommentJpaDaoTest {

    private static final long PRE_EXISTING_USER_ID = 201;
    private static final long PRE_EXISTING_REVIEW_ID = 400;
    private static final long PRE_EXISTING_COMMENT_ID = 700;
    private static final long NEW_COMMENT_ID = 1000;

    private static final String NEW_COMMENT_CONTENT = "New Comment";

    @Autowired
    private CommentJpaDao commentDao;

    @PersistenceContext
    private EntityManager em;

    @Test
    public void test_findById_Exists() {
        // 1. Pre-conditions - comment exists

        // 2. Execute
        Optional<Comment> maybeComment = commentDao.findById(PRE_EXISTING_COMMENT_ID);

        // 3. Post-conditions
        assertTrue(maybeComment.isPresent());
        Comment comment = maybeComment.get();
        assertEquals(PRE_EXISTING_COMMENT_ID, comment.getId().longValue());
        assertEquals("Great review!", comment.getContent());
        assertEquals(PRE_EXISTING_USER_ID, comment.getUser().getId().longValue());
        assertEquals(PRE_EXISTING_REVIEW_ID, comment.getReview().getId().longValue());
    }

    @Test
    public void test_findById_NotExists() {
        // 1. Pre-conditions - comment doesn't exist

        // 2. Execute
        Optional<Comment> maybeComment = commentDao.findById(NEW_COMMENT_ID);

        // 3. Post-conditions
        assertFalse(maybeComment.isPresent());
    }

    @Test
    public void test_findByReviewId() {
        // 1. Pre-conditions - comment exists
        int offset = 0;
        int pageSize = 10;

        // 2. Execute
        List<Comment> comments = commentDao.findByReviewId(PRE_EXISTING_REVIEW_ID, pageSize, offset);

        // 3. Post-conditions
        assertNotNull(comments);
        assertEquals(2, comments.size()); // Review 400 has 2 direct comments

        // Verify comments are ordered by creation date (DESC)
        LocalDateTime previousDate = LocalDateTime.now();
        for(Comment comment : comments) {
            assertTrue(comment.getCreatedAt().isBefore(previousDate) ||
                    comment.getCreatedAt().equals(previousDate));
            previousDate = comment.getCreatedAt();
            assertEquals(PRE_EXISTING_REVIEW_ID, comment.getReview().getId().longValue());
        }
    }

    @Test
    public void test_save_New() {
        // 1. Pre-conditions
        User user = em.find(User.class, PRE_EXISTING_USER_ID);
        Review review = em.find(Review.class, PRE_EXISTING_REVIEW_ID);
        LocalDateTime now = LocalDateTime.now();
        Comment comment = new Comment(user, review, NEW_COMMENT_CONTENT, now);

        // 2. Execute
        Comment saved = commentDao.save(comment);

        // 3. Post-conditions
        assertNotNull(saved);
        assertNotNull(saved.getId());
        assertEquals(NEW_COMMENT_CONTENT, saved.getContent());
        assertEquals(now, saved.getCreatedAt());
        assertEquals(PRE_EXISTING_USER_ID, saved.getUser().getId().longValue());
        assertEquals(PRE_EXISTING_REVIEW_ID, saved.getReview().getId().longValue());

        // Verify in database
        Comment dbComment = em.find(Comment.class, saved.getId());
        assertEquals(saved.getContent(), dbComment.getContent());
        assertEquals(saved.getCreatedAt(), dbComment.getCreatedAt());
        assertEquals(saved.getUser().getId(), dbComment.getUser().getId());
        assertEquals(saved.getReview().getId(), dbComment.getReview().getId());
    }

    @Test
    public void test_save_Update() {
        // 1. Pre-conditions
        Comment comment = em.find(Comment.class, PRE_EXISTING_COMMENT_ID);
        comment.setContent(NEW_COMMENT_CONTENT);

        // 2. Execute
        Comment updated = commentDao.save(comment);

        // 3. Post-conditions
        assertEquals(PRE_EXISTING_COMMENT_ID, updated.getId().longValue());
        assertEquals(NEW_COMMENT_CONTENT, updated.getContent());

        // Verify in database
        Comment dbComment = em.find(Comment.class, PRE_EXISTING_COMMENT_ID);
        assertEquals(NEW_COMMENT_CONTENT, dbComment.getContent());
    }

    @Test
    public void test_deleteById_Existing() {
        // 1. Pre-conditions - comment exists

        // 2. Execute
        commentDao.deleteById(PRE_EXISTING_COMMENT_ID);

        // 3. Post-conditions
        assertNull(em.find(Comment.class, PRE_EXISTING_COMMENT_ID));
    }

    @Test
    public void test_deleteById_NonExisting() {
        // 1. Pre-conditions

        // 2. Execute - should not throw exception
        commentDao.deleteById(NEW_COMMENT_ID);

        // 3. Post-conditions
        assertNull(em.find(Comment.class, NEW_COMMENT_ID));
    }
}
