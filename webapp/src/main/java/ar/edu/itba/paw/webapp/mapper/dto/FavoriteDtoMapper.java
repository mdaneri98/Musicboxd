package ar.edu.itba.paw.webapp.mapper.dto;

import ar.edu.itba.paw.webapp.dto.FavoriteDTO;
import ar.edu.itba.paw.webapp.dto.links.FavoriteLinksDTO;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.UriInfo;
import java.time.LocalDateTime;

/**
 * Mapper to convert FavoriteDTO with HATEOAS links
 */
@Component
public class FavoriteDtoMapper {

    public FavoriteDTO toDTO(Long userId, Long itemId, String itemType, LocalDateTime createdAt, UriInfo uriInfo) {
        FavoriteDTO dto = new FavoriteDTO(userId, itemId, itemType, createdAt);

        if (uriInfo != null) {
            FavoriteLinksDTO links = new FavoriteLinksDTO();

            String itemPath = getItemPath(itemType);
            links.setSelf(uriInfo.getBaseUriBuilder()
                    .path("users").path(String.valueOf(userId))
                    .path("favorites").path(itemPath).path(String.valueOf(itemId)).build());

            links.setUser(uriInfo.getBaseUriBuilder()
                    .path("users").path(String.valueOf(userId)).build());

            links.setItem(uriInfo.getBaseUriBuilder()
                    .path(itemPath).path(String.valueOf(itemId)).build());

            dto.setLinks(links);
        }

        return dto;
    }

    private String getItemPath(String itemType) {
        if (itemType == null)
            return "items";
        switch (itemType.toLowerCase()) {
            case "artist":
                return "artists";
            case "album":
                return "albums";
            case "song":
                return "songs";
            default:
                return "items";
        }
    }
}
