package ar.edu.itba.paw.services;

import ar.edu.itba.paw.Album;
import ar.edu.itba.paw.Artist;
import ar.edu.itba.paw.persistence.AlbumDao;
import ar.edu.itba.paw.persistence.ArtistDao;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

    public List<Artist> findBySongId(long id) {
        return artistDao.findBySongId(id);
    }

    @Override
    public int save(Artist artist) {
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

