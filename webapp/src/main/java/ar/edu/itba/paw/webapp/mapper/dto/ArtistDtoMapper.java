package ar.edu.itba.paw.webapp.mapper.dto;

import ar.edu.itba.paw.domain.artist.Artist;
import ar.edu.itba.paw.webapp.dto.ArtistDTO;
import ar.edu.itba.paw.webapp.dto.links.ArtistLinksDTO;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.UriInfo;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ArtistDtoMapper {

    public ArtistDTO toDTO(Artist artist, UriInfo uriInfo) {
        if (artist == null) {
            return null;
        }

        ArtistDTO dto = new ArtistDTO();
        dto.setId(artist.getId().getValue());
        dto.setName(artist.getName());
        dto.setBio(artist.getBio());
        dto.setImageId(artist.getImageId());
        dto.setRatingCount(artist.getRatingCount());
        dto.setAvgRating(artist.getAvgRating());
        dto.setCreatedAt(artist.getCreatedAt());
        dto.setUpdatedAt(artist.getUpdatedAt());

        if (uriInfo != null) {
            ArtistLinksDTO links = new ArtistLinksDTO();

            links.setSelf(uriInfo.getBaseUriBuilder()
                    .path("artists").path(String.valueOf(artist.getId().getValue())).build());

            if (artist.getImageId() != null) {
                links.setImage(uriInfo.getBaseUriBuilder()
                        .path("images").path(String.valueOf(artist.getImageId())).build());
            }

            links.setAlbums(uriInfo.getBaseUriBuilder()
                    .path("artists").path(String.valueOf(artist.getId().getValue())).path("albums").build());

            links.setSongs(uriInfo.getBaseUriBuilder()
                    .path("artists").path(String.valueOf(artist.getId().getValue())).path("songs").build());

            links.setReviews(uriInfo.getBaseUriBuilder()
                    .path("artists").path(String.valueOf(artist.getId().getValue())).path("reviews").build());

            dto.setLinks(links);
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
}
