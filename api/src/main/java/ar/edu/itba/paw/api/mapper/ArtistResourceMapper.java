package ar.edu.itba.paw.api.mapper;

import ar.edu.itba.paw.api.models.ArtistResource;
import ar.edu.itba.paw.api.utils.ApiUriConstants;
import ar.edu.itba.paw.api.utils.HATEOASUtils;
import ar.edu.itba.paw.models.dtos.ArtistDTO;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ArtistResourceMapper implements ResourceMapper<ArtistDTO, ArtistResource> {

    @Override
    public ArtistResource toResource(ArtistDTO artistDTO, String baseUrl) {
        ArtistResource resource = new ArtistResource(artistDTO);
        
        // Add CRUD links
        HATEOASUtils.addCrudLinks(resource, baseUrl, ApiUriConstants.ARTISTS_BASE, artistDTO.getId());
        
        // Add related resources links
        resource.addLink(baseUrl + ApiUriConstants.ARTISTS_BASE + "/" + artistDTO.getId() + "/reviews", 
                        "reviews", "Artist reviews");
        
        if (artistDTO.getImage().getId() != null) {
            resource.addLink(baseUrl + ApiUriConstants.IMAGES_BASE + "/" + artistDTO.getImage().getId(), 
                            "image", "Artist image");
        }
        
        return resource;
    }

    @Override
    public List<ArtistResource> toResourceList(List<ArtistDTO> artistDTOs, String baseUrl) {
        return artistDTOs.stream()
                .map(dto -> toResource(dto, baseUrl))
                .collect(Collectors.toList());
    }
}

