package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.domain.artist.Artist;
import ar.edu.itba.paw.domain.artist.ArtistId;
import ar.edu.itba.paw.domain.artist.ArtistRepository;
import ar.edu.itba.paw.infrastructure.jpa.ArtistJpaEntity;
import ar.edu.itba.paw.persistence.mappers.ArtistMapper;
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
public class ArtistRepositoryJpa implements ArtistRepository {

    @PersistenceContext
    private EntityManager em;

    private final ArtistMapper mapper;

    @Autowired
    public ArtistRepositoryJpa(ArtistMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public Optional<Artist> findById(ArtistId id) {
        ArtistJpaEntity entity = em.find(ArtistJpaEntity.class, id.getValue());
        return Optional.ofNullable(entity).map(mapper::toDomain);
    }

    @Override
    public Map<Long, Artist> findByIds(Set<Long> ids) {
        if (ids.isEmpty()) {
            return Collections.emptyMap();
        }

        String jpql = "SELECT a FROM ArtistJpaEntity a WHERE a.id IN :ids";
        List<ArtistJpaEntity> entities = em.createQuery(jpql, ArtistJpaEntity.class)
            .setParameter("ids", ids)
            .getResultList();

        return entities.stream()
            .map(mapper::toDomain)
            .collect(Collectors.toMap(
                artist -> artist.getId().getValue(),
                artist -> artist
            ));
    }

    @Override
    public Artist save(Artist artist) {
        ArtistJpaEntity entity = mapper.toJpaEntity(artist);

        if (artist.getId() == null) {
            em.persist(entity);
        } else {
            entity = em.merge(entity);
        }

        em.flush();
        return mapper.toDomain(entity);
    }

    @Override
    public void delete(ArtistId id) {
        ArtistJpaEntity entity = em.find(ArtistJpaEntity.class, id.getValue());
        if (entity != null) {
            em.remove(entity);
        }
    }

    @Override
    public List<Artist> findAll(Integer page, Integer size) {
        String jpql = "SELECT a FROM ArtistJpaEntity a ORDER BY a.name";
        TypedQuery<ArtistJpaEntity> query = em.createQuery(jpql, ArtistJpaEntity.class);

        if (page != null && size != null) {
            query.setFirstResult(page * size);
            query.setMaxResults(size);
        }

        return query.getResultList().stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Artist> findBySongId(Long songId) {
        String nativeSQL = "SELECT DISTINCT a.* FROM artist a " +
                          "JOIN song_artists sa ON a.id = sa.artist_id " +
                          "WHERE sa.song_id = :songId";

        @SuppressWarnings("unchecked")
        List<Object[]> results = em.createNativeQuery(nativeSQL)
            .setParameter("songId", songId)
            .getResultList();

        if (results.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> artistIds = results.stream()
            .map(row -> ((Number) row[0]).longValue())
            .collect(Collectors.toList());

        String jpql = "SELECT a FROM ArtistJpaEntity a WHERE a.id IN :ids ORDER BY a.name";
        return em.createQuery(jpql, ArtistJpaEntity.class)
            .setParameter("ids", artistIds)
            .getResultList().stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Artist> findByNameContaining(String substring, Integer page, Integer size) {
        Query nativeQuery = em.createNativeQuery(
            "SELECT a.id FROM artist a " +
            "WHERE LOWER(a.name) LIKE LOWER(:name) " +
            "ORDER BY a.name");
        nativeQuery.setParameter("name", "%" + substring + "%");

        if (page != null && size != null) {
            nativeQuery.setFirstResult(page * size);
            nativeQuery.setMaxResults(size);
        }

        @SuppressWarnings("unchecked")
        List<Object> rawResults = nativeQuery.getResultList();

        if (rawResults.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> artistIds = rawResults.stream()
            .map(n -> ((Number) n).longValue())
            .collect(Collectors.toList());

        TypedQuery<ArtistJpaEntity> query = em.createQuery(
            "SELECT a FROM ArtistJpaEntity a WHERE a.id IN :ids ORDER BY a.name",
            ArtistJpaEntity.class);
        query.setParameter("ids", artistIds);

        return query.getResultList().stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public Long countAll() {
        String jpql = "SELECT COUNT(a) FROM ArtistJpaEntity a";
        return em.createQuery(jpql, Long.class).getSingleResult();
    }

    @Override
    public boolean hasUserReviewed(Long userId, ArtistId artistId) {
        String jpql = "SELECT COUNT(r) FROM ArtistReview r " +
                     "WHERE r.user.id = :userId AND r.artist.id = :artistId AND r.isBlocked = false";
        Long count = em.createQuery(jpql, Long.class)
            .setParameter("userId", userId)
            .setParameter("artistId", artistId.getValue())
            .getSingleResult();
        return count > 0;
    }

    @Override
    public boolean deleteReviewsFromArtist(ArtistId artistId) {
        Query query = em.createQuery(
            "DELETE FROM Review r WHERE r.id IN " +
            "(SELECT ar.id FROM ArtistReview ar WHERE ar.artist.id = :artistId)");
        query.setParameter("artistId", artistId.getValue());
        return query.executeUpdate() >= 1;
    }
}
