package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.Comment;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.Collections;
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
    public List<Comment> findByReviewId(long reviewId, int pageSize, int offset) {
        // Query 1: SQL nativo para obtener IDs paginados (garantiza paginación en BD)
        Query nativeQuery = em.createNativeQuery(
                "SELECT id FROM comment " +
                "WHERE review_id = :reviewId " +
                "ORDER BY created_at DESC");
        nativeQuery.setParameter("reviewId", reviewId);
        nativeQuery.setMaxResults(pageSize);
        nativeQuery.setFirstResult(offset);
        
        @SuppressWarnings("unchecked")
        List<Object> rawResults = nativeQuery.getResultList();
        List<Long> commentIds = rawResults.stream()
                .map(n -> ((Number)n).longValue())
                .collect(java.util.stream.Collectors.toList());
        
        if (commentIds.isEmpty()) {
            return Collections.emptyList();
        }
        
        // Query 2: JPQL para obtener entidades completas
        final TypedQuery<Comment> query = em.createQuery(
                "FROM Comment c WHERE c.id IN :ids ORDER BY c.createdAt DESC", 
                Comment.class);
        query.setParameter("ids", commentIds);
        
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