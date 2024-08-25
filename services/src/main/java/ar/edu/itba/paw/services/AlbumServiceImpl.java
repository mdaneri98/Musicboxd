package ar.edu.itba.paw.services;

import ar.edu.itba.paw.Album;
import ar.edu.itba.paw.persistence.AlbumDao;

import java.util.List;
import java.util.Optional;

public class AlbumServiceImpl implements AlbumService {
    /*
        FIXME: Add required `business logic`
     */
    private final AlbumDao albumDao;

    public AlbumServiceImpl(AlbumDao albumDao) {
        this.albumDao = albumDao;
    }

    @Override
    public Optional<Album> findById(long id) {
        return albumDao.findById(id);
    }

    @Override
    public List<Album> findAll() {
        return albumDao.findAll();
    }

    @Override
    public int save(Album album) {
        return albumDao.save(album);
    }

    @Override
    public int update(Album album) {
        return albumDao.update(album);
    }

    @Override
    public int deleteById(long id) {
        return albumDao.deleteById(id);
    }
}
