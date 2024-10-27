package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.Comment;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Repository
public class CommentJpaDao implements CommentDao {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Optional<Comment> findById(long id) {
        return Optional.ofNullable(em.find(Comment.class, id));
    }

    @Override
    public List<Comment> findByReviewId(long reviewId) {
        final TypedQuery<Comment> query = em.createQuery(
                "FROM Comment c " +
                        "WHERE c.review.id = :reviewId " +
                        "ORDER BY c.createdAt DESC", Comment.class);
        query.setParameter("reviewId", reviewId);
        return query.getResultList();
    }

    @Override
    public Comment save(Comment comment) {
        if (comment.getId() == null) {
            em.persist(comment);
            return comment;
        }
        return em.merge(comment);
    }

    @Override
    public void deleteById(long id) {
        Comment comment = em.find(Comment.class, id);
        if (comment != null) {
            em.remove(comment);
        }
    }
}