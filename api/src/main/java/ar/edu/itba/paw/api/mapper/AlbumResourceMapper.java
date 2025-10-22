package ar.edu.itba.paw.api.mapper;

import ar.edu.itba.paw.api.models.resources.AlbumResource;
import ar.edu.itba.paw.api.models.links.managers.AlbumLinkManager;
import ar.edu.itba.paw.models.dtos.AlbumDTO;
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

