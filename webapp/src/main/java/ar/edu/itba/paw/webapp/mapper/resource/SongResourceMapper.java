package ar.edu.itba.paw.webapp.mapper.resource;

import ar.edu.itba.paw.webapp.dto.SongDTO;
import ar.edu.itba.paw.webapp.models.resources.SongResource;
import ar.edu.itba.paw.webapp.models.links.managers.SongLinkManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class SongResourceMapper implements ResourceMapper<SongDTO, SongResource> {

    @Autowired
    private SongLinkManager songLinkManager;

    @Override
    public SongResource toResource(SongDTO songDTO, String baseUrl) {
        SongResource resource = new SongResource(songDTO);
        songLinkManager.addSongLinks(resource, baseUrl, songDTO.getId(), songDTO);
        return resource;
    }

    @Override
    public List<SongResource> toResourceList(List<SongDTO> songDTOs, String baseUrl) {
        return songDTOs.stream()
                .map(dto -> toResource(dto, baseUrl))
                .collect(Collectors.toList());
    }
}
