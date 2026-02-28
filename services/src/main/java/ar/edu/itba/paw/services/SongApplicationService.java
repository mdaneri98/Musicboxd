package ar.edu.itba.paw.services;

import ar.edu.itba.paw.domain.song.Song;
import ar.edu.itba.paw.models.FilterType;
import ar.edu.itba.paw.usecases.song.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SongApplicationService {

    private final GetSong getSong;
    private final GetAllSongs getAllSongs;
    private final CreateSong createSong;
    private final UpdateSong updateSong;
    private final DeleteSong deleteSong;
    private final GetSongsByArtistId getSongsByArtistId;
    private final GetSongsByAlbumId getSongsByAlbumId;
    private final SearchSongsByTitle searchSongsByTitle;
    private final UpdateSongRating updateSongRating;

    @Autowired
    public SongApplicationService(GetSong getSong,
                                   GetAllSongs getAllSongs,
                                   CreateSong createSong,
                                   UpdateSong updateSong,
                                   DeleteSong deleteSong,
                                   GetSongsByArtistId getSongsByArtistId,
                                   GetSongsByAlbumId getSongsByAlbumId,
                                   SearchSongsByTitle searchSongsByTitle,
                                   UpdateSongRating updateSongRating) {
        this.getSong = getSong;
        this.getAllSongs = getAllSongs;
        this.createSong = createSong;
        this.updateSong = updateSong;
        this.deleteSong = deleteSong;
        this.getSongsByArtistId = getSongsByArtistId;
        this.getSongsByAlbumId = getSongsByAlbumId;
        this.searchSongsByTitle = searchSongsByTitle;
        this.updateSongRating = updateSongRating;
    }

    public Song getById(Long songId) {
        return getSong.execute(songId);
    }

    public List<Song> getAll(int page, int size) {
        return getAllSongs.execute(page, size);
    }

    public Long count() {
        return getAllSongs.count();
    }

    public Song create(CreateSongCommand command) {
        return createSong.execute(command);
    }

    public Song update(UpdateSongCommand command) {
        return updateSong.execute(command);
    }

    public void delete(DeleteSongCommand command) {
        deleteSong.execute(command);
    }

    public List<Song> getByArtistId(Long artistId, FilterType filterType, int page, int size) {
        return getSongsByArtistId.execute(artistId, filterType, page, size);
    }

    public List<Song> getByAlbumId(Long albumId) {
        return getSongsByAlbumId.execute(albumId);
    }

    public List<Song> searchByTitle(String titleSubstring, int page, int size) {
        return searchSongsByTitle.execute(titleSubstring, page, size);
    }

    public void updateRating(Long songId) {
        updateSongRating.execute(songId);
    }
}
