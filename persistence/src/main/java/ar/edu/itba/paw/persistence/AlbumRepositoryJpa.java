package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.domain.album.Album;
import ar.edu.itba.paw.domain.album.AlbumId;
import ar.edu.itba.paw.domain.album.AlbumRepository;
import ar.edu.itba.paw.domain.artist.ArtistId;
import ar.edu.itba.paw.infrastructure.jpa.AlbumJpaEntity;
import ar.edu.itba.paw.models.Artist;
import ar.edu.itba.paw.models.Image;
import ar.edu.itba.paw.persistence.mappers.AlbumMapper;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * JPA implementation of AlbumRepository.
 * Adapts JPA persistence to the domain repository port.
 *
 * Part of the Hexagonal Architecture migration (Phase 4).
 */
@Repository
public class AlbumRepositoryJpa implements AlbumRepository {

    @PersistenceContext
    private EntityManager em;

    private final AlbumMapper mapper;

    public AlbumRepositoryJpa(AlbumMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public Optional<Album> findById(AlbumId id) {
        if (id == null) {
            return Optional.empty();
        }

        AlbumJpaEntity entity = em.find(AlbumJpaEntity.class, id.getValue());
        return Optional.ofNullable(entity).map(mapper::toDomain);
    }

    @Override
    public List<Album> findByArtist(ArtistId artistId) {
        TypedQuery<AlbumJpaEntity> query = em.createQuery(
                "SELECT a FROM AlbumJpaEntity a WHERE a.artist.id = :artistId",
                AlbumJpaEntity.class
        );
        query.setParameter("artistId", artistId.getValue());

        return query.getResultList().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Album> findByTitleContaining(String substring, Integer page, Integer size) {
        TypedQuery<AlbumJpaEntity> query = em.createQuery(
                "SELECT a FROM AlbumJpaEntity a WHERE LOWER(a.title) LIKE LOWER(:substring)",
                AlbumJpaEntity.class
        );
        query.setParameter("substring", "%" + substring + "%");
        query.setFirstResult((page - 1) * size);
        query.setMaxResults(size);

        return query.getResultList().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Album> findPaginated(Integer page, Integer size) {
        TypedQuery<AlbumJpaEntity> query = em.createQuery(
                "SELECT a FROM AlbumJpaEntity a ORDER BY a.createdAt DESC",
                AlbumJpaEntity.class
        );
        query.setFirstResult(page);
        query.setMaxResults(size);

        return query.getResultList().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Album save(Album album) {
        // Load dependencies
        Artist artist = em.find(Artist.class, album.getArtistId().getValue());
        Image image = em.find(Image.class, album.getImageId());

        if (artist == null) {
            throw new IllegalStateException("Artist not found: " + album.getArtistId());
        }
        if (image == null) {
            throw new IllegalStateException("Image not found: " + album.getImageId());
        }

        AlbumJpaEntity entity = mapper.toEntity(album, artist, image);

        if (entity.getId() == null) {
            em.persist(entity);
        } else {
            entity = em.merge(entity);
        }

        em.flush();
        return mapper.toDomain(entity);
    }

    @Override
    public Boolean delete(AlbumId id) {
        AlbumJpaEntity entity = em.find(AlbumJpaEntity.class, id.getValue());
        if (entity != null) {
            em.remove(entity);
            return true;
        }
        return false;
    }

    @Override
    public Boolean updateRating(AlbumId albumId, double avgRating, int ratingCount) {
        AlbumJpaEntity entity = em.find(AlbumJpaEntity.class, albumId.getValue());
        if (entity != null) {
            entity.setAvgRating(avgRating);
            entity.setRatingCount(ratingCount);
            em.merge(entity);
            return true;
        }
        return false;
    }

    @Override
    public Boolean hasUserReviewed(Long userId, AlbumId albumId) {
        Long count = em.createQuery(
                "SELECT COUNT(r) FROM AlbumReview r WHERE r.user.id = :userId AND r.album.id = :albumId",
                Long.class
        )
                .setParameter("userId", userId)
                .setParameter("albumId", albumId.getValue())
                .getSingleResult();

        return count > 0;
    }

    @Override
    public Long countAll() {
        return em.createQuery("SELECT COUNT(a) FROM AlbumJpaEntity a", Long.class)
                .getSingleResult();
    }
}
