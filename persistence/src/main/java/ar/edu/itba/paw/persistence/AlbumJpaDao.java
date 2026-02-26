package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.infrastructure.jpa.AlbumJpaEntity;
import ar.edu.itba.paw.models.Album;
import ar.edu.itba.paw.models.FilterType;
import ar.edu.itba.paw.models.reviews.AlbumReview;
import ar.edu.itba.paw.persistence.mappers.LegacyAlbumMapper;

import org.springframework.beans.factory.annotation.Autowired;
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
public class AlbumJpaDao implements AlbumDao {

    @PersistenceContext
    private EntityManager entityManager;

    private final LegacyAlbumMapper mapper;

    @Autowired
    public AlbumJpaDao(LegacyAlbumMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public Optional<Album> findById(Long id) {
        AlbumJpaEntity entity = entityManager.find(AlbumJpaEntity.class, id);
        return Optional.ofNullable(entity).map(mapper::toLegacy);
    }

    @Override
    public List<Album> findAll() {
        TypedQuery<AlbumJpaEntity> query = entityManager.createQuery(
                "SELECT a FROM AlbumJpaEntity a JOIN FETCH a.artist", AlbumJpaEntity.class);
        return query.getResultList().stream()
                .map(mapper::toLegacy)
                .collect(Collectors.toList());
    }

    @Override
    public List<Album> findPaginated(FilterType filterType, Integer limit, Integer offset) {
        String nativeSQL = "SELECT a.id FROM album a " + filterType.getFilter();
        Query nativeQuery = entityManager.createNativeQuery(nativeSQL);
        nativeQuery.setFirstResult(offset);
        nativeQuery.setMaxResults(limit);

        @SuppressWarnings("unchecked")
        List<Object> rawResults = nativeQuery.getResultList();
        List<Long> albumIds = rawResults.stream()
                .map(n -> ((Number)n).longValue())
                .collect(Collectors.toList());

        if (albumIds.isEmpty()) {
            return Collections.emptyList();
        }

        String entityQuery = "SELECT a FROM AlbumJpaEntity a WHERE a.id IN :ids " + filterType.getFilter();
        TypedQuery<AlbumJpaEntity> query = entityManager.createQuery(entityQuery, AlbumJpaEntity.class);
        query.setParameter("ids", albumIds);

        return query.getResultList().stream()
                .map(mapper::toLegacy)
                .collect(Collectors.toList());
    }

    @Override
    public List<Album> findByTitleContaining(String sub, Integer page, Integer size) {
        Query nativeQuery = entityManager.createNativeQuery(
                "SELECT a.id FROM album a " +
                "WHERE LOWER(a.title) LIKE LOWER(:sub) " +
                "ORDER BY a.title"
        );
        nativeQuery.setParameter("sub", "%" + sub + "%");
        nativeQuery.setMaxResults(size);
        nativeQuery.setFirstResult((page - 1) * size);

        @SuppressWarnings("unchecked")
        List<Object> rawResults = nativeQuery.getResultList();
        List<Long> albumIds = rawResults.stream()
                .map(n -> ((Number)n).longValue())
                .collect(Collectors.toList());

        if (albumIds.isEmpty()) {
            return Collections.emptyList();
        }

        TypedQuery<AlbumJpaEntity> query = entityManager.createQuery(
                "SELECT a FROM AlbumJpaEntity a JOIN FETCH a.artist WHERE a.id IN :ids ORDER BY a.title",
                AlbumJpaEntity.class);
        query.setParameter("ids", albumIds);

        return query.getResultList().stream()
                .map(mapper::toLegacy)
                .collect(Collectors.toList());
    }

    @Override
    public List<Album> findByArtistId(Long artistId) {
        TypedQuery<AlbumJpaEntity> query = entityManager.createQuery(
                "SELECT a FROM AlbumJpaEntity a JOIN FETCH a.artist WHERE a.artist.id = :artistId",
                AlbumJpaEntity.class);
        query.setParameter("artistId", artistId);
        return query.getResultList().stream()
                .map(mapper::toLegacy)
                .collect(Collectors.toList());
    }

    @Override
    public Album create(Album album) {
        AlbumJpaEntity entity = mapper.toEntity(album);
        entityManager.persist(entity);
        return mapper.toLegacy(entity);
    }

    @Override
    public Album update(Album album) {
        AlbumJpaEntity entity = mapper.toEntity(album);
        AlbumJpaEntity merged = entityManager.merge(entity);
        return mapper.toLegacy(merged);
    }

    @Override
    public Boolean updateRating(Long albumId, Double newRating, Integer newRatingAmount) {
        // Actualizar la calificación del álbum
        AlbumJpaEntity album = entityManager.find(AlbumJpaEntity.class, albumId);
        if (album != null) {
            album.setAvgRating(newRating);
            album.setRatingCount(newRatingAmount);
            return true;
        }
        return false;
    }

    @Override
    public Boolean hasUserReviewed(Long userId, Long albumId) {
        // Comprobar si un usuario ha revisado un álbum
        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(ar) FROM AlbumReview ar WHERE ar.user.id = :userId AND ar.album.id = :albumId AND ar.isBlocked = false",
                Long.class
        );
        query.setParameter("userId", userId);
        query.setParameter("albumId", albumId);
        long count = query.getSingleResult();
        return count > 0;
    }

    @Override
    public Boolean delete(Long id) {
        // Eliminar un álbum por su ID
        AlbumJpaEntity album = entityManager.find(AlbumJpaEntity.class, id);
        if (album != null) {
            entityManager.remove(album);
            return true;
        }
        return false;
    }

    @Override
    public Boolean deleteReviewsFromAlbum(Long albumId) {
        Query query = entityManager.createQuery(
                "DELETE FROM Review r WHERE r.id IN " +
                        "(SELECT ar.id FROM AlbumReview ar WHERE ar.album.id = :albumId)");
        query.setParameter("albumId", albumId);
        return query.executeUpdate() >= 1;
    }

    @Override
    public List<AlbumReview> findReviewsByAlbumId(Long albumId) {
        final TypedQuery<AlbumReview> query = entityManager.createQuery(
                "FROM AlbumReview ar WHERE ar.album.id = :albumId AND ar.isBlocked = false ORDER BY ar.createdAt DESC",
                AlbumReview.class
        );
        query.setParameter("albumId", albumId);
        return query.getResultList();
    }

    @Override
    public Long countAll() {
        Query query = entityManager.createQuery("SELECT COUNT(a) FROM AlbumJpaEntity a");
        return (Long) query.getSingleResult();
    }

}
