package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.domain.album.AlbumId;
import ar.edu.itba.paw.domain.artist.ArtistId;
import ar.edu.itba.paw.domain.review.*;
import ar.edu.itba.paw.domain.song.SongId;
import ar.edu.itba.paw.domain.user.UserId;
import ar.edu.itba.paw.infrastructure.jpa.*;
import ar.edu.itba.paw.persistence.mappers.ReviewMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class ReviewRepositoryJpa implements ReviewRepository {

    @PersistenceContext
    private EntityManager em;

    private final ReviewMapper mapper;

    @Autowired
    public ReviewRepositoryJpa(ReviewMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public Optional<Review> findById(ReviewId id) {
        ReviewJpaEntity entity = em.find(ReviewJpaEntity.class, id.getValue());
        return Optional.ofNullable(entity).map(mapper::toDomain);
    }

    @Override
    public Review save(Review review) {
        ReviewJpaEntity entity = mapper.toEntity(review);

        if (review.getId() == null) {
            em.persist(entity);
        } else {
            entity = em.merge(entity);
        }

        em.flush();
        return mapper.toDomain(entity);
    }

    @Override
    public void delete(ReviewId id) {
        ReviewJpaEntity entity = em.find(ReviewJpaEntity.class, id.getValue());
        if (entity != null) {
            em.remove(entity);
        }
    }

    @Override
    public List<Review> findAll(Integer page, Integer size) {
        String jpql = "SELECT r FROM ReviewJpaEntity r WHERE r.isBlocked = false ORDER BY r.createdAt DESC";
        TypedQuery<ReviewJpaEntity> query = em.createQuery(jpql, ReviewJpaEntity.class);

        if (page != null && size != null) {
            query.setFirstResult(page * size);
            query.setMaxResults(size);
        }

        return query.getResultStream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public Long countAll() {
        String jpql = "SELECT COUNT(r) FROM ReviewJpaEntity r WHERE r.isBlocked = false";
        return em.createQuery(jpql, Long.class).getSingleResult();
    }

    @Override
    public List<ArtistReview> findByArtistId(ArtistId artistId, Integer page, Integer size) {
        String jpql = "SELECT r FROM ArtistReviewJpaEntity r WHERE r.artistId = :artistId AND r.isBlocked = false ORDER BY r.createdAt DESC";
        TypedQuery<ArtistReviewJpaEntity> query = em.createQuery(jpql, ArtistReviewJpaEntity.class);
        query.setParameter("artistId", artistId.getValue());

        if (page != null && size != null) {
            query.setFirstResult(page * size);
            query.setMaxResults(size);
        }

        return query.getResultStream()
            .map(mapper::toDomain)
            .map(r -> (ArtistReview) r)
            .collect(Collectors.toList());
    }

    @Override
    public List<AlbumReview> findByAlbumId(AlbumId albumId, Integer page, Integer size) {
        String jpql = "SELECT r FROM AlbumReviewJpaEntity r WHERE r.albumId = :albumId AND r.isBlocked = false ORDER BY r.createdAt DESC";
        TypedQuery<AlbumReviewJpaEntity> query = em.createQuery(jpql, AlbumReviewJpaEntity.class);
        query.setParameter("albumId", albumId.getValue());

        if (page != null && size != null) {
            query.setFirstResult(page * size);
            query.setMaxResults(size);
        }

        return query.getResultStream()
            .map(mapper::toDomain)
            .map(r -> (AlbumReview) r)
            .collect(Collectors.toList());
    }

    @Override
    public List<SongReview> findBySongId(SongId songId, Integer page, Integer size) {
        String jpql = "SELECT r FROM SongReviewJpaEntity r WHERE r.songId = :songId AND r.isBlocked = false ORDER BY r.createdAt DESC";
        TypedQuery<SongReviewJpaEntity> query = em.createQuery(jpql, SongReviewJpaEntity.class);
        query.setParameter("songId", songId.getValue());

        if (page != null && size != null) {
            query.setFirstResult(page * size);
            query.setMaxResults(size);
        }

        return query.getResultStream()
            .map(mapper::toDomain)
            .map(r -> (SongReview) r)
            .collect(Collectors.toList());
    }

    @Override
    public List<Review> findByUserId(UserId userId, Integer page, Integer size) {
        String jpql = "SELECT r FROM ReviewJpaEntity r WHERE r.userId = :userId AND r.isBlocked = false ORDER BY r.createdAt DESC";
        TypedQuery<ReviewJpaEntity> query = em.createQuery(jpql, ReviewJpaEntity.class);
        query.setParameter("userId", userId.getValue());

        if (page != null && size != null) {
            query.setFirstResult(page * size);
            query.setMaxResults(size);
        }

        return query.getResultStream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public Optional<ArtistReview> findArtistReviewByUserAndArtist(UserId userId, ArtistId artistId) {
        String jpql = "SELECT r FROM ArtistReviewJpaEntity r WHERE r.userId = :userId AND r.artistId = :artistId";
        List<ArtistReviewJpaEntity> results = em.createQuery(jpql, ArtistReviewJpaEntity.class)
            .setParameter("userId", userId.getValue())
            .setParameter("artistId", artistId.getValue())
            .setMaxResults(1)
            .getResultList();

        return results.isEmpty()
            ? Optional.empty()
            : Optional.of((ArtistReview) mapper.toDomain(results.get(0)));
    }

    @Override
    public Optional<AlbumReview> findAlbumReviewByUserAndAlbum(UserId userId, AlbumId albumId) {
        String jpql = "SELECT r FROM AlbumReviewJpaEntity r WHERE r.userId = :userId AND r.albumId = :albumId";
        List<AlbumReviewJpaEntity> results = em.createQuery(jpql, AlbumReviewJpaEntity.class)
            .setParameter("userId", userId.getValue())
            .setParameter("albumId", albumId.getValue())
            .setMaxResults(1)
            .getResultList();

        return results.isEmpty()
            ? Optional.empty()
            : Optional.of((AlbumReview) mapper.toDomain(results.get(0)));
    }

    @Override
    public Optional<SongReview> findSongReviewByUserAndSong(UserId userId, SongId songId) {
        String jpql = "SELECT r FROM SongReviewJpaEntity r WHERE r.userId = :userId AND r.songId = :songId";
        List<SongReviewJpaEntity> results = em.createQuery(jpql, SongReviewJpaEntity.class)
            .setParameter("userId", userId.getValue())
            .setParameter("songId", songId.getValue())
            .setMaxResults(1)
            .getResultList();

        return results.isEmpty()
            ? Optional.empty()
            : Optional.of((SongReview) mapper.toDomain(results.get(0)));
    }

    @Override
    public Long countByArtistId(ArtistId artistId) {
        String jpql = "SELECT COUNT(r) FROM ArtistReviewJpaEntity r WHERE r.artistId = :artistId AND r.isBlocked = false";
        return em.createQuery(jpql, Long.class)
            .setParameter("artistId", artistId.getValue())
            .getSingleResult();
    }

    @Override
    public Long countByAlbumId(AlbumId albumId) {
        String jpql = "SELECT COUNT(r) FROM AlbumReviewJpaEntity r WHERE r.albumId = :albumId AND r.isBlocked = false";
        return em.createQuery(jpql, Long.class)
            .setParameter("albumId", albumId.getValue())
            .getSingleResult();
    }

    @Override
    public Long countBySongId(SongId songId) {
        String jpql = "SELECT COUNT(r) FROM SongReviewJpaEntity r WHERE r.songId = :songId AND r.isBlocked = false";
        return em.createQuery(jpql, Long.class)
            .setParameter("songId", songId.getValue())
            .getSingleResult();
    }

    @Override
    public Long countByUserId(UserId userId) {
        String jpql = "SELECT COUNT(r) FROM ReviewJpaEntity r WHERE r.userId = :userId AND r.isBlocked = false";
        return em.createQuery(jpql, Long.class)
            .setParameter("userId", userId.getValue())
            .getSingleResult();
    }

    @Override
    public void addLike(ReviewId reviewId, UserId userId) {
        String sql = "INSERT INTO review_like (user_id, review_id) VALUES (:userId, :reviewId)";
        em.createNativeQuery(sql)
            .setParameter("userId", userId.getValue())
            .setParameter("reviewId", reviewId.getValue())
            .executeUpdate();

        updateLikeCount(reviewId);
    }

    @Override
    public void removeLike(ReviewId reviewId, UserId userId) {
        String sql = "DELETE FROM review_like WHERE user_id = :userId AND review_id = :reviewId";
        em.createNativeQuery(sql)
            .setParameter("userId", userId.getValue())
            .setParameter("reviewId", reviewId.getValue())
            .executeUpdate();

        updateLikeCount(reviewId);
    }

    @Override
    public boolean isLikedByUser(ReviewId reviewId, UserId userId) {
        String sql = "SELECT COUNT(*) FROM review_like WHERE user_id = :userId AND review_id = :reviewId";
        Number count = (Number) em.createNativeQuery(sql)
            .setParameter("userId", userId.getValue())
            .setParameter("reviewId", reviewId.getValue())
            .getSingleResult();

        return count.intValue() > 0;
    }

    @Override
    public List<Review> findLikedByUser(UserId userId, Integer page, Integer size) {
        String jpql = "SELECT r FROM ReviewJpaEntity r WHERE r.id IN " +
                     "(SELECT rl.reviewId FROM review_like rl WHERE rl.userId = :userId) " +
                     "ORDER BY r.createdAt DESC";
        TypedQuery<ReviewJpaEntity> query = em.createQuery(jpql, ReviewJpaEntity.class);
        query.setParameter("userId", userId.getValue());

        if (page != null && size != null) {
            query.setFirstResult(page * size);
            query.setMaxResults(size);
        }

        return query.getResultStream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public Long countLikedByUser(UserId userId) {
        String sql = "SELECT COUNT(*) FROM review_like WHERE user_id = :userId";
        Number count = (Number) em.createNativeQuery(sql)
            .setParameter("userId", userId.getValue())
            .getSingleResult();

        return count.longValue();
    }

    private void updateLikeCount(ReviewId reviewId) {
        String countSql = "SELECT COUNT(*) FROM review_like WHERE review_id = :reviewId";
        Number count = (Number) em.createNativeQuery(countSql)
            .setParameter("reviewId", reviewId.getValue())
            .getSingleResult();

        String updateSql = "UPDATE review SET likes = :likes WHERE id = :reviewId";
        em.createNativeQuery(updateSql)
            .setParameter("likes", count.intValue())
            .setParameter("reviewId", reviewId.getValue())
            .executeUpdate();
    }
}
