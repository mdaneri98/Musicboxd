package ar.edu.itba.paw.services;

import ar.edu.itba.paw.domain.album.Album;
import ar.edu.itba.paw.usecases.album.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AlbumApplicationService {

    private final GetAlbum getAlbum;
    private final GetAllAlbums getAllAlbums;
    private final CreateAlbum createAlbum;
    private final UpdateAlbum updateAlbum;
    private final DeleteAlbum deleteAlbum;
    private final GetAlbumsByArtistId getAlbumsByArtistId;
    private final SearchAlbumsByTitle searchAlbumsByTitle;
    private final UpdateAlbumRating updateAlbumRating;

    @Autowired
    public AlbumApplicationService(GetAlbum getAlbum,
                                    GetAllAlbums getAllAlbums,
                                    CreateAlbum createAlbum,
                                    UpdateAlbum updateAlbum,
                                    DeleteAlbum deleteAlbum,
                                    GetAlbumsByArtistId getAlbumsByArtistId,
                                    SearchAlbumsByTitle searchAlbumsByTitle,
                                    UpdateAlbumRating updateAlbumRating) {
        this.getAlbum = getAlbum;
        this.getAllAlbums = getAllAlbums;
        this.createAlbum = createAlbum;
        this.updateAlbum = updateAlbum;
        this.deleteAlbum = deleteAlbum;
        this.getAlbumsByArtistId = getAlbumsByArtistId;
        this.searchAlbumsByTitle = searchAlbumsByTitle;
        this.updateAlbumRating = updateAlbumRating;
    }

    public Album getById(Long albumId) {
        return getAlbum.execute(albumId);
    }

    public List<Album> getAll(Integer page, Integer size) {
        return getAllAlbums.execute(page, size);
    }

    public Long count() {
        return getAllAlbums.count();
    }

    public Album create(CreateAlbumCommand command) {
        return createAlbum.execute(command);
    }

    public Album update(UpdateAlbumCommand command) {
        return updateAlbum.execute(command);
    }

    public void delete(DeleteAlbumCommand command) {
        deleteAlbum.execute(command);
    }

    public List<Album> getByArtistId(Long artistId) {
        return getAlbumsByArtistId.execute(artistId);
    }

    public List<Album> searchByTitle(String titleSubstring, Integer page, Integer size) {
        return searchAlbumsByTitle.execute(titleSubstring, page, size);
    }

    public void updateRating(Long albumId) {
        updateAlbumRating.execute(albumId);
    }
}
