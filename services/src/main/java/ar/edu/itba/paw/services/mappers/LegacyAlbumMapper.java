package ar.edu.itba.paw.services.mappers;

import ar.edu.itba.paw.domain.album.Album;
import ar.edu.itba.paw.domain.album.AlbumId;
import ar.edu.itba.paw.models.Artist;
import ar.edu.itba.paw.models.Image;
import org.springframework.stereotype.Component;

@Component
public class LegacyAlbumMapper {

    public ar.edu.itba.paw.models.Album toLegacyModel(Album domain) {
        if (domain == null) {
            return null;
        }

        Image image = new Image(domain.getImageId(), null);
        Artist artist = domain.getArtistId() != null ? new Artist(domain.getArtistId()) : null;

        return new ar.edu.itba.paw.models.Album(
            domain.getId() != null ? domain.getId().getValue() : null,
            domain.getTitle(),
            domain.getGenre(),
            domain.getReleaseDate(),
            domain.getCreatedAt(),
            domain.getUpdatedAt(),
            image,
            artist,
            domain.getRatingCount(),
            domain.getAvgRating()
        );
    }

    public Album fromLegacyModel(ar.edu.itba.paw.models.Album legacy) {
        if (legacy == null) {
            return null;
        }

        if (legacy.getId() == null) {
            return Album.create(
                legacy.getTitle(),
                legacy.getGenre(),
                legacy.getReleaseDate(),
                legacy.getImage() != null ? legacy.getImage().getId() : null,
                legacy.getArtist() != null ? legacy.getArtist().getId() : null
            );
        } else {
            return Album.reconstitute(
                new AlbumId(legacy.getId()),
                legacy.getTitle(),
                legacy.getGenre(),
                legacy.getReleaseDate(),
                legacy.getImage() != null ? legacy.getImage().getId() : null,
                legacy.getArtist() != null ? legacy.getArtist().getId() : null,
                legacy.getRatingCount(),
                legacy.getAvgRating(),
                legacy.getCreatedAt(),
                legacy.getUpdatedAt()
            );
        }
    }
}
