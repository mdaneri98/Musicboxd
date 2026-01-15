package ar.edu.itba.paw.webapp.mapper.dto;

import ar.edu.itba.paw.webapp.dto.AlbumDTO;
import ar.edu.itba.paw.models.Album;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.UriInfo;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper to convert between Album model and AlbumDTO
 */
@Component
public class AlbumDtoMapper {

    public AlbumDTO toDTO(Album album, UriInfo uriInfo) {
        if (album == null) {
            return null;
        }

        AlbumDTO dto = new AlbumDTO();
        dto.setId(album.getId());
        dto.setTitle(album.getTitle());
        dto.setGenre(album.getGenre());
        dto.setReleaseDate(album.getReleaseDate());
        dto.setImageId(album.getImage() != null ? album.getImage().getId() : null);
        dto.setArtistId(album.getArtist() != null ? album.getArtist().getId() : null);
        dto.setArtistName(album.getArtist() != null ? album.getArtist().getName() : null);
        dto.setRatingCount(album.getRatingCount());
        dto.setAvgRating(album.getAvgRating());
        dto.setCreatedAt(album.getCreatedAt());
        dto.setUpdatedAt(album.getUpdatedAt());

        // Build HATEOAS links
        if (uriInfo != null) {
            dto.setSelf(uriInfo.getBaseUriBuilder()
                    .path("albums").path(String.valueOf(album.getId())).build());

            if (album.getImage() != null) {
                dto.setImage(uriInfo.getBaseUriBuilder()
                        .path("images").path(String.valueOf(album.getImage().getId())).build());
            }

            if (album.getArtist() != null) {
                dto.setArtistLink(uriInfo.getBaseUriBuilder()
                        .path("artists").path(String.valueOf(album.getArtist().getId())).build());
            }

            dto.setSongsLink(uriInfo.getBaseUriBuilder()
                    .path("albums").path(String.valueOf(album.getId())).path("songs").build());

            dto.setReviewsLink(uriInfo.getBaseUriBuilder()
                    .path("albums").path(String.valueOf(album.getId())).path("reviews").build());
        }

        return dto;
    }

    public List<AlbumDTO> toDTOList(List<Album> albums, UriInfo uriInfo) {
        if (albums == null) {
            return null;
        }

        return albums.stream()
                .map(a -> toDTO(a, uriInfo))
                .collect(Collectors.toList());
    }

    public Album toModel(AlbumDTO dto) {
        if (dto == null) {
            return null;
        }

        Album album = new Album();
        album.setId(dto.getId());
        album.setTitle(dto.getTitle());
        album.setGenre(dto.getGenre());
        album.setReleaseDate(dto.getReleaseDate());
        album.setRatingCount(dto.getRatingCount());
        album.setAvgRating(dto.getAvgRating());
        album.setCreatedAt(dto.getCreatedAt());
        album.setUpdatedAt(dto.getUpdatedAt());

        return album;
    }

    public List<Album> toModelList(List<AlbumDTO> dtos) {
        if (dtos == null) {
            return null;
        }

        return dtos.stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }
}
