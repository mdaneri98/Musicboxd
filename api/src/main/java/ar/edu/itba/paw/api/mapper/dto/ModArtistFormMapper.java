package ar.edu.itba.paw.api.mapper.dto;

import ar.edu.itba.paw.api.form.ModArtistForm;
import ar.edu.itba.paw.models.dtos.ArtistDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

/**
 * Mapper to convert ModArtistForm to ArtistDTO
 */
@Component
public class ModArtistFormMapper {

    @Autowired
    private ModAlbumFormMapper albumFormMapper;

    public ArtistDTO toDTO(ModArtistForm form) {
        if (form == null) {
            return null;
        }
        
        ArtistDTO dto = new ArtistDTO();
        dto.setId(form.getId() > 0 ? form.getId() : null);
        dto.setName(form.getName());
        dto.setBio(form.getBio());
        dto.setImageId(form.getArtistImgId() > 0 ? form.getArtistImgId() : null);

        if (form.getAlbums() != null && !form.getAlbums().isEmpty()) {
            dto.setAlbums(form.getAlbums().stream()
                    .map(albumFormMapper::toDTO)
                    .collect(Collectors.toList()));
        }
        
        return dto;
    }
}

