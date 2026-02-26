package ar.edu.itba.paw.persistence.mappers;

import ar.edu.itba.paw.infrastructure.jpa.AlbumJpaEntity;
import ar.edu.itba.paw.models.Album;
import ar.edu.itba.paw.models.Artist;
import ar.edu.itba.paw.models.Image;
import org.springframework.stereotype.Component;

/**
 * Mapper between AlbumJpaEntity and legacy Album model.
 * Used during the incremental migration to Hexagonal Architecture.
 *
 * This mapper ensures backward compatibility while using the new JPA entity structure.
 * Once the migration is complete, this mapper can be removed.
 *
 * Part of the Hexagonal Architecture migration (Phase 4).
 */
@Component
public class LegacyAlbumMapper {

    /**
     * Converts a JPA entity to a legacy Album model.
     *
     * @param entity JPA entity
     * @return Legacy Album model
     */
    public Album toLegacy(AlbumJpaEntity entity) {
        if (entity == null) {
            return null;
        }

        Album album = new Album();
        album.setId(entity.getId());
        album.setTitle(entity.getTitle());
        album.setGenre(entity.getGenre());
        album.setReleaseDate(entity.getReleaseDate());
        album.setCreatedAt(entity.getCreatedAt());
        album.setUpdatedAt(entity.getUpdatedAt());
        album.setImage(entity.getImage());
        album.setArtist(entity.getArtist());
        album.setRatingCount(entity.getRatingCount());
        album.setAvgRating(entity.getAvgRating());
        album.setSongs(entity.getSongs());

        return album;
    }

    /**
     * Converts a legacy Album model to a JPA entity.
     *
     * @param album Legacy Album model
     * @return JPA entity
     */
    public AlbumJpaEntity toEntity(Album album) {
        if (album == null) {
            return null;
        }

        AlbumJpaEntity entity = new AlbumJpaEntity();
        entity.setId(album.getId());
        entity.setTitle(album.getTitle());
        entity.setGenre(album.getGenre());
        entity.setReleaseDate(album.getReleaseDate());
        entity.setCreatedAt(album.getCreatedAt());
        entity.setUpdatedAt(album.getUpdatedAt());
        entity.setImage(album.getImage());
        entity.setArtist(album.getArtist());
        entity.setRatingCount(album.getRatingCount());
        entity.setAvgRating(album.getAvgRating());
        entity.setSongs(album.getSongs());

        return entity;
    }

    /**
     * Converts a JPA entity to a legacy Album model, requiring explicit dependencies.
     * Used when creating new entities with separate Artist and Image objects.
     *
     * @param entity JPA entity
     * @param artist Artist entity
     * @param image Image entity
     * @return Legacy Album model
     */
    public Album toLegacy(AlbumJpaEntity entity, Artist artist, Image image) {
        if (entity == null) {
            return null;
        }

        Album album = new Album();
        album.setId(entity.getId());
        album.setTitle(entity.getTitle());
        album.setGenre(entity.getGenre());
        album.setReleaseDate(entity.getReleaseDate());
        album.setCreatedAt(entity.getCreatedAt());
        album.setUpdatedAt(entity.getUpdatedAt());
        album.setImage(image != null ? image : entity.getImage());
        album.setArtist(artist != null ? artist : entity.getArtist());
        album.setRatingCount(entity.getRatingCount());
        album.setAvgRating(entity.getAvgRating());
        album.setSongs(entity.getSongs());

        return album;
    }
}
