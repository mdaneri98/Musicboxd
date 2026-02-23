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

    // ============================ C.R.U.D ============================
    @Override
    public Review create(Review entity) {
        em.persist(entity);
        em.flush();
        return entity;
    }

    @Override
    public Boolean delete(Long id) {
        Review review = em.find(Review.class, id);
        if (review != null) {
            em.remove(review);
            em.flush();
            return true;
        }
        return false;
    }

    @Override
    public Review update(Review entity) {
        return em.merge(entity);
    }

    @Override
    public Optional<ArtistReview> findArtistReviewById(Long id) {
        return Optional.ofNullable(em.find(ArtistReview.class, id));
    }

    @Override
    public Optional<AlbumReview> findAlbumReviewById(Long id) {
        return Optional.ofNullable(em.find(AlbumReview.class, id));
    }

    @Override
    public Optional<SongReview> findSongReviewById(Long id) {
        return Optional.ofNullable(em.find(SongReview.class, id));
    }

    @Override
    public Optional<ArtistReview> findArtistReviewByUserId(Long userId, Long artistId) {
        final TypedQuery<ArtistReview> query = em.createQuery(
                "FROM ArtistReview ar WHERE ar.user.id = :userId AND ar.artist.id = :artistId AND ar.isBlocked = false",
                ArtistReview.class);
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
    public Optional<AlbumReview> findAlbumReviewByUserId(Long userId, Long albumId) {
        final TypedQuery<AlbumReview> query = em.createQuery(
                "FROM AlbumReview ar WHERE ar.user.id = :userId AND ar.album.id = :albumId AND ar.isBlocked = false",
                AlbumReview.class);
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
    public Optional<SongReview> findSongReviewByUserId(Long userId, Long songId) {
        final TypedQuery<SongReview> query = em.createQuery(
                "FROM SongReview sr WHERE sr.user.id = :userId AND sr.song.id = :songId",
                SongReview.class);
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
    public Void createLike(Long userId, Long reviewId) {
        em.createNativeQuery("INSERT INTO review_like (user_id, review_id) VALUES (:userId, :reviewId)")
                .setParameter("userId", userId)
                .setParameter("reviewId", reviewId)
                .executeUpdate();
        return null;
    }

    @Override
    public Void deleteLike(Long userId, Long reviewId) {
        em.createNativeQuery("DELETE FROM review_like WHERE user_id = :userId AND review_id = :reviewId")
                .setParameter("userId", userId)
                .setParameter("reviewId", reviewId)
                .executeUpdate();
        return null;
    }

    @Override
    public Void updateLikeCount(Long reviewId) {
        Query countQuery = em.createNativeQuery(
                "SELECT COUNT(*) FROM review_like WHERE review_id = :reviewId");
        countQuery.setParameter("reviewId", reviewId);
        Integer likes = ((Number) countQuery.getSingleResult()).intValue();

        em.createQuery("UPDATE Review r SET r.likes = :likes WHERE r.id = :reviewId")
                .setParameter("reviewId", reviewId)
                .setParameter("likes", likes)
                .executeUpdate();
        return null;
    }

    @Override
    public List<User> likedBy(Long reviewId, Integer pageNum, Integer pageSize) {
        // Query 1: SQL nativo para obtener IDs paginados (garantiza paginación en BD)
        Query nativeQuery = em.createNativeQuery(
                "SELECT DISTINCT u.id FROM cuser u " +
                        "JOIN review_like rl ON u.id = rl.user_id " +
                        "WHERE rl.review_id = :reviewId");

        nativeQuery.setParameter("reviewId", reviewId);
        nativeQuery.setMaxResults(pageSize);
        nativeQuery.setFirstResult((pageNum - 1) * pageSize);

        @SuppressWarnings("unchecked")
        List<Object> rawResults = nativeQuery.getResultList();
        List<Long> userIds = rawResults.stream()
                .map(n -> ((Number) n).longValue())
                .collect(Collectors.toList());

        if (userIds.isEmpty()) {
            return Collections.emptyList();
        }

        // Query 2: JPQL para obtener entidades completas
        TypedQuery<User> query = em.createQuery(
                "FROM User u WHERE u.id IN :ids ORDER BY u.username",
                User.class);
        query.setParameter("ids", userIds);

        return query.getResultList();
    }

    @Override
    public Boolean isLiked(Long userId, Long reviewId) {
        Query query = em.createQuery(
                "SELECT COUNT(u) FROM Review r JOIN r.likedBy u WHERE r.id = :reviewId AND u.id = :userId");
        query.setParameter("reviewId", reviewId);
        query.setParameter("userId", userId);

        Long count = (Long) query.getSingleResult();
        return count > 0;
    }

    @Override
    public List<Long> getLikedReviewIds(Long userId, List<Long> reviewIds) {
        if (reviewIds == null || reviewIds.isEmpty()) {
            return Collections.emptyList();
        }

        TypedQuery<Long> query = em.createQuery(
                "SELECT r.id FROM Review r JOIN r.likedBy u WHERE u.id = :userId AND r.id IN :reviewIds",
                Long.class);
        query.setParameter("userId", userId);
        query.setParameter("reviewIds", reviewIds);

        return query.getResultList();
    }

    @Override
    public List<ArtistReview> findArtistReviewsPaginated(Long artistId, Integer page, Integer pageSize) {
        // Query 1: SQL nativo para obtener IDs paginados (garantiza paginación en BD)
        Query nativeQuery = em.createNativeQuery(
                "SELECT ar.review_id FROM artist_review ar " +
                        "JOIN review r ON ar.review_id = r.id " +
                        "WHERE ar.artist_id = :artistId " +
                        "ORDER BY r.created_at DESC");
        nativeQuery.setParameter("artistId", artistId);
        nativeQuery.setFirstResult((page - 1) * pageSize);
        nativeQuery.setMaxResults(pageSize);

        @SuppressWarnings("unchecked")
        List<Object> rawResults = nativeQuery.getResultList();
        List<Long> reviewIds = rawResults.stream()
                .map(n -> ((Number) n).longValue())
                .collect(Collectors.toList());

        if (reviewIds.isEmpty()) {
            return Collections.emptyList();
        }

        // Query 2: JPQL para obtener entidades completas
        final TypedQuery<ArtistReview> query = em.createQuery(
                "FROM ArtistReview ar WHERE ar.id IN :ids ORDER BY ar.createdAt DESC",
                ArtistReview.class);
        query.setParameter("ids", reviewIds);

        return query.getResultList();
    }

    @Override
    public List<AlbumReview> findAlbumReviewsPaginated(Long albumId, Integer page, Integer pageSize) {
        // Query 1: SQL nativo para obtener IDs paginados (garantiza paginación en BD)
        Query nativeQuery = em.createNativeQuery(
                "SELECT ar.review_id FROM album_review ar " +
                        "JOIN review r ON ar.review_id = r.id " +
                        "WHERE ar.album_id = :albumId " +
                        "ORDER BY r.created_at DESC");
        nativeQuery.setParameter("albumId", albumId);
        nativeQuery.setFirstResult((page - 1) * pageSize);
        nativeQuery.setMaxResults(pageSize);

        @SuppressWarnings("unchecked")
        List<Object> rawResults = nativeQuery.getResultList();
        List<Long> reviewIds = rawResults.stream()
                .map(n -> ((Number) n).longValue())
                .collect(Collectors.toList());

        if (reviewIds.isEmpty()) {
            return Collections.emptyList();
        }

        // Query 2: JPQL para obtener entidades completas
        final TypedQuery<AlbumReview> query = em.createQuery(
                "FROM AlbumReview ar WHERE ar.id IN :ids ORDER BY ar.createdAt DESC",
                AlbumReview.class);
        query.setParameter("ids", reviewIds);

        return query.getResultList();
    }

    @Override
    public List<SongReview> findSongReviewsPaginated(Long songId, Integer page, Integer pageSize) {
        // Query 1: SQL nativo para obtener IDs paginados (garantiza paginación en BD)
        Query nativeQuery = em.createNativeQuery(
                "SELECT sr.review_id FROM song_review sr " +
                        "JOIN review r ON sr.review_id = r.id " +
                        "WHERE sr.song_id = :songId " +
                        "ORDER BY r.created_at DESC");
        nativeQuery.setParameter("songId", songId);
        nativeQuery.setFirstResult((page - 1) * pageSize);
        nativeQuery.setMaxResults(pageSize);

        @SuppressWarnings("unchecked")
        List<Object> rawResults = nativeQuery.getResultList();
        List<Long> reviewIds = rawResults.stream()
                .map(n -> ((Number) n).longValue())
                .collect(Collectors.toList());

        if (reviewIds.isEmpty()) {
            return Collections.emptyList();
        }

        // Query 2: JPQL para obtener entidades completas
        final TypedQuery<SongReview> query = em.createQuery(
                "FROM SongReview sr WHERE sr.id IN :ids ORDER BY sr.createdAt DESC",
                SongReview.class);
        query.setParameter("ids", reviewIds);

        return query.getResultList();
    }

    @Override
    public List<Review> getReviewsFromFollowedUsersPaginated(Long userId, Integer page, Integer pageSize) {
        // Query 1: SQL nativo para obtener IDs paginados (garantiza paginación en BD)
        Query nativeQuery = em.createNativeQuery(
                "SELECT DISTINCT r.id " +
                        "FROM review r " +
                        "JOIN cuser u ON r.user_id = u.id " +
                        "JOIN follower f ON u.id = f.following " +
                        "WHERE f.user_id = :userId " +
                        "AND r.isblocked = false");

        nativeQuery.setParameter("userId", userId);
        nativeQuery.setFirstResult((page - 1) * pageSize);
        nativeQuery.setMaxResults(pageSize);

        @SuppressWarnings("unchecked")
        List<Object> rawResults = nativeQuery.getResultList();
        List<Long> reviewIds = rawResults.stream()
                .map(n -> ((Number) n).longValue())
                .collect(Collectors.toList());

        if (reviewIds.isEmpty()) {
            return Collections.emptyList();
        }

        // Query 2: JPQL para obtener entidades completas
        final TypedQuery<Review> query = em.createQuery(
                "FROM Review r WHERE r.id IN :ids ORDER BY r.createdAt DESC",
                Review.class);
        query.setParameter("ids", reviewIds);

        return query.getResultList();
    }

    @Override
    public List<Review> findReviewsByUserPaginated(Long userId, Integer page, Integer pageSize) {
        // Query 1: SQL nativo para obtener IDs paginados (garantiza paginación en BD)
        Query nativeQuery = em.createNativeQuery(
                "SELECT r.id FROM review r " +
                        "WHERE r.user_id = :userId " +
                        "ORDER BY r.created_at DESC");
        nativeQuery.setParameter("userId", userId);
        nativeQuery.setFirstResult((page - 1) * pageSize);
        nativeQuery.setMaxResults(pageSize);

        @SuppressWarnings("unchecked")
        List<Object> rawResults = nativeQuery.getResultList();
        List<Long> reviewIds = rawResults.stream()
                .map(n -> ((Number) n).longValue())
                .collect(Collectors.toList());

        if (reviewIds.isEmpty()) {
            return Collections.emptyList();
        }

        // Query 2: JPQL para obtener entidades completas
        final TypedQuery<Review> query = em.createQuery(
                "FROM Review r WHERE r.id IN :ids ORDER BY r.createdAt DESC",
                Review.class);
        query.setParameter("ids", reviewIds);

        return query.getResultList();
    }

    @Override
    public Boolean isArtistReview(Long reviewId) {
        return em.find(ArtistReview.class, reviewId) != null;
    }

    @Override
    public Boolean isAlbumReview(Long reviewId) {
        return em.find(AlbumReview.class, reviewId) != null;
    }

    @Override
    public Boolean isSongReview(Long reviewId) {
        return em.find(SongReview.class, reviewId) != null;
    }

    @Override
    public Void block(Long reviewId) {
        Review review = em.find(Review.class, reviewId);
        if (review != null) {
            review.setBlocked(true);
        }
        return null;
    }

    @Override
    public Void unblock(Long reviewId) {
        Review review = em.find(Review.class, reviewId);
        if (review != null) {
            review.setBlocked(false);
        }
        return null;
    }

    @Override
    public Optional<Review> findById(Long id) {
        return Optional.ofNullable(em.find(Review.class, id));
    }

    @Override
    public List<Review> findAll() {
        return em.createQuery("FROM Review r WHERE r.isBlocked = false ORDER BY r.createdAt DESC", Review.class)
                .getResultList();
    }

    @Override
    public List<Review> findPaginated(FilterType filterType, Integer page, Integer pageSize) {
        // Query 1: SQL nativo para obtener IDs paginados (garantiza paginación en BD)
        String nativeSQL = "SELECT r.id FROM review r WHERE isblocked = false " +
                filterType.getFilter();

        Query nativeQuery = em.createNativeQuery(nativeSQL)
                .setFirstResult((page - 1) * pageSize)
                .setMaxResults(pageSize);

        @SuppressWarnings("unchecked")
        List<Object> rawResults = nativeQuery.getResultList();
        List<Long> reviewIds = rawResults.stream()
                .map(n -> ((Number) n).longValue())
                .collect(Collectors.toList());

        if (reviewIds.isEmpty()) {
            return Collections.emptyList();
        }

        // Query 2: JPQL para obtener entidades completas manteniendo el orden del
        // filtro
        String entityQueryStr = "SELECT r FROM Review r WHERE r.id IN :ids AND isBlocked = false ";

        return em.createQuery(entityQueryStr, Review.class)
                .setParameter("ids", reviewIds)
                .getResultList();
    }

    @Override
    public Void updateCommentAmount(Long reviewId) {
        Query countQuery = em.createQuery(
                "SELECT COUNT(c) FROM Comment c WHERE c.review.id = :reviewId");
        countQuery.setParameter("reviewId", reviewId);

        Long count = (Long) countQuery.getSingleResult();

        Query updateQuery = em.createQuery(
                "UPDATE Review r SET r.commentAmount = :count WHERE r.id = :reviewId");
        updateQuery.setParameter("count", count.intValue());
        updateQuery.setParameter("reviewId", reviewId);
        updateQuery.executeUpdate();
        return null;
    }

    @Override
    public Long countAll() {
        Query query = em.createQuery("SELECT COUNT(r) FROM Review r WHERE r.isBlocked = false");
        return (Long) query.getSingleResult();
    }

    @Override
    public Long countReviewsByUser(Long userId) {
        Query query = em.createQuery("SELECT COUNT(r) FROM Review r WHERE r.user.id = :userId AND r.isBlocked = false");
        query.setParameter("userId", userId);
        return (Long) query.getSingleResult();
    }

    @Override
    public List<Review> findBySubstring(String substring, Integer page, Integer size) {
        // Query 1: SQL nativo para obtener IDs paginados (garantiza paginación en BD)
        Query nativeQuery = em.createNativeQuery(
                "SELECT r.id FROM review r WHERE r.title LIKE :substring AND r.isBlocked = false ORDER BY r.createdAt DESC");
        nativeQuery.setParameter("substring", "%" + substring + "%");
        nativeQuery.setFirstResult((page - 1) * size);
        nativeQuery.setMaxResults(size);

        @SuppressWarnings("unchecked")
        List<Object> rawResults = nativeQuery.getResultList();
        List<Long> reviewIds = rawResults.stream()
                .map(n -> ((Number) n).longValue())
                .collect(Collectors.toList());

        if (reviewIds.isEmpty()) {
            return Collections.emptyList();
        }

        // Query 2: JPQL para obtener entidades completas
        TypedQuery<Review> query = em.createQuery(
                "FROM Review r WHERE r.id IN :ids ORDER BY r.createdAt DESC",
                Review.class);
        query.setParameter("ids", reviewIds);
        return query.getResultList();
    }

    @Override
    public Long countReviewsFromFollowedUsers(Long userId) {
        Query nativeQuery = em.createNativeQuery(
                "SELECT COUNT(DISTINCT r.id) " +
                        "FROM review r " +
                        "JOIN cuser u ON r.user_id = u.id " +
                        "JOIN follower f ON u.id = f.following " +
                        "WHERE f.user_id = :userId " +
                        "AND r.isblocked = false");
        nativeQuery.setParameter("userId", userId);
        return ((Number) nativeQuery.getSingleResult()).longValue();
    }

}
