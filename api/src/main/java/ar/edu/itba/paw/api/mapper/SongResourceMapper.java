package ar.edu.itba.paw.api.mapper;

import ar.edu.itba.paw.api.models.SongResource;
import ar.edu.itba.paw.api.utils.ApiUriConstants;
import ar.edu.itba.paw.api.utils.HATEOASUtils;
import ar.edu.itba.paw.models.dtos.SongDTO;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class SongResourceMapper implements ResourceMapper<SongDTO, SongResource> {

    @Override
    public SongResource toResource(SongDTO songDTO, String baseUrl) {
        SongResource resource = new SongResource(songDTO);
        
        // Add CRUD links
        HATEOASUtils.addCrudLinks(resource, baseUrl, ApiUriConstants.SONGS_BASE, songDTO.getId());
        
        // Add related resources links
        resource.addLink(baseUrl + ApiUriConstants.SONGS_BASE + "/" + songDTO.getId() + "/reviews", 
                        "reviews", "Song reviews");
        
        if (songDTO.getAlbumId() != null) {
            resource.addLink(baseUrl + ApiUriConstants.ALBUMS_BASE + "/" + songDTO.getAlbumId(), 
                            "album", "Song album");
        }
        
        if (songDTO.getAlbumImageId() != null) {
            resource.addLink(baseUrl + ApiUriConstants.IMAGES_BASE + "/" + songDTO.getAlbumImageId(), 
                            "image", "Song/Album image");
        }
        
        return resource;
    }

    @Override
    public List<SongResource> toResourceList(List<SongDTO> songDTOs, String baseUrl) {
        return songDTOs.stream()
                .map(dto -> toResource(dto, baseUrl))
                .collect(Collectors.toList());
    }
}

