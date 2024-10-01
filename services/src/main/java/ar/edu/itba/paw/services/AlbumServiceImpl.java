package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.Album;
import ar.edu.itba.paw.models.Artist;
import ar.edu.itba.paw.models.FilterType;
import ar.edu.itba.paw.persistence.AlbumDao;
import ar.edu.itba.paw.persistence.ArtistDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.logging.Filter;

@Service
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

    public List<Album> findPaginated(FilterType filterType, int page, int pageSize) {
        return albumDao.findPaginated(filterType, pageSize, (page - 1) * pageSize);
    }

    @Override
    public List<Album> findByArtistId(long id) {return albumDao.findByArtistId(id);}

    @Override
    public List<Album> findAll() {
        return albumDao.findAll();
    }

    @Override
    public List<Album> findByTitleContaining(String sub) {
        return albumDao.findByTitleContaining(sub);
    }

    @Override
    public long save(Album album) {
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
