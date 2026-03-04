package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.domain.album.Album;
import ar.edu.itba.paw.domain.album.AlbumId;
import ar.edu.itba.paw.domain.album.AlbumRepository;
import ar.edu.itba.paw.infrastructure.jpa.AlbumJpaEntity;
import ar.edu.itba.paw.persistence.mappers.AlbumMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class AlbumRepositoryJpa implements AlbumRepository {

    @PersistenceContext
    private EntityManager em;

    private final AlbumMapper mapper;

    @Autowired
    public AlbumRepositoryJpa(AlbumMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public Optional<Album> findById(AlbumId id) {
        AlbumJpaEntity entity = em.find(AlbumJpaEntity.class, id.getValue());
        return Optional.ofNullable(entity).map(mapper::toDomain);
    }

    @Override
    public Map<Long, Album> findByIds(Set<Long> ids) {
        if (ids.isEmpty()) {
            return Collections.emptyMap();
        }

        String jpql = "SELECT a FROM AlbumJpaEntity a WHERE a.id IN :ids";
        List<AlbumJpaEntity> entities = em.createQuery(jpql, AlbumJpaEntity.class)
            .setParameter("ids", ids)
            .getResultList();

        return entities.stream()
            .map(mapper::toDomain)
            .collect(Collectors.toMap(
                album -> album.getId().getValue(),
                album -> album
            ));
    }

    @Override
    public Album save(Album album) {
        AlbumJpaEntity entity = mapper.toJpaEntity(album);

        if (album.getId() == null) {
            em.persist(entity);
        } else {
            entity = em.merge(entity);
        }

        em.flush();
        return mapper.toDomain(entity);
    }

    @Override
    public void delete(AlbumId id) {
        AlbumJpaEntity entity = em.find(AlbumJpaEntity.class, id.getValue());
        if (entity != null) {
            em.remove(entity);
        }
    }

    @Override
    public List<Album> findAll(Integer page, Integer size) {
        String jpql = "SELECT a FROM AlbumJpaEntity a ORDER BY a.title";
        TypedQuery<AlbumJpaEntity> query = em.createQuery(jpql, AlbumJpaEntity.class);

        if (page != null && size != null) {
            query.setFirstResult(page * size);
            query.setMaxResults(size);
        }

        return query.getResultList().stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Album> findByArtistId(Long artistId) {
        String jpql = "SELECT a FROM AlbumJpaEntity a WHERE a.artistId = :artistId ORDER BY a.title";
        return em.createQuery(jpql, AlbumJpaEntity.class)
            .setParameter("artistId", artistId)
            .getResultList().stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Album> findByTitleContaining(String substring, Integer page, Integer size) {
        Query nativeQuery = em.createNativeQuery(
            "SELECT a.id FROM album a " +
            "WHERE LOWER(a.title) LIKE LOWER(:sub) " +
            "ORDER BY a.title");
        nativeQuery.setParameter("sub", "%" + substring + "%");

        if (page != null && size != null) {
            nativeQuery.setFirstResult(page * size);
            nativeQuery.setMaxResults(size);
        }

        @SuppressWarnings("unchecked")
        List<Object> rawResults = nativeQuery.getResultList();

        if (rawResults.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> albumIds = rawResults.stream()
            .map(n -> ((Number) n).longValue())
            .collect(Collectors.toList());

        TypedQuery<AlbumJpaEntity> query = em.createQuery(
            "SELECT a FROM AlbumJpaEntity a WHERE a.id IN :ids ORDER BY a.title",
            AlbumJpaEntity.class);
        query.setParameter("ids", albumIds);

        return query.getResultList().stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public Long countAll() {
        String jpql = "SELECT COUNT(a) FROM AlbumJpaEntity a";
        return em.createQuery(jpql, Long.class).getSingleResult();
    }

    @Override
    public boolean hasUserReviewed(Long userId, AlbumId albumId) {
        String jpql = "SELECT COUNT(r) FROM AlbumReview r " +
                     "WHERE r.user.id = :userId AND r.album.id = :albumId AND r.isBlocked = false";
        Long count = em.createQuery(jpql, Long.class)
            .setParameter("userId", userId)
            .setParameter("albumId", albumId.getValue())
            .getSingleResult();
        return count > 0;
    }

    @Override
    public boolean deleteReviewsFromAlbum(AlbumId albumId) {
        Query query = em.createQuery(
            "DELETE FROM Review r WHERE r.id IN " +
            "(SELECT ar.id FROM AlbumReview ar WHERE ar.album.id = :albumId)");
        query.setParameter("albumId", albumId.getValue());
        return query.executeUpdate() >= 1;
    }
}
