package ar.edu.itba.paw.persistence.mappers;

import ar.edu.itba.paw.domain.song.Song;
import ar.edu.itba.paw.domain.song.SongId;
import ar.edu.itba.paw.infrastructure.jpa.SongJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class SongMapper {

    public Song toDomain(SongJpaEntity entity) {
        if (entity == null) {
            return null;
        }

        return Song.reconstitute(
            new SongId(entity.getId()),
            entity.getTitle(),
            entity.getDuration(),
            entity.getTrackNumber(),
            entity.getAlbumId(),
            entity.getArtistIds(),
            entity.getRatingCount(),
            entity.getAvgRating(),
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }

    public SongJpaEntity toJpaEntity(Song domain) {
        if (domain == null) {
            return null;
        }

        SongJpaEntity entity = new SongJpaEntity();
        if (domain.getId() != null) {
            entity.setId(domain.getId().getValue());
        }
        entity.setTitle(domain.getTitle());
        entity.setDuration(domain.getDuration());
        entity.setTrackNumber(domain.getTrackNumber());
        entity.setAlbumId(domain.getAlbumId());
        entity.setArtistIds(domain.getArtistIds());
        entity.setRatingCount(domain.getRatingCount());
        entity.setAvgRating(domain.getAvgRating());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());

        return entity;
    }
}
