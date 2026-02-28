package ar.edu.itba.paw.services.mappers;

import ar.edu.itba.paw.domain.song.Song;
import ar.edu.itba.paw.domain.song.SongId;
import ar.edu.itba.paw.models.Album;
import ar.edu.itba.paw.models.Artist;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class LegacySongMapper {

    public ar.edu.itba.paw.models.Song toLegacyModel(Song domain) {
        if (domain == null) {
            return null;
        }

        Album album = new Album(domain.getAlbumId());

        List<Artist> artists = domain.getArtistIds().stream()
            .map(Artist::new)
            .collect(Collectors.toList());

        ar.edu.itba.paw.models.Song legacy = new ar.edu.itba.paw.models.Song(
            domain.getId() != null ? domain.getId().getValue() : null,
            domain.getTitle(),
            domain.getDuration(),
            domain.getTrackNumber(),
            domain.getCreatedAt(),
            domain.getUpdatedAt(),
            album,
            domain.getRatingCount(),
            domain.getAvgRating()
        );

        legacy.setArtists(artists);

        return legacy;
    }

    public Song fromLegacyModel(ar.edu.itba.paw.models.Song legacy) {
        if (legacy == null) {
            return null;
        }

        List<Long> artistIds = legacy.getArtists() != null ?
            legacy.getArtists().stream()
                .map(Artist::getId)
                .collect(Collectors.toList()) :
            List.of();

        if (legacy.getId() == null) {
            return Song.create(
                legacy.getTitle(),
                legacy.getDuration(),
                legacy.getTrackNumber(),
                legacy.getAlbum() != null ? legacy.getAlbum().getId() : null,
                artistIds
            );
        } else {
            return Song.reconstitute(
                new SongId(legacy.getId()),
                legacy.getTitle(),
                legacy.getDuration(),
                legacy.getTrackNumber(),
                legacy.getAlbum() != null ? legacy.getAlbum().getId() : null,
                artistIds,
                legacy.getRatingCount(),
                legacy.getAvgRating(),
                legacy.getCreatedAt(),
                legacy.getUpdatedAt()
            );
        }
    }
}
