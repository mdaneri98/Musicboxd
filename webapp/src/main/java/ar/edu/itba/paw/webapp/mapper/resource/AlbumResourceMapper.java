package ar.edu.itba.paw.webapp.mapper.resource;

import ar.edu.itba.paw.webapp.dto.AlbumDTO;
import ar.edu.itba.paw.webapp.models.resources.AlbumResource;
import ar.edu.itba.paw.webapp.models.links.managers.AlbumLinkManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AlbumResourceMapper implements ResourceMapper<AlbumDTO, AlbumResource> {

    @Autowired
    private AlbumLinkManager albumLinkManager;

    @Override
    public AlbumResource toResource(AlbumDTO albumDTO, String baseUrl) {
        AlbumResource resource = new AlbumResource(albumDTO);
        albumLinkManager.addAlbumLinks(resource, baseUrl, albumDTO.getId());
        return resource;
    }

    @Override
    public List<AlbumResource> toResourceList(List<AlbumDTO> albumDTOs, String baseUrl) {
        return albumDTOs.stream()
                .map(dto -> toResource(dto, baseUrl))
                .collect(Collectors.toList());
    }
}
