package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.Album;
import ar.edu.itba.paw.models.FilterType;
import ar.edu.itba.paw.models.Song;
import ar.edu.itba.paw.models.reviews.SongReview;
import ar.edu.itba.paw.models.dtos.SongDTO;
import ar.edu.itba.paw.persistence.SongDao;
import ar.edu.itba.paw.services.utils.TimeUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service 
public class SongServiceImpl implements SongService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SongServiceImpl.class);
    private final SongDao songDao;
    private final UserService userService;

    public SongServiceImpl(SongDao songDao, UserService userService) {
        this.songDao = songDao;
        this.userService = userService;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Song> find(long id) {
        Optional<Song> song = songDao.find(id);
        song.ifPresent(s -> s.getAlbum().setFormattedReleaseDate(TimeUtils.formatDate(s.getAlbum().getReleaseDate())));
        return song;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Song> findAll() {
        List<Song> songs = songDao.findAll();
        songs.forEach(s -> s.getAlbum().setFormattedReleaseDate(TimeUtils.formatDate(s.getAlbum().getReleaseDate())));
        return songs;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Song> findByTitleContaining(String sub) {
        List<Song> songs = songDao.findByTitleContaining(sub);
        return songs;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Song> findPaginated(FilterType filterType, int page, int pageSize) {
        List<Song> songs = songDao.findPaginated(filterType, pageSize, (page - 1) * pageSize);
        return songs;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Song> findByArtistId(long id, int pageNum, int pageSize) {
        List<Song> songs = songDao.findByArtistId(id, pageSize, (pageNum - 1) * pageSize);
        return songs;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Song> findByAlbumId(long id) {
        List<Song> songs = songDao.findByAlbumId(id);
        return songs;
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
        List<Long> userIds = new ArrayList<>();
        songDao.findReviewsBySongId(id).forEach(review -> userIds.add(review.getUser().getId()));
        songDao.deleteReviewsFromSong(id);
        userIds.forEach(userId -> userService.updateUserReviewAmount(userId));
        boolean result = songDao.delete(id);
        if (result) {
            LOGGER.info("Song deleted successfully");
        } else {
            LOGGER.warn("Failed to delete song with ID: {}", id);
        }
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<SongReview> findReviewsBySongId(long songId) {
        return songDao.findReviewsBySongId(songId);
    }

    @Override
    public boolean deleteReviewsFromSong(long id){
        return songDao.deleteReviewsFromSong(id);
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
        songDao.create(song);
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
        Song song = songDao.find(songDTO.getId()).get();
        song.setTitle(songDTO.getTitle());
        song.setDuration(songDTO.getDuration());
        song.setTrackNumber(songDTO.getTrackNumber());
        song.setAlbum(album);
        song.setUpdatedAt(LocalDateTime.now());
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
    public boolean updateRating(long songId, Double newRating, int newRatingAmount) {
        return songDao.updateRating(songId, newRating, newRatingAmount);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasUserReviewed(long userId, long songId) {
        return songDao.hasUserReviewed(userId, songId);
    }
}
