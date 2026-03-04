package ar.edu.itba.paw.persistence.mappers;

import ar.edu.itba.paw.domain.artist.Artist;
import ar.edu.itba.paw.domain.artist.ArtistId;
import ar.edu.itba.paw.infrastructure.jpa.ArtistJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class ArtistMapper {

    public Artist toDomain(ArtistJpaEntity entity) {
        if (entity == null) {
            return null;
        }

        return Artist.reconstitute(
            new ArtistId(entity.getId()),
            entity.getName(),
            entity.getBio(),
            entity.getImageId(),
            entity.getRatingCount(),
            entity.getAvgRating(),
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }

    public ArtistJpaEntity toJpaEntity(Artist domain) {
        if (domain == null) {
            return null;
        }

        ArtistJpaEntity entity = new ArtistJpaEntity();
        if (domain.getId() != null) {
            entity.setId(domain.getId().getValue());
        }
        entity.setName(domain.getName());
        entity.setBio(domain.getBio());
        entity.setImageId(domain.getImageId());
        entity.setRatingCount(domain.getRatingCount());
        entity.setAvgRating(domain.getAvgRating());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());

        return entity;
    }
}
