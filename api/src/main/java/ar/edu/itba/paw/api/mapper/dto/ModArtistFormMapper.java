package ar.edu.itba.paw.api.mapper.dto;

import ar.edu.itba.paw.api.form.ModArtistForm;
import ar.edu.itba.paw.models.Artist;
import ar.edu.itba.paw.models.Image;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

/**
 * Mapper to convert ModArtistForm to Artist model
 */
@Component
public class ModArtistFormMapper {

    @Autowired
    private ModAlbumFormMapper albumFormMapper;

    public Artist toModel(ModArtistForm form) {
        if (form == null) {
            return null;
        }
        
        Artist artist = new Artist();
        artist.setId(form.getId());
        artist.setName(form.getName());
        artist.setBio(form.getBio());
        
        if (form.getArtistImgId() != null) {
            artist.setImage(new Image(form.getArtistImgId(), null));
        }

        if (form.getAlbums() != null && !form.getAlbums().isEmpty()) {
            artist.setAlbums(form.getAlbums().stream()
                    .filter(a -> !a.isDeleted())
                    .map(albumFormMapper::toModel)
                    .collect(Collectors.toList()));
        }
        
        return artist;
    }

    public Artist mergeModel(Artist artist, ModArtistForm form) {
        if (form == null) {
            return artist;
        }
        
        artist.setName(form.getName());
        artist.setBio(form.getBio());
        if (form.getArtistImgId() != null) {
            artist.setImage(new Image(form.getArtistImgId(), null));
        }
        if (form.getAlbums() != null && !form.getAlbums().isEmpty()) {
            artist.setAlbums(form.getAlbums().stream()
                    .filter(a -> !a.isDeleted())
                    .map(albumFormMapper::toModel)
                    .collect(Collectors.toList()));
        }
        return artist;
    }
}
