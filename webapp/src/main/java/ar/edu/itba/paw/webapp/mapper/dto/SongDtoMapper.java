package ar.edu.itba.paw.webapp.mapper.dto;

import ar.edu.itba.paw.webapp.dto.SongDTO;
import ar.edu.itba.paw.models.Song;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.UriInfo;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper to convert between Song model and SongDTO
 */
@Component
public class SongDtoMapper {

    public SongDTO toDTO(Song song, UriInfo uriInfo) {
        if (song == null) {
            return null;
        }

        SongDTO dto = new SongDTO();
        dto.setId(song.getId());
        dto.setTitle(song.getTitle());
        dto.setDuration(song.getDuration());
        dto.setTrackNumber(song.getTrackNumber());
        dto.setAlbumId(song.getAlbum() != null ? song.getAlbum().getId() : null);
        dto.setAlbumTitle(song.getAlbum() != null ? song.getAlbum().getTitle() : null);
        dto.setAlbumImageId(song.getAlbum() != null && song.getAlbum().getImage() != null
                ? song.getAlbum().getImage().getId() : null);
        dto.setRatingCount(song.getRatingCount());
        dto.setAvgRating(song.getAvgRating());
        dto.setCreatedAt(song.getCreatedAt());
        dto.setUpdatedAt(song.getUpdatedAt());
        dto.setReleaseDate(song.getAlbum() != null && song.getAlbum().getReleaseDate() != null ? song.getAlbum().getReleaseDate() : null);
        if (song.getArtists() != null && !song.getArtists().isEmpty()) {
            dto.setArtistId(song.getArtists().get(0).getId());
        }

        // Build HATEOAS links
        if (uriInfo != null) {
            dto.setSelf(uriInfo.getBaseUriBuilder()
                    .path("songs").path(String.valueOf(song.getId())).build());

            if (song.getAlbum() != null) {
                dto.setAlbumLink(uriInfo.getBaseUriBuilder()
                        .path("albums").path(String.valueOf(song.getAlbum().getId())).build());
            }

            if (song.getArtists() != null && !song.getArtists().isEmpty()) {
                dto.setArtistLink(uriInfo.getBaseUriBuilder()
                        .path("artists").path(String.valueOf(song.getArtists().get(0).getId())).build());
            }

            dto.setReviewsLink(uriInfo.getBaseUriBuilder()
                    .path("songs").path(String.valueOf(song.getId())).path("reviews").build());
        }

        return dto;
    }

    public List<SongDTO> toDTOList(List<Song> songs, UriInfo uriInfo) {
        if (songs == null) {
            return null;
        }

        return songs.stream()
                .map(s -> toDTO(s, uriInfo))
                .collect(Collectors.toList());
    }

    public Song toModel(SongDTO dto) {
        if (dto == null) {
            return null;
        }

        Song song = new Song();
        song.setId(dto.getId());
        song.setTitle(dto.getTitle());
        song.setDuration(dto.getDuration());
        song.setTrackNumber(dto.getTrackNumber());
        song.setRatingCount(dto.getRatingCount());
        song.setAvgRating(dto.getAvgRating());
        song.setCreatedAt(dto.getCreatedAt());
        song.setUpdatedAt(dto.getUpdatedAt());

        return song;
    }

    public List<Song> toModelList(List<SongDTO> dtos) {
        if (dtos == null) {
            return null;
        }

        return dtos.stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }
}
