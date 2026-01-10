package ar.edu.itba.paw.webapp.mapper.resource;

import ar.edu.itba.paw.webapp.dto.FavoriteDTO;
import ar.edu.itba.paw.webapp.models.resources.FavoriteResource;
import ar.edu.itba.paw.webapp.utils.ApiUriConstants;
import ar.edu.itba.paw.webapp.utils.ControllerUtils;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class FavoriteResourceMapper {

    public FavoriteResource toResource(FavoriteDTO dto, String baseUri) {
        final FavoriteResource resource = new FavoriteResource(dto);
        
        String path = "";
        if (ControllerUtils.ITEM_TYPE_ARTIST.equalsIgnoreCase(dto.getType()) || "artist".equalsIgnoreCase(dto.getType())) {
            path = "/favorites/artists";
        } else if (ControllerUtils.ITEM_TYPE_ALBUM.equalsIgnoreCase(dto.getType()) || "album".equalsIgnoreCase(dto.getType())) {
            path = "/favorites/albums";
        } else if (ControllerUtils.ITEM_TYPE_SONG.equalsIgnoreCase(dto.getType()) || "song".equalsIgnoreCase(dto.getType())) {
            path = "/favorites/songs";
        }

        final URI selfUri = UriBuilder.fromUri(baseUri)
                .path(ApiUriConstants.USERS_BASE)
                .path(String.valueOf(dto.getUserId()))
                .path(path)
                .path(String.valueOf(dto.getItemId()))
                .build();

        resource.addSelfLink(selfUri.toString());
        resource.addDeleteLink(selfUri.toString());
        
        return resource;
    }

    public List<FavoriteResource> toResourceList(List<FavoriteDTO> dtos, String baseUri) {
        return dtos.stream().map(d -> toResource(d, baseUri)).collect(Collectors.toList());
    }
}
