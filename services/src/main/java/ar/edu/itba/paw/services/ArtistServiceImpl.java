package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.Artist;
import ar.edu.itba.paw.models.FilterType;
import ar.edu.itba.paw.persistence.ArtistDao;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.logging.Filter;

@Service
public class ArtistServiceImpl implements ArtistService {
    private final ArtistDao artistDao;

    public ArtistServiceImpl(ArtistDao artistDao) {
        this.artistDao = artistDao;
    }

    @Override
    public Optional<Artist> findById(long id) {
        return artistDao.findById(id);
    }

    @Override
    public List<Artist> findAll() {
        return artistDao.findAll();
    }

    @Override
    public List<Artist> findPaginated(FilterType filterType, int limit, int offset) {
        return artistDao.findPaginated(filterType, limit, offset);
    }

    @Override
    public List<Artist> findByNameContaining(String sub) {
        return artistDao.findByNameContaining(sub);
    }

    @Override
    public List<Artist> findBySongId(long id) {
        return artistDao.findBySongId(id);
    }

    @Override
    public long save(Artist artist) {
        return artistDao.save(artist);
    }

    @Override
    public int update(Artist artist) {
        return artistDao.update(artist);
    }

    @Override
    public int deleteById(long id) {
        return artistDao.deleteById(id);
    }
}

