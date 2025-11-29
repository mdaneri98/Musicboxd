package ar.edu.itba.paw.api.mapper.dto;

import ar.edu.itba.paw.api.form.ModSongForm;
import ar.edu.itba.paw.models.Album;
import ar.edu.itba.paw.models.Song;
import ar.edu.itba.paw.models.dtos.SongDTO;
import org.springframework.stereotype.Component;

/**
 * Mapper to convert ModSongForm to Song model
 */
@Component
public class ModSongFormMapper {

    public Song toModel(ModSongForm form) {
        if (form == null) {
            return null;
        }
        
        Song song = new Song();
        song.setId(form.getId());
        song.setTitle(form.getTitle());
        song.setDuration(form.getDuration());
        song.setTrackNumber(form.getTrackNumber());
        if (form.getAlbumId() != null) {
            song.setAlbum(new Album(form.getAlbumId()));
        }
        
        return song;
    }

    /**
     * @deprecated Use toModel() instead. This method will be removed after SongService refactoring.
     */
    @Deprecated
    public SongDTO toDTO(ModSongForm form) {
        if (form == null) {
            return null;
        }
        
        SongDTO dto = new SongDTO();
        dto.setId(form.getId());
        dto.setTitle(form.getTitle());
        dto.setDuration(form.getDuration());
        dto.setTrackNumber(form.getTrackNumber());
        dto.setAlbumId(form.getAlbumId());
        dto.setIsDeleted(form.isDeleted());
        
        return dto;
    }
}
