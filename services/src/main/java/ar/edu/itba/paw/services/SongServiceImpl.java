package ar.edu.itba.paw.services;


import ar.edu.itba.paw.models.Album;
import ar.edu.itba.paw.models.FilterType;
import ar.edu.itba.paw.models.Song;
import ar.edu.itba.paw.models.dtos.AlbumDTO;
import ar.edu.itba.paw.models.dtos.SongDTO;
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
    public long save(Song song) {
        song.setId(songDao.save(song));
        return songDao.saveSongArtist(song, song.getAlbum().getArtist());
    }

    @Override
    public int update(Song song) {
        return songDao.update(song);
    }

    @Override
    public int deleteById(long id) {
        return songDao.deleteById(id);
    }


    //****************************************************************************************** Testing
    @Override
    public Song save(SongDTO songDTO, Album album) {
        Song song = new Song(songDTO.getId(), songDTO.getTitle(), songDTO.getDuration(), songDTO.getTrackNumber(), album);
        song = songDao.saveX(song);
        songDao.saveSongArtist(song, song.getAlbum().getArtist());
        return song;
    }

    @Override
    public boolean save(List<SongDTO> songsDTO, Album album) {
        for (SongDTO songDTO : songsDTO) {
            if (!songDTO.isDeleted()) {
                save(songDTO, album);
            }
        }
        return true;
    }

    @Override
    public Song update(SongDTO songDTO, Album album) {
        Song song = new Song(songDTO.getId(), songDTO.getTitle(), songDTO.getDuration(), songDTO.getTrackNumber(), album);
        song = songDao.updateX(song);
        return song;
    }

    @Override
    public boolean update(List<SongDTO> songsDTO, Album album) {
        for (SongDTO songDTO : songsDTO) {
            if (songDTO.getId() != 0) {
                if (songDTO.isDeleted()) {
                    deleteById(songDTO.getId());
                } else {
                    update(songDTO, album);
                }
            } else {
                if (!songDTO.isDeleted()) {
                    save(songDTO, album);
                }
            }
        }
        return true;
    }
}
