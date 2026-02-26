package ar.edu.itba.paw.persistence.mappers;

import ar.edu.itba.paw.domain.album.Album;
import ar.edu.itba.paw.domain.album.AlbumId;
import ar.edu.itba.paw.domain.artist.ArtistId;
import ar.edu.itba.paw.infrastructure.jpa.AlbumJpaEntity;
import ar.edu.itba.paw.models.Artist;
import ar.edu.itba.paw.models.Image;
import org.springframework.stereotype.Component;

/**
 * Mapper between AlbumJpaEntity (persistence) and Album (domain).
 * Responsible for translating between the two representations.
 *
 * Part of the Hexagonal Architecture migration (Phase 4).
 */
@Component
public class AlbumMapper {

    /**
     * Converts a JPA entity to a domain entity.
     *
     * @param entity JPA entity
     * @return Domain entity
     */
    public Album toDomain(AlbumJpaEntity entity) {
        if (entity == null) {
            return null;
        }

        return Album.reconstitute(
                new AlbumId(entity.getId()),
                entity.getTitle(),
                entity.getGenre(),
                new ArtistId(entity.getArtist().getId()),
                entity.getImage().getId(),
                entity.getReleaseDate(),
                entity.getAvgRating(),
                entity.getRatingCount(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    /**
     * Converts a domain entity to a JPA entity.
     * Requires Artist and Image entities to be loaded for relationships.
     *
     * @param domain Domain entity
     * @param artist Artist entity (loaded from database)
     * @param image Image entity (loaded from database)
     * @return JPA entity
     */
    public AlbumJpaEntity toEntity(Album domain, Artist artist, Image image) {
        if (domain == null) {
            return null;
        }

        AlbumJpaEntity entity = new AlbumJpaEntity();

        if (domain.getId() != null) {
            entity.setId(domain.getId().getValue());
        }

        entity.setTitle(domain.getTitle());
        entity.setGenre(domain.getGenre());
        entity.setReleaseDate(domain.getReleaseDate());
        entity.setAvgRating(domain.getAvgRating());
        entity.setRatingCount(domain.getRatingCount());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        entity.setArtist(artist);
        entity.setImage(image);

        return entity;
    }
}
