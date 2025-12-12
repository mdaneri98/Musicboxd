package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.Album;
import ar.edu.itba.paw.models.Artist;
import ar.edu.itba.paw.models.FilterType;
import ar.edu.itba.paw.models.Song;
import ar.edu.itba.paw.models.reviews.Review;
import ar.edu.itba.paw.persistence.SongDao;
import ar.edu.itba.paw.persistence.AlbumDao;
import ar.edu.itba.paw.exception.not_found.AlbumNotFoundException;
import ar.edu.itba.paw.exception.not_found.SongNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

@Service 
public class SongServiceImpl implements SongService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SongServiceImpl.class);
    private final SongDao songDao;
    private final AlbumDao albumDao;
    private final UserService userService;

    public SongServiceImpl(SongDao songDao, AlbumDao albumDao, UserService userService) {
        this.songDao = songDao;
        this.albumDao = albumDao;
        this.userService = userService;
    }

    @Override
    @Transactional(readOnly = true)
    public Song findById(Long id) {
        return songDao.findById(id).orElseThrow(() -> new SongNotFoundException(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Song> findAll() {
        return songDao.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Song> findByTitleContaining(String sub, Integer page, Integer size) {
        return songDao.findByTitleContaining(sub, page, size);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Song> findPaginated(FilterType filterType, Integer page, Integer pageSize) {
        return songDao.findPaginated(filterType, pageSize, (page - 1) * pageSize);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Song> findByArtistId(Long id, FilterType filterType, Integer pageNum, Integer pageSize) {
        return songDao.findByArtistId(id, filterType, pageSize, (pageNum - 1) * pageSize);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Song> findByAlbumId(Long id) {
        return songDao.findByAlbumId(id);
    }

    @Override
    @Transactional
    public Boolean delete(Long id) {
        LOGGER.info("Deleting song with ID: {}", id);
        List<Long> userIds = new ArrayList<>();
        songDao.findReviewsBySongId(id).forEach(review -> userIds.add(review.getUser().getId()));
        songDao.deleteReviewsFromSong(id);
        userIds.forEach(userService::updateUserReviewAmount);
        boolean result = songDao.delete(id);
        if (result) LOGGER.info("Song deleted successfully");
        else LOGGER.warn("Failed to delete song with ID: {}", id);
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Review> findReviewsBySongId(Long songId) {
        return new ArrayList<>(songDao.findReviewsBySongId(songId));
    }

    @Override
    @Transactional
    public Song create(Song songInput) {
        LOGGER.info("Creating new song: {}", songInput.getTitle());
        
        Album inputAlbum = songInput.getAlbum();
        Album album = null;
        if (inputAlbum != null && inputAlbum.getId() != null) {
            album = albumDao.findById(inputAlbum.getId()).orElseThrow(() -> new AlbumNotFoundException(inputAlbum.getId()));
        }
        
        Song song = new Song(songInput.getTitle(), songInput.getDuration(), songInput.getTrackNumber(), album);
        songDao.create(song);
        
        if (songInput.getArtists() != null && !songInput.getArtists().isEmpty()) {
            songDao.saveSongArtist(song, songInput.getArtists().get(0));
        } else if (album != null && album.getArtist() != null) {
            songDao.saveSongArtist(song, album.getArtist());
        }
        
        LOGGER.info("Song created successfully with ID: {}", song.getId());
        return song;
    }

    @Override
    @Transactional
    public Boolean createAll(List<Song> songs, Album album) {
        LOGGER.info("Creating multiple songs for album: {}", album.getId());
        for (Song song : songs) {
            song.setAlbum(album);
            if (song.getArtists() == null || song.getArtists().isEmpty()) {
                List<Artist> artists = new ArrayList<>();
                artists.add(album.getArtist());
                song.setArtists(artists);
            }
            create(song);
        }
        LOGGER.info("All songs created successfully for album: {}", album.getId());
        return true;
    }

    @Override
    @Transactional
    public Song update(Song songInput) {
        LOGGER.info("Updating song with ID: {}", songInput.getId());
        Song song = songDao.findById(songInput.getId()).orElseThrow(() -> new SongNotFoundException(songInput.getId()));
        
        if (songInput.getAlbum() != null && songInput.getAlbum().getId() != null) {
            song.setAlbum(albumDao.findById(songInput.getAlbum().getId())
                    .orElseThrow(() -> new AlbumNotFoundException(songInput.getAlbum().getId())));
        }
        
        if (songInput.getTitle() != null) song.setTitle(songInput.getTitle());
        if (songInput.getDuration() != null) song.setDuration(songInput.getDuration());
        if (songInput.getTrackNumber() != null) song.setTrackNumber(songInput.getTrackNumber());
        
        song = songDao.update(song);
        LOGGER.info("Song updated successfully");
        return song;
    }

    @Override
    @Transactional
    public Boolean updateAll(List<Song> songs, Album album) {
        LOGGER.info("Updating multiple songs for album: {}", album.getTitle());
        for (Song songInput : songs) {
            if (songInput.getAlbum() == null) {
                songInput.setAlbum(new Album(album.getId()));
            }
            if (songInput.getArtists() == null || songInput.getArtists().isEmpty()) {
                List<Artist> artists = new ArrayList<>();
                artists.add(album.getArtist());
                songInput.setArtists(artists);
            }
            if (songInput.getId() != null && songInput.getId() != 0) {
                update(songInput);
            } else {
                create(songInput);
            }
        }
        LOGGER.info("All songs updated successfully for album: {}", album.getTitle());
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public Boolean hasUserReviewed(Long userId, Long songId) {
        return songDao.hasUserReviewed(userId, songId);
    }

    @Override
    @Transactional(readOnly = true)
    public Long countAll() {
        return songDao.countAll();
    }

    @Override
    @Transactional
    public Boolean updateRating(Long songId) {
        LOGGER.info("Updating rating for song ID: {}", songId);
        
        List<Review> reviews = findReviewsBySongId(songId);
        Double avgRating = reviews.stream().mapToInt(Review::getRating).average().orElse(0.0);
        Double roundedAvgRating = Math.round(avgRating * 100.0) / 100.0;
        int ratingAmount = reviews.size();
        Boolean updated = songDao.updateRating(songId, roundedAvgRating, ratingAmount);

        if (updated) LOGGER.info("Song rating updated. New average rating: {}, Total reviews: {}", roundedAvgRating, ratingAmount);
        else LOGGER.error("Song rating not updated. average rating: {}, Total reviews: {}", roundedAvgRating, ratingAmount);
        return updated;
    }
}
