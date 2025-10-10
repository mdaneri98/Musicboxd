package ar.edu.itba.paw.api.mapper;

import ar.edu.itba.paw.api.models.AlbumResource;
import ar.edu.itba.paw.api.utils.ApiUriConstants;
import ar.edu.itba.paw.api.utils.HATEOASUtils;
import ar.edu.itba.paw.models.dtos.AlbumDTO;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AlbumResourceMapper implements ResourceMapper<AlbumDTO, AlbumResource> {

    @Override
    public AlbumResource toResource(AlbumDTO albumDTO, String baseUrl) {
        AlbumResource resource = new AlbumResource(albumDTO);
        
        // Add CRUD links
        HATEOASUtils.addCrudLinks(resource, baseUrl, ApiUriConstants.ALBUMS_BASE, albumDTO.getId());
        
        // Add related resources links
        resource.addLink(baseUrl + ApiUriConstants.ALBUMS_BASE + "/" + albumDTO.getId() + "/reviews", 
                        "reviews", "Album reviews");
        resource.addLink(baseUrl + ApiUriConstants.ALBUMS_BASE + "/" + albumDTO.getId() + "/songs", 
                        "songs", "Album songs");
        
        if (albumDTO.getArtistId() != null) {
            resource.addLink(baseUrl + ApiUriConstants.ARTISTS_BASE + "/" + albumDTO.getArtistId(), 
                            "artist", "Album artist");
        }
        
        if (albumDTO.getImageId() != null) {
            resource.addLink(baseUrl + ApiUriConstants.IMAGES_BASE + "/" + albumDTO.getImageId(), 
                            "image", "Album image");
        }
        
        return resource;
    }

    @Override
    public List<AlbumResource> toResourceList(List<AlbumDTO> albumDTOs, String baseUrl) {
        return albumDTOs.stream()
                .map(dto -> toResource(dto, baseUrl))
                .collect(Collectors.toList());
    }
}

