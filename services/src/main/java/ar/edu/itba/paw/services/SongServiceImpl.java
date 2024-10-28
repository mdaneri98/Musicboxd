package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.Album;
import ar.edu.itba.paw.models.FilterType;
import ar.edu.itba.paw.models.Song;
import ar.edu.itba.paw.models.dtos.SongDTO;
import ar.edu.itba.paw.persistence.SongDao;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class SongServiceImpl implements SongService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SongServiceImpl.class);
    private final SongDao songDao;

    public SongServiceImpl(SongDao songDao) {
        this.songDao = songDao;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Song> find(long id) {
        return songDao.find(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Song> findAll() {
        return songDao.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Song> findByTitleContaining(String sub) {
        return songDao.findByTitleContaining(sub);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Song> findPaginated(FilterType filterType, int page, int pageSize) {
        return songDao.findPaginated(filterType, pageSize, (page - 1) * pageSize);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Song> findByArtistId(long id) {
        return songDao.findByArtistId(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Song> findByAlbumId(long id) {
        return songDao.findByAlbumId(id);
    }

    @Override
    @Transactional
    public Song create(Song song) {
        LOGGER.info("Creating new song: {}", song.getTitle());
        Song createdSong = songDao.create(song);
        songDao.saveSongArtist(createdSong, song.getAlbum().getArtist());
        LOGGER.info("Song created successfully with ID: {}", createdSong.getId());
        return createdSong;
    }

    @Override
    @Transactional
    public Song update(Song song) {
        LOGGER.info("Updating song with ID: {}", song.getId());
        Song updatedSong = songDao.update(song);
        LOGGER.info("Song updated successfully");
        return updatedSong;
    }

    @Override
    @Transactional
    public boolean delete(long id) {
        LOGGER.info("Deleting song with ID: {}", id);
        songDao.deleteReviewsFromSong(id);
        boolean result = songDao.delete(id);
        if (result) {
            LOGGER.info("Song deleted successfully");
        } else {
            LOGGER.warn("Failed to delete song with ID: {}", id);
        }
        return result;
    }

    @Override
    @Transactional
    public Song create(SongDTO songDTO, Album album) {
        LOGGER.info("Creating new song from DTO: {}", songDTO.getTitle());
        Song song = new Song(songDTO.getTitle(), songDTO.getDuration(), songDTO.getTrackNumber(), album);
        song.setCreatedAt(LocalDateTime.now());
        song.setUpdatedAt(LocalDateTime.now());
        song.setRatingCount(0);
        song.setAvgRating(0d);
        songDao.saveSongArtist(song, song.getAlbum().getArtist());
        LOGGER.info("Song created successfully with ID: {}", song.getId());
        return song;
    }

    @Override
    @Transactional
    public boolean createAll(List<SongDTO> songsDTO, Album album) {
        LOGGER.info("Creating multiple songs for album: {}", album.getTitle());
        for (SongDTO songDTO : songsDTO) {
            if (!songDTO.isDeleted()) {
                create(songDTO, album);
            }
        }
        LOGGER.info("All songs created successfully for album: {}", album.getTitle());
        return true;
    }

    @Override
    @Transactional
    public Song update(SongDTO songDTO, Album album) {
        LOGGER.info("Updating song with ID: {}", songDTO.getId());
        Song song = new Song(songDTO.getId(), songDTO.getTitle(), songDTO.getDuration(), songDTO.getTrackNumber(), album);
        song = songDao.update(song);
        LOGGER.info("Song updated successfully");
        return song;
    }

    @Override
    @Transactional
    public boolean updateAll(List<SongDTO> songsDTO, Album album) {
        LOGGER.info("Updating multiple songs for album: {}", album.getTitle());
        for (SongDTO songDTO : songsDTO) {
            if (songDTO.getId() != 0) {
                if (songDTO.isDeleted()) {
                    delete(songDTO.getId());
                } else {
                    update(songDTO, album);
                }
            } else {
                if (!songDTO.isDeleted()) {
                    create(songDTO, album);
                }
            }
        }
        LOGGER.info("All songs updated successfully for album: {}", album.getTitle());
        return true;
    }

    @Override
    @Transactional
    public boolean updateRating(long songId, float newRating, int newRatingAmount) {
        return songDao.updateRating(songId, newRating, newRatingAmount);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasUserReviewed(long userId, long songId) {
        return songDao.hasUserReviewed(userId, songId);
    }
}
