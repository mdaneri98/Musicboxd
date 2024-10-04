package ar.edu.itba.paw.services;


import ar.edu.itba.paw.models.FilterType;
import ar.edu.itba.paw.models.Song;
import ar.edu.itba.paw.persistence.SongDao;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SongServiceImpl implements SongService {
    /*
        FIXME: Add required `business logic`
     */
    private final SongDao songDao;

    public SongServiceImpl(SongDao songDao) {
        this.songDao = songDao;
    }

    @Override
    public Optional<Song> find(long id) {
        return songDao.find(id);
    }

    @Override
    public List<Song> findAll() {
        return songDao.findAll();
    }

    @Override
    public List<Song> findByTitleContaining(String sub) {
        return songDao.findByTitleContaining(sub);
    }

    @Override
    public List<Song> findPaginated(FilterType filterType, int page, int pageSize) {
        return songDao.findPaginated(filterType, pageSize, (page - 1) * pageSize);
    }

    public List<Song> findByArtistId(long id) {
        return songDao.findByArtistId(id);
    }

    public List<Song> findByAlbumId(long id) {
        return songDao.findByAlbumId(id);
    }

    @Override
    public Song create(Song song) {
        Song createdSong = songDao.create(song);
        int result = songDao.saveSongArtist(createdSong, song.getAlbum().getArtist());
        //FIXME: ¿Chequear que se haya agregado correctamente?
        return createdSong;
    }

    @Override
    public Song update(Song song) {
        return songDao.update(song);
    }

    @Override
    public boolean delete(long id) {
        return songDao.delete(id);
    }
}
