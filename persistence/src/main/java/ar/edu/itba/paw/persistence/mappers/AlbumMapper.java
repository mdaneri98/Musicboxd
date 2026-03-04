package ar.edu.itba.paw.persistence.mappers;

import ar.edu.itba.paw.domain.album.Album;
import ar.edu.itba.paw.domain.album.AlbumId;
import ar.edu.itba.paw.infrastructure.jpa.AlbumJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class AlbumMapper {

    public Album toDomain(AlbumJpaEntity entity) {
        if (entity == null) {
            return null;
        }

        return Album.reconstitute(
            new AlbumId(entity.getId()),
            entity.getTitle(),
            entity.getGenre(),
            entity.getReleaseDate(),
            entity.getImageId(),
            entity.getArtistId(),
            entity.getRatingCount(),
            entity.getAvgRating(),
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }

    public AlbumJpaEntity toJpaEntity(Album domain) {
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
        entity.setImageId(domain.getImageId());
        entity.setArtistId(domain.getArtistId());
        entity.setRatingCount(domain.getRatingCount());
        entity.setAvgRating(domain.getAvgRating());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());

        return entity;
    }
}
