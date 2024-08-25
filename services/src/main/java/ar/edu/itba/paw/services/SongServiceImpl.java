package ar.edu.itba.paw.services;


import ar.edu.itba.paw.Song;
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
    public Optional<Song> findById(long id) {
        return songDao.findById(id);
    }

    @Override
    public List<Song> findAll() {
        return songDao.findAll();
    }

    @Override
    public int save(Song song) {
        return songDao.save(song);
    }

    @Override
    public int update(Song song) {
        return songDao.update(song);
    }

    @Override
    public int deleteById(long id) {
        return songDao.deleteById(id);
    }
}
