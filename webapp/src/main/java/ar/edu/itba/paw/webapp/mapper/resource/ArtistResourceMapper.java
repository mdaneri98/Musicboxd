package ar.edu.itba.paw.webapp.mapper.resource;

import ar.edu.itba.paw.webapp.dto.ArtistDTO;
import ar.edu.itba.paw.webapp.models.resources.ArtistResource;
import ar.edu.itba.paw.webapp.models.links.managers.ArtistLinkManager;
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
        artistLinkManager.addArtistLinks(resource, baseUrl, artistDTO.getId(), artistDTO);
        return resource;
    }

    @Override
    public List<ArtistResource> toResourceList(List<ArtistDTO> artistDTOs, String baseUrl) {
        return artistDTOs.stream()
                .map(dto -> toResource(dto, baseUrl))
                .collect(Collectors.toList());
    }
}
