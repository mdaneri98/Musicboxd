package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.FilterType;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.reviews.AlbumReview;
import ar.edu.itba.paw.models.reviews.ArtistReview;
import ar.edu.itba.paw.models.reviews.Review;
import ar.edu.itba.paw.models.reviews.SongReview;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Primary
@Repository
public class ReviewJpaDao implements ReviewDao {

    @PersistenceContext
    private EntityManager em;


    //============================ C.R.U.D ============================
    @Override
    public Review create(Review entity) {
        em.persist(entity);
        return entity;
    }

    @Override
    public boolean delete(long id) {
        Review review = em.find(Review.class, id);
        if (review != null) {
            em.remove(review);
            return true;
        }
        return false;
    }

    @Override
    public Review update(Review entity) {
        return em.merge(entity);
    }

    @Override
    public Optional<ArtistReview> findArtistReviewById(long id) {
        return Optional.ofNullable(em.find(ArtistReview.class, id));
    }

    @Override
    public Optional<AlbumReview> findAlbumReviewById(long id) {
        return Optional.ofNullable(em.find(AlbumReview.class, id));
    }

    @Override
    public Optional<SongReview> findSongReviewById(long id) {
        return Optional.ofNullable(em.find(SongReview.class, id));
    }

    @Override
    public List<ArtistReview> findReviewsByArtistId(long artistId) {
        final TypedQuery<ArtistReview> query = em.createQuery(
                "FROM ArtistReview review WHERE review.artist.id = :artistId AND review.isBlocked = false ORDER BY review.createdAt DESC",
                ArtistReview.class
        );
        query.setParameter("artistId", artistId);
        return query.getResultList();
    }

    @Override
    public Optional<ArtistReview> findArtistReviewByUserId(long userId, long artistId) {
        final TypedQuery<ArtistReview> query = em.createQuery(
                "FROM ArtistReview ar WHERE ar.user.id = :userId AND ar.artist.id = :artistId",
                ArtistReview.class
        );
        query.setParameter("userId", userId);
        query.setParameter("artistId", artistId);
        List<ArtistReview> results = query.getResultList();
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    // ===

    @Override
    public ArtistReview saveArtistReview(ArtistReview review) {
        if (review.getId() == null) {
            em.persist(review);
            return review;
        }
        return em.merge(review);
    }

    @Override
    public List<AlbumReview> findReviewsByAlbumId(long albumId) {
        final TypedQuery<AlbumReview> query = em.createQuery(
                "FROM AlbumReview ar WHERE ar.album.id = :albumId AND ar.isBlocked = false ORDER BY ar.createdAt DESC",
                AlbumReview.class
        );
        query.setParameter("albumId", albumId);
        return query.getResultList();
    }

    @Override
    public Optional<AlbumReview> findAlbumReviewByUserId(long userId, long albumId) {
        final TypedQuery<AlbumReview> query = em.createQuery(
                "FROM AlbumReview ar WHERE ar.user.id = :userId AND ar.album.id = :albumId",
                AlbumReview.class
        );
        query.setParameter("userId", userId);
        query.setParameter("albumId", albumId);
        List<AlbumReview> results = query.getResultList();
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    @Override
    public AlbumReview saveAlbumReview(AlbumReview review) {
        if (review.getId() == null) {
            em.persist(review);
            return review;
        }
        return em.merge(review);
    }

    @Override
    public List<SongReview> findReviewsBySongId(long songId) {
        final TypedQuery<SongReview> query = em.createQuery(
                "FROM SongReview sr WHERE sr.song.id = :songId AND sr.isBlocked = false ORDER BY sr.createdAt DESC",
                SongReview.class
        );
        query.setParameter("songId", songId);
        return query.getResultList();
    }

    @Override
    public Optional<SongReview> findSongReviewByUserId(long userId, long songId) {
        final TypedQuery<SongReview> query = em.createQuery(
                "FROM SongReview sr WHERE sr.user.id = :userId AND sr.song.id = :songId",
                SongReview.class
        );
        query.setParameter("userId", userId);
        query.setParameter("songId", songId);
        List<SongReview> results = query.getResultList();
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    @Override
    public SongReview saveSongReview(SongReview review) {
        if (review.getId() == null) {
            em.persist(review);
            return review;
        }
        return em.merge(review);
    }

    @Override
    public void createLike(long userId, long reviewId) {
        em.createNativeQuery("INSERT INTO review_like (user_id, review_id) VALUES (:userId, :reviewId)")
                .setParameter("userId", userId)
                .setParameter("reviewId", reviewId)
                .executeUpdate();
    }

    @Override
    public void deleteLike(long userId, long reviewId) {
        em.createNativeQuery("DELETE FROM review_like WHERE user_id = :userId AND review_id = :reviewId")
                .setParameter("userId", userId)
                .setParameter("reviewId", reviewId)
                .executeUpdate();
    }

    @Override
    public void updateLikeCount(long reviewId) {
        Query countQuery = em.createNativeQuery(
                "SELECT COUNT(*) FROM review_like WHERE review_id = :reviewId"
        );
        countQuery.setParameter("reviewId", reviewId);
        Integer likes = ((Number) countQuery.getSingleResult()).intValue();

        em.createQuery("UPDATE Review r SET r.likes = :likes WHERE r.id = :reviewId")
                .setParameter("reviewId", reviewId)
                .setParameter("likes", likes)
                .executeUpdate();
    }


    @Override
    public List<User> likedBy(int page, int pageSize) {
        Query nativeQuery = em.createNativeQuery("SELECT id FROM review.likedBy");
        nativeQuery.setMaxResults(pageSize);
        nativeQuery.setFirstResult((page - 1) * pageSize);

        final List<Long> idList = (List<Long>) nativeQuery.getResultList()
                .stream().map(n -> (Long)((Number)n).longValue()).collect(Collectors.toList());

        // Sino el siguiente query falla, no te deja hacer IN de una lista vacía.
        if (idList.isEmpty())
            return Collections.emptyList();

        final TypedQuery<User> query = em.createQuery("FROM User WHERE id IN :ids", User.class);
        query.setParameter("ids", idList);

        return query.getResultList();
    }

    @Override
    public boolean isLiked(Long userId, Long reviewId) {
        Query query = em.createQuery(
                "SELECT COUNT(u) FROM Review r JOIN r.likedBy u WHERE r.id = :reviewId AND u.id = :userId"
        );
        query.setParameter("reviewId", reviewId);
        query.setParameter("userId", userId);

        Long count = (Long) query.getSingleResult();
        return count > 0;
    }

    //FIXME: Realizar la paginación mediante id's.
    @Override
    public List<ArtistReview> findArtistReviewsPaginated(long artistId, int page, int pageSize) {
        final TypedQuery<ArtistReview> query = em.createQuery(
                "FROM ArtistReview ar WHERE ar.artist.id = :artistId AND ar.isBlocked = false ORDER BY ar.createdAt DESC",
                ArtistReview.class
        );
        query.setParameter("artistId", artistId);
        query.setFirstResult((page - 1) * pageSize);
        query.setMaxResults(pageSize);
        return query.getResultList();
    }

    //FIXME: Realizar la paginación mediante id's.
    @Override
    public List<AlbumReview> findAlbumReviewsPaginated(long albumId, int page, int pageSize) {
        final TypedQuery<AlbumReview> query = em.createQuery(
                "FROM AlbumReview ar WHERE ar.album.id = :albumId AND ar.isBlocked = false ORDER BY ar.createdAt DESC",
                AlbumReview.class
        );
        query.setParameter("albumId", albumId);
        query.setFirstResult((page - 1) * pageSize);
        query.setMaxResults(pageSize);
        return query.getResultList();
    }

    //FIXME: Realizar la paginación mediante id's.
    @Override
    public List<SongReview> findSongReviewsPaginated(long songId, int page, int pageSize) {
        final TypedQuery<SongReview> query = em.createQuery(
                "FROM SongReview sr WHERE sr.song.id = :songId AND sr.isBlocked = false ORDER BY sr.createdAt DESC",
                SongReview.class
        );
        query.setParameter("songId", songId);
        query.setFirstResult((page - 1) * pageSize);
        query.setMaxResults(pageSize);
        return query.getResultList();
    }

     //FIXME: Realizar la paginación mediante id's.
     @Override
     public List<Review> getPopularReviewsPaginated(int page, int pageSize) {
         final TypedQuery<Review> query = em.createQuery(
                 "SELECT r FROM Review r " +
                 "WHERE r.isBlocked = false " +
                 "AND (TYPE(r) = ArtistReview OR TYPE(r) = AlbumReview OR TYPE(r) = SongReview) " +
                 "ORDER BY r.likes DESC, r.createdAt DESC",
                 Review.class
         );
         query.setFirstResult((page - 1) * pageSize);
         query.setMaxResults(pageSize);
         return query.getResultList();
     }

    @Override
    public List<Review> getReviewsFromFollowedUsersPaginated(Long userId, int page, int pageSize) {
        final TypedQuery<Review> query = em.createQuery(
                "SELECT DISTINCT r FROM User u " +
                        "JOIN u.following f " +
                        "JOIN Review r ON r.user = f " +
                        "WHERE u.id = :userId " +
                        "AND r.isBlocked = false " +
                        "AND (TYPE(r) = ArtistReview OR TYPE(r) = AlbumReview OR TYPE(r) = SongReview) " +
                        "ORDER BY r.createdAt DESC",
                Review.class
        );
        query.setParameter("userId", userId);
        query.setFirstResult((page - 1) * pageSize);
        query.setMaxResults(pageSize);
        return query.getResultList();
    }

    //FIXME: Realizar la paginación mediante id's.
    @Override
    public List<Review> findReviewsByUserPaginated(Long userId, int page, int pageSize) {
        final TypedQuery<Review> query = em.createQuery(
                "FROM Review r " +
                "WHERE r.user.id = :userId " +
                "AND (TYPE(r) = ArtistReview OR TYPE(r) = AlbumReview OR TYPE(r) = SongReview) " +
                "ORDER BY r.createdAt DESC",
                Review.class
        );
        query.setParameter("userId", userId);
        query.setFirstResult((page - 1) * pageSize);
        query.setMaxResults(pageSize);
        return query.getResultList();
    }

    @Override
    public boolean isArtistReview(long reviewId) {
        return em.find(ArtistReview.class, reviewId) != null;
    }

    @Override
    public boolean isAlbumReview(long reviewId) {
        return em.find(AlbumReview.class, reviewId) != null;
    }

    @Override
    public boolean isSongReview(long reviewId) {
        return em.find(SongReview.class, reviewId) != null;
    }

    @Override
    public void block(Long reviewId) {
        em.createQuery("UPDATE Review r SET r.isBlocked = true WHERE r.id = :reviewId")
                .setParameter("reviewId", reviewId)
                .executeUpdate();
    }

    @Override
    public void unblock(Long reviewId) {
        em.createQuery("UPDATE Review r SET r.isBlocked = false WHERE r.id = :reviewId")
                .setParameter("reviewId", reviewId)
                .executeUpdate();
    }

    @Override
    public Optional<Review> find(long id) {
        return Optional.ofNullable(em.find(Review.class, id));
    }

    @Override
    public List<Review> findAll() {
        return em.createQuery("FROM Review r WHERE r.isBlocked = false ORDER BY r.createdAt DESC", Review.class)
                .getResultList();
    }

    @Override
    public List<Review> findPaginated(FilterType filterType, int limit, int offset) {
        String queryStr = "FROM Review WHERE isBlocked = false " +
        "AND (TYPE(r) = ArtistReview OR TYPE(r) = AlbumReview OR TYPE(r) = SongReview) " +
         filterType.getFilter();

        final TypedQuery<Review> query = em.createQuery(queryStr, Review.class);
        query.setFirstResult(offset);
        query.setMaxResults(limit);
        return query.getResultList();
    }

    @Override
    public void updateCommentAmount(long reviewId) {
        Query countQuery = em.createQuery(
                "SELECT COUNT(c) FROM Comment c WHERE c.review.id = :reviewId"
        );
        countQuery.setParameter("reviewId", reviewId);

        Long count = (Long) countQuery.getSingleResult();

        Query updateQuery = em.createQuery(
                "UPDATE Review r SET r.commentAmount = :count WHERE r.id = :reviewId"
        );
        updateQuery.setParameter("count", count.intValue());
        updateQuery.setParameter("reviewId", reviewId);
        updateQuery.executeUpdate();
    }

}
