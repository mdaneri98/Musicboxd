package ar.edu.itba.paw.persistence.mappers;

import ar.edu.itba.paw.infrastructure.jpa.AlbumJpaEntity;
import ar.edu.itba.paw.models.Album;
import org.springframework.stereotype.Component;

@Component
public class LegacyAlbumMapper {

    public Album toLegacy(AlbumJpaEntity entity) {
        if (entity == null) {
            return null;
        }

        Album legacy = new Album();
        legacy.setId(entity.getId());
        legacy.setTitle(entity.getTitle());
        legacy.setGenre(entity.getGenre());
        legacy.setReleaseDate(entity.getReleaseDate());
        legacy.setCreatedAt(entity.getCreatedAt());
        legacy.setUpdatedAt(entity.getUpdatedAt());
        legacy.setImage(entity.getImage());
        legacy.setRatingCount(entity.getRatingCount());
        legacy.setAvgRating(entity.getAvgRating());
        legacy.setArtist(entity.getArtist());
        legacy.setSongs(entity.getSongs());

        return legacy;
    }

    public AlbumJpaEntity toEntity(Album legacy) {
        if (legacy == null) {
            return null;
        }

        AlbumJpaEntity entity = new AlbumJpaEntity();
        entity.setId(legacy.getId());
        entity.setTitle(legacy.getTitle());
        entity.setGenre(legacy.getGenre());
        entity.setReleaseDate(legacy.getReleaseDate());
        entity.setCreatedAt(legacy.getCreatedAt());
        entity.setUpdatedAt(legacy.getUpdatedAt());
        entity.setImage(legacy.getImage());
        entity.setRatingCount(legacy.getRatingCount());
        entity.setAvgRating(legacy.getAvgRating());
        entity.setArtist(legacy.getArtist());
        entity.setSongs(legacy.getSongs());

        return entity;
    }
}
