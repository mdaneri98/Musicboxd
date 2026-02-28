package ar.edu.itba.paw.services.mappers;

import ar.edu.itba.paw.domain.artist.Artist;
import ar.edu.itba.paw.models.Image;
import org.springframework.stereotype.Component;

@Component
public class LegacyArtistMapper {

    public ar.edu.itba.paw.models.Artist toLegacyModel(Artist domain) {
        if (domain == null) {
            return null;
        }

        Image image = new Image(domain.getImageId(), null);

        return new ar.edu.itba.paw.models.Artist(
            domain.getId() != null ? domain.getId().getValue() : null,
            domain.getName(),
            domain.getBio(),
            domain.getCreatedAt(),
            domain.getUpdatedAt(),
            image,
            domain.getRatingCount(),
            domain.getAvgRating()
        );
    }

    public Artist fromLegacyModel(ar.edu.itba.paw.models.Artist legacy) {
        if (legacy == null) {
            return null;
        }

        if (legacy.getId() == null) {
            return Artist.create(
                legacy.getName(),
                legacy.getBio(),
                legacy.getImage() != null ? legacy.getImage().getId() : null
            );
        } else {
            return Artist.reconstitute(
                new ar.edu.itba.paw.domain.artist.ArtistId(legacy.getId()),
                legacy.getName(),
                legacy.getBio(),
                legacy.getImage() != null ? legacy.getImage().getId() : null,
                legacy.getRatingCount(),
                legacy.getAvgRating(),
                legacy.getCreatedAt(),
                legacy.getUpdatedAt()
            );
        }
    }
}
