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
import ar.edu.itba.paw.services.mappers.SongMapper;
import ar.edu.itba.paw.persistence.AlbumDao;
import ar.edu.itba.paw.services.exception.AlbumNotFoundException;
import ar.edu.itba.paw.services.exception.SongNotFoundException;
import ar.edu.itba.paw.models.dtos.ReviewDTO;
import ar.edu.itba.paw.services.mappers.ReviewMapper;
import java.util.stream.Collectors;

@Service 
public class SongServiceImpl implements SongService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SongServiceImpl.class);
    private final SongDao songDao;
    private final AlbumDao albumDao;
    private final UserService userService;
    private final SongMapper songMapper;

    public SongServiceImpl(SongDao songDao, AlbumDao albumDao, UserService userService, SongMapper songMapper) {
        this.songDao = songDao;
        this.albumDao = albumDao;
        this.userService = userService;
        this.songMapper = songMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public SongDTO findById(Long id) {
        Song song = songDao.findById(id).orElseThrow(() -> new SongNotFoundException("Song with id " + id + " not found"));
        song.getAlbum().setFormattedReleaseDate(TimeUtils.formatDate(song.getAlbum().getReleaseDate()));
        SongDTO songDTO = songMapper.toDTO(song);
        return songDTO;
    }

    @Override
    @Transactional(readOnly = true)
    public List<SongDTO> findAll() {
        List<Song> songs = songDao.findAll();
        songs.forEach(s -> s.getAlbum().setFormattedReleaseDate(TimeUtils.formatDate(s.getAlbum().getReleaseDate())));
        return songMapper.toDTOList(songs);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SongDTO> findByTitleContaining(String sub) {
        List<Song> songs = songDao.findByTitleContaining(sub);
        return songMapper.toDTOList(songs);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SongDTO> findPaginated(FilterType filterType, Integer page, Integer pageSize) {
        List<Song> songs = songDao.findPaginated(filterType, pageSize, (page - 1) * pageSize);
        return songMapper.toDTOList(songs);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SongDTO> findByArtistId(Long id, Integer pageNum, Integer pageSize) {
        List<Song> songs = songDao.findByArtistId(id, pageSize, (pageNum - 1) * pageSize);
        return songMapper.toDTOList(songs);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SongDTO> findByAlbumId(Long id) {
        List<Song> songs = songDao.findByAlbumId(id);
        return songMapper.toDTOList(songs);
    }

    @Override
    @Transactional
    public Boolean delete(Long id) {
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
    public List<ReviewDTO> findReviewsBySongId(Long songId) {
        return songDao.findReviewsBySongId(songId).stream().map(SongReview::toDTO).collect(Collectors.toList());
    }

    @Override
    public Boolean deleteReviewsFromSong(Long id){
        return songDao.deleteReviewsFromSong(id);
    }

    @Override
    @Transactional
    public SongDTO create(SongDTO songDTO) {
        LOGGER.info("Creating new song from DTO: {}", songDTO.getTitle());
        Album album = albumDao.findById(songDTO.getAlbumId()).orElseThrow(() -> new AlbumNotFoundException("Album with id " + songDTO.getAlbumId() + " not found"));
        Song song = new Song(songDTO.getTitle(), songDTO.getDuration(), songDTO.getTrackNumber(), album);
        song.setCreatedAt(LocalDateTime.now());
        song.setUpdatedAt(LocalDateTime.now());
        song.setRatingCount(0);
        song.setAvgRating(0d);
        songDao.create(song);
        songDao.saveSongArtist(song, song.getAlbum().getArtist());
        LOGGER.info("Song created successfully with ID: {}", song.getId());
        return songMapper.toDTO(song);
    }

    @Override
    @Transactional
    public Boolean createAll(List<SongDTO> songsDTO, Album album) {
        LOGGER.info("Creating multiple songs for album: {}", album.getTitle());
        for (SongDTO songDTO : songsDTO) {
            if (!songDTO.isDeleted()) {
                create(songDTO);
            }
        }
        LOGGER.info("All songs created successfully for album: {}", album.getTitle());
        return true;
    }

    @Override
    @Transactional
    public SongDTO update(SongDTO songDTO) {
        LOGGER.info("Updating song with ID: {}", songDTO.getId());
        Song song = songDao.findById(songDTO.getId()).orElseThrow(() -> new SongNotFoundException("Song with id " + songDTO.getId() + " not found"));
        Album album = albumDao.findById(songDTO.getAlbumId()).orElseThrow(() -> new AlbumNotFoundException("Album with id " + songDTO.getAlbumId() + " not found"));
        song.setTitle(songDTO.getTitle());
        song.setDuration(songDTO.getDuration());
        song.setTrackNumber(songDTO.getTrackNumber());
        song.setAlbum(album);
        song.setUpdatedAt(LocalDateTime.now());
        song = songDao.update(song);
        LOGGER.info("Song updated successfully");
        return songMapper.toDTO(song);
    }

    @Override
    @Transactional
    public Boolean updateAll(List<SongDTO> songsDTO, Album album) {
        LOGGER.info("Updating multiple songs for album: {}", album.getTitle());
        for (SongDTO songDTO : songsDTO) {
            if (songDTO.getId() != 0) {
                if (songDTO.isDeleted()) {
                    delete(songDTO.getId());
                } else {
                    update(songDTO);
                }
            } else {
                if (!songDTO.isDeleted()) {
                    create(songDTO);
                }
            }
        }
        LOGGER.info("All songs updated successfully for album: {}", album.getTitle());
        return true;
    }

    @Override
    @Transactional
    public Boolean updateRating(Long songId, Double newRating, Integer newRatingAmount) {
        return songDao.updateRating(songId, newRating, newRatingAmount);
    }

    @Override
    @Transactional(readOnly = true)
    public Boolean hasUserReviewed(Long userId, Long songId) {
        return songDao.hasUserReviewed(userId, songId);
    }
}
