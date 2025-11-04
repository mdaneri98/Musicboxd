package ar.edu.itba.paw.api.mapper.dto;

import ar.edu.itba.paw.api.form.ModAlbumForm;
import ar.edu.itba.paw.models.dtos.AlbumDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

/**
 * Mapper to convert ModAlbumForm to AlbumDTO
 */
@Component
public class ModAlbumFormMapper {

    @Autowired
    private ModSongFormMapper songFormMapper;

    public AlbumDTO toDTO(ModAlbumForm form) {
        if (form == null) {
            return null;
        }
        
        AlbumDTO dto = new AlbumDTO();
        dto.setId(form.getId() > 0 ? form.getId() : null);
        dto.setTitle(form.getTitle());
        dto.setGenre(form.getGenre());
        dto.setReleaseDate(form.getReleaseDate());
        dto.setImageId(form.getAlbumImageId() > 0 ? form.getAlbumImageId() : null);
        dto.setArtistId(form.getArtistId() > 0 ? form.getArtistId() : null);
        dto.setIsDeleted(form.isDeleted());
        
        // Map nested songs if present
        if (form.getSongs() != null && !form.getSongs().isEmpty()) {
            dto.setSongs(form.getSongs().stream()
                    .map(songFormMapper::toDTO)
                    .collect(Collectors.toList()));
        }
        
        // Note: albumImage (MultipartFile) should be handled separately in the controller
        
        return dto;
    }
}

