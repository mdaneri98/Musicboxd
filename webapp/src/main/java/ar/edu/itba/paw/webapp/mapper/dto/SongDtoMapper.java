package ar.edu.itba.paw.webapp.mapper.dto;

import ar.edu.itba.paw.views.SongView;
import ar.edu.itba.paw.webapp.dto.SongDTO;
import ar.edu.itba.paw.webapp.dto.links.SongLinksDTO;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.UriInfo;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class SongDtoMapper {

    public SongDTO toDTO(SongView songView, UriInfo uriInfo) {
        if (songView == null) {
            return null;
        }

        SongDTO dto = new SongDTO();
        dto.setId(songView.getId());
        dto.setTitle(songView.getTitle());
        dto.setDuration(songView.getDuration());
        dto.setTrackNumber(songView.getTrackNumber());
        dto.setAlbumId(songView.getAlbumId());
        dto.setAlbumTitle(songView.getAlbumTitle());
        dto.setAlbumImageId(songView.getAlbumImageId());

        if (songView.getArtistIds() != null && !songView.getArtistIds().isEmpty()) {
            dto.setArtistId(songView.getArtistIds().get(0));
        }

        dto.setRatingCount(songView.getRatingCount());
        dto.setAvgRating(songView.getAvgRating());
        dto.setCreatedAt(songView.getCreatedAt());
        dto.setUpdatedAt(songView.getUpdatedAt());

        if (uriInfo != null) {
            SongLinksDTO links = new SongLinksDTO();

            links.setSelf(uriInfo.getBaseUriBuilder()
                    .path("songs").path(String.valueOf(songView.getId())).build());

            if (songView.getAlbumId() != null) {
                links.setAlbum(uriInfo.getBaseUriBuilder()
                        .path("albums").path(String.valueOf(songView.getAlbumId())).build());
            }

            if (songView.getArtistIds() != null && !songView.getArtistIds().isEmpty()) {
                links.setArtist(uriInfo.getBaseUriBuilder()
                        .path("artists").path(String.valueOf(songView.getArtistIds().get(0))).build());
            }

            links.setReviews(uriInfo.getBaseUriBuilder()
                    .path("songs").path(String.valueOf(songView.getId())).path("reviews").build());

            dto.setLinks(links);
        }

        return dto;
    }

    public List<SongDTO> toDTOList(List<SongView> songViews, UriInfo uriInfo) {
        if (songViews == null) {
            return null;
        }

        return songViews.stream()
                .map(s -> toDTO(s, uriInfo))
                .collect(Collectors.toList());
    }
}
