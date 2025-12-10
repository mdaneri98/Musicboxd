package ar.edu.itba.paw.api.mapper.dto;

import ar.edu.itba.paw.api.form.ModAlbumForm;
import ar.edu.itba.paw.models.Album;
import ar.edu.itba.paw.models.Artist;
import ar.edu.itba.paw.models.Image;
import ar.edu.itba.paw.api.dto.AlbumDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

/**
 * Mapper to convert ModAlbumForm to Album model
 */
@Component
public class ModAlbumFormMapper {

    @Autowired
    private ModSongFormMapper songFormMapper;

    public Album toModel(ModAlbumForm form) {
        if (form == null) {
            return null;
        }
        
        Album album = new Album();
        album.setId(form.getId());
        album.setTitle(form.getTitle());
        album.setGenre(form.getGenre());
        album.setReleaseDate(form.getReleaseDate());
        
        if (form.getAlbumImageId() != null) {
            album.setImage(new Image(form.getAlbumImageId(), null));
        }
        
        if (form.getArtistId() != null) {
            album.setArtist(new Artist(form.getArtistId()));
        }
        
        if (form.getId() == null) {
            album.setRatingCount(0);
            album.setAvgRating(0.0);
            album.setCreatedAt(LocalDateTime.now());
            album.setUpdatedAt(LocalDateTime.now());
        }
        

        if (form.getSongs() != null && !form.getSongs().isEmpty()) {
            album.setSongs(form.getSongs().stream()
                    .filter(s -> !s.isDeleted())
                    .map(songFormMapper::toModel)
                    .collect(Collectors.toList()));
        }
        
        return album;
    }

    public AlbumDTO toDTO(ModAlbumForm form) {
        if (form == null) {
            return null;
        }
        
        AlbumDTO dto = new AlbumDTO();
        dto.setId(form.getId());
        dto.setTitle(form.getTitle());
        dto.setGenre(form.getGenre());
        dto.setReleaseDate(form.getReleaseDate());
        dto.setImageId(form.getAlbumImageId());
        dto.setArtistId(form.getArtistId());
        dto.setIsDeleted(form.isDeleted());
        
        if (form.getSongs() != null && !form.getSongs().isEmpty()) {
            dto.setSongs(form.getSongs().stream()
                    .map(songFormMapper::toDTO)
                    .collect(Collectors.toList()));
        }
        
        return dto;
    }

    public Album mergeModel(Album album, ModAlbumForm form) {
        if (form == null) {
            return album;
        }
        
        album.setTitle(form.getTitle());
        album.setGenre(form.getGenre());
        album.setReleaseDate(form.getReleaseDate());
        if (form.getAlbumImageId() != null) {
            album.setImage(new Image(form.getAlbumImageId(), null));
        }
        if (form.getArtistId() != null) {
            album.setArtist(new Artist(form.getArtistId()));
        }
        return album;
    }
}
