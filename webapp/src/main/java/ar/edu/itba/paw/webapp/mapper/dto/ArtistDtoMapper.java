package ar.edu.itba.paw.webapp.mapper.dto;

import ar.edu.itba.paw.webapp.dto.ArtistDTO;
import ar.edu.itba.paw.models.Artist;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.UriInfo;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper to convert between Artist model and ArtistDTO
 */
@Component
public class ArtistDtoMapper {

    public ArtistDTO toDTO(Artist artist, UriInfo uriInfo) {
        if (artist == null) {
            return null;
        }

        ArtistDTO dto = new ArtistDTO();
        dto.setId(artist.getId());
        dto.setName(artist.getName());
        dto.setBio(artist.getBio());
        dto.setImageId(artist.getImage() != null ? artist.getImage().getId() : null);
        dto.setRatingCount(artist.getRatingCount());
        dto.setAvgRating(artist.getAvgRating());
        dto.setCreatedAt(artist.getCreatedAt());
        dto.setUpdatedAt(artist.getUpdatedAt());

        // Build HATEOAS links
        if (uriInfo != null) {
            dto.setSelf(uriInfo.getBaseUriBuilder()
                    .path("artists").path(String.valueOf(artist.getId())).build());

            if (artist.getImage() != null) {
                dto.setImage(uriInfo.getBaseUriBuilder()
                        .path("images").path(String.valueOf(artist.getImage().getId())).build());
            }

            dto.setAlbumsLink(uriInfo.getBaseUriBuilder()
                    .path("artists").path(String.valueOf(artist.getId())).path("albums").build());

            dto.setSongsLink(uriInfo.getBaseUriBuilder()
                    .path("artists").path(String.valueOf(artist.getId())).path("songs").build());

            dto.setReviewsLink(uriInfo.getBaseUriBuilder()
                    .path("artists").path(String.valueOf(artist.getId())).path("reviews").build());
        }

        return dto;
    }

    public List<ArtistDTO> toDTOList(List<Artist> artists, UriInfo uriInfo) {
        if (artists == null) {
            return null;
        }

        return artists.stream()
                .map(a -> toDTO(a, uriInfo))
                .collect(Collectors.toList());
    }

    public Artist toModel(ArtistDTO dto) {
        if (dto == null) {
            return null;
        }

        Artist artist = new Artist();
        artist.setId(dto.getId());
        artist.setName(dto.getName());
        artist.setBio(dto.getBio());
        artist.setRatingCount(dto.getRatingCount());
        artist.setAvgRating(dto.getAvgRating());
        artist.setCreatedAt(dto.getCreatedAt());
        artist.setUpdatedAt(dto.getUpdatedAt());

        return artist;
    }

    public List<Artist> toModelList(List<ArtistDTO> dtos) {
        if (dtos == null) {
            return null;
        }

        return dtos.stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }
}
