package ar.edu.itba.paw.api.mapper.dto;

import ar.edu.itba.paw.api.form.ModSongForm;
import ar.edu.itba.paw.models.dtos.SongDTO;
import org.springframework.stereotype.Component;

/**
 * Mapper to convert ModSongForm to SongDTO
 */
@Component
public class ModSongFormMapper {

    public SongDTO toDTO(ModSongForm form) {
        if (form == null) {
            return null;
        }
        
        SongDTO dto = new SongDTO();
        dto.setId(form.getId() > 0 ? form.getId() : null);
        dto.setTitle(form.getTitle());
        dto.setDuration(form.getDuration());
        dto.setTrackNumber(form.getTrackNumber());
        dto.setAlbumId(form.getAlbumId() > 0 ? form.getAlbumId() : null);
        dto.setIsDeleted(form.isDeleted());
        
        return dto;
    }
}

