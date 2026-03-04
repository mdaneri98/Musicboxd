package ar.edu.itba.paw.webapp.mapper.dto;

import ar.edu.itba.paw.views.AlbumView;
import ar.edu.itba.paw.webapp.dto.AlbumDTO;
import ar.edu.itba.paw.webapp.dto.links.AlbumLinksDTO;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.UriInfo;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class AlbumDtoMapper {

    public AlbumDTO toDTO(AlbumView albumView, UriInfo uriInfo) {
        if (albumView == null) {
            return null;
        }

        AlbumDTO dto = new AlbumDTO();
        dto.setId(albumView.getId());
        dto.setTitle(albumView.getTitle());
        dto.setGenre(albumView.getGenre());
        dto.setReleaseDate(albumView.getReleaseDate());
        dto.setImageId(albumView.getImageId());
        dto.setArtistId(albumView.getArtistId());
        dto.setArtistName(albumView.getArtistName());
        dto.setRatingCount(albumView.getRatingCount());
        dto.setAvgRating(albumView.getAvgRating());
        dto.setCreatedAt(albumView.getCreatedAt());
        dto.setUpdatedAt(albumView.getUpdatedAt());

        if (uriInfo != null) {
            AlbumLinksDTO links = new AlbumLinksDTO();

            links.setSelf(uriInfo.getBaseUriBuilder()
                    .path("albums").path(String.valueOf(albumView.getId())).build());

            if (albumView.getImageId() != null) {
                links.setImage(uriInfo.getBaseUriBuilder()
                        .path("images").path(String.valueOf(albumView.getImageId())).build());
            }

            if (albumView.getArtistId() != null) {
                links.setArtist(uriInfo.getBaseUriBuilder()
                        .path("artists").path(String.valueOf(albumView.getArtistId())).build());
            }

            links.setSongs(uriInfo.getBaseUriBuilder()
                    .path("albums").path(String.valueOf(albumView.getId())).path("songs").build());

            links.setReviews(uriInfo.getBaseUriBuilder()
                    .path("albums").path(String.valueOf(albumView.getId())).path("reviews").build());

            dto.setLinks(links);
        }

        return dto;
    }

    public List<AlbumDTO> toDTOList(List<AlbumView> albumViews, UriInfo uriInfo) {
        if (albumViews == null) {
            return null;
        }

        return albumViews.stream()
                .map(a -> toDTO(a, uriInfo))
                .collect(Collectors.toList());
    }
}
