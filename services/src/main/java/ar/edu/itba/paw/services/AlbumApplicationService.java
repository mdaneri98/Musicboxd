package ar.edu.itba.paw.services;

import ar.edu.itba.paw.domain.album.Album;
import ar.edu.itba.paw.usecases.album.*;
import ar.edu.itba.paw.views.AlbumView;
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
    private final GetAlbumView getAlbumView;
    private final GetAllAlbumsView getAllAlbumsView;
    private final SearchAlbumsByTitleView searchAlbumsByTitleView;
    private final GetAlbumsByArtistIdView getAlbumsByArtistIdView;

    @Autowired
    public AlbumApplicationService(GetAlbum getAlbum,
                                    GetAllAlbums getAllAlbums,
                                    CreateAlbum createAlbum,
                                    UpdateAlbum updateAlbum,
                                    DeleteAlbum deleteAlbum,
                                    GetAlbumsByArtistId getAlbumsByArtistId,
                                    SearchAlbumsByTitle searchAlbumsByTitle,
                                    UpdateAlbumRating updateAlbumRating,
                                    GetAlbumView getAlbumView,
                                    GetAllAlbumsView getAllAlbumsView,
                                    SearchAlbumsByTitleView searchAlbumsByTitleView,
                                    GetAlbumsByArtistIdView getAlbumsByArtistIdView) {
        this.getAlbum = getAlbum;
        this.getAllAlbums = getAllAlbums;
        this.createAlbum = createAlbum;
        this.updateAlbum = updateAlbum;
        this.deleteAlbum = deleteAlbum;
        this.getAlbumsByArtistId = getAlbumsByArtistId;
        this.searchAlbumsByTitle = searchAlbumsByTitle;
        this.updateAlbumRating = updateAlbumRating;
        this.getAlbumView = getAlbumView;
        this.getAllAlbumsView = getAllAlbumsView;
        this.searchAlbumsByTitleView = searchAlbumsByTitleView;
        this.getAlbumsByArtistIdView = getAlbumsByArtistIdView;
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

    public AlbumView getViewById(Long albumId) {
        return getAlbumView.execute(albumId);
    }

    public List<AlbumView> getAllViews(Integer page, Integer size) {
        return getAllAlbumsView.execute(page, size);
    }

    public List<AlbumView> searchViewByTitle(String titleSubstring, Integer page, Integer size) {
        return searchAlbumsByTitleView.execute(titleSubstring, page, size);
    }

    public List<AlbumView> getViewsByArtistId(Long artistId) {
        return getAlbumsByArtistIdView.execute(artistId);
    }
}
