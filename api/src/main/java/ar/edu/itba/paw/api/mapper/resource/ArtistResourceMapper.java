package ar.edu.itba.paw.api.mapper.resource;

import ar.edu.itba.paw.api.models.resources.ArtistResource;
import ar.edu.itba.paw.api.models.links.managers.ArtistLinkManager;
import ar.edu.itba.paw.models.dtos.ArtistDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ArtistResourceMapper implements ResourceMapper<ArtistDTO, ArtistResource> {

    @Autowired
    private ArtistLinkManager artistLinkManager;

    @Override
    public ArtistResource toResource(ArtistDTO artistDTO, String baseUrl) {
        ArtistResource resource = new ArtistResource(artistDTO);
        artistLinkManager.addArtistLinks(resource, baseUrl, artistDTO.getId());
        return resource;
    }

    @Override
    public List<ArtistResource> toResourceList(List<ArtistDTO> artistDTOs, String baseUrl) {
        return artistDTOs.stream()
                .map(dto -> toResource(dto, baseUrl))
                .collect(Collectors.toList());
    }
}

