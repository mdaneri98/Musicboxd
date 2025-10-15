package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.Artist;
import ar.edu.itba.paw.models.FilterType;
import ar.edu.itba.paw.models.Image;
import ar.edu.itba.paw.models.dtos.ArtistDTO;
import ar.edu.itba.paw.models.dtos.AlbumDTO;
import ar.edu.itba.paw.persistence.ArtistDao;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDateTime;
import java.util.ArrayList;
import ar.edu.itba.paw.services.exception.ArtistNotFoundException;
import java.util.List;
import ar.edu.itba.paw.services.mappers.ArtistMapper;
import ar.edu.itba.paw.services.mappers.ReviewMapper;
import java.util.stream.Collectors;
import ar.edu.itba.paw.models.dtos.ReviewDTO;
import ar.edu.itba.paw.services.utils.MergeUtils;

@Service
public class ArtistServiceImpl implements ArtistService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ArtistServiceImpl.class);
    private final ArtistDao artistDao;
    private final ImageService imageService;
    private final AlbumService albumService;
    private final UserService userService;
    private final ArtistMapper artistMapper;
    private final ReviewMapper reviewMapper;

    public ArtistServiceImpl(ArtistDao artistDao, ImageService imageService, AlbumService albumService, UserService userService, ArtistMapper artistMapper, ReviewMapper reviewMapper) {
        this.artistDao = artistDao;
        this.imageService = imageService;
        this.albumService = albumService;
        this.userService = userService;
        this.artistMapper = artistMapper;
        this.reviewMapper = reviewMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public ArtistDTO findById(Long id) {
        return artistDao.findById(id).map(artistMapper::toDTO).orElseThrow(() -> new ArtistNotFoundException("Artist with id " + id + " not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ArtistDTO> findAll() {
        return artistMapper.toDTOList(artistDao.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ArtistDTO> findPaginated(FilterType filterType, Integer page, Integer pageSize) {
        return artistMapper.toDTOList(artistDao.findPaginated(filterType, pageSize, (page - 1) * pageSize));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ArtistDTO> findByNameContaining(String sub, Integer page, Integer size) {
        return artistMapper.toDTOList(artistDao.findByNameContaining(sub, page, size));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ArtistDTO> findBySongId(Long id) {
        return artistMapper.toDTOList(artistDao.findBySongId(id));
    }

    @Override
    @Transactional
    public Boolean delete(Long id) {
        LOGGER.info("Attempting to delete artist with ID: {}", id);
        Artist artist = artistDao.findById(id).orElseThrow(() -> new ArtistNotFoundException("Artist with id " + id + " not found"));

        // Delete Images
        List<AlbumDTO> list = albumService.findByArtistId(id);
        list.forEach(album -> albumService.delete(album.getId()));

        List<Long> userIds = new ArrayList<>();
        artistDao.findReviewsByArtistId(id).forEach(review -> userIds.add(review.getUser().getId()));
        artistDao.deleteReviewsFromArtist(id);
        userIds.forEach(userId -> userService.updateUserReviewAmount(userId));
        boolean deleted = artistDao.delete(id);
        imageService.delete(artist.getImage().getId());
        if (deleted) {
            LOGGER.info("Artist with ID {} deleted successfully", id);
        } else {
            LOGGER.error("Failed to delete artist with ID {}", id);
        }
        return deleted;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewDTO> findReviewsByArtistId(Long artistId) {
        return artistDao.findReviewsByArtistId(artistId).stream().map(reviewMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Boolean delete(ArtistDTO artistDTO) {
        if (artistDTO.getId() == null || artistDTO.getImageId() == null || artistDTO.getId() < 1 || artistDTO.getImageId() < 1) {
            LOGGER.warn("Invalid artist data for deletion: {}", artistDTO);
            return false;
        }

        Long id = artistDTO.getId();

        // Delete Images
        List<AlbumDTO> list = albumService.findByArtistId(id);
        list.forEach(album -> albumService.delete(album.getId()));

        List<Long> userIds = new ArrayList<>();
        artistDao.findReviewsByArtistId(id).forEach(review -> userIds.add(review.getUser().getId()));
        artistDao.deleteReviewsFromArtist(id);
        userIds.forEach(userId -> userService.updateUserReviewAmount(userId));
        boolean deleted = artistDao.delete(id);
        imageService.delete(artistDTO.getImageId());
        if (deleted) {
            LOGGER.info("Artist with ID {} deleted successfully", id);
        } else {
            LOGGER.error("Failed to delete artist with ID {}", id);
        }
        return deleted;
    }

    @Override
    @Transactional
    public ArtistDTO create(ArtistDTO artistDTO) {
        LOGGER.info("Creating new artist from DTO: {}", artistDTO.getName());

        Image image;
        if (artistDTO.getImageId() == 0)
            image = imageService.findById(imageService.getDefaultImgId());
        else
            image = imageService.findById(artistDTO.getImageId());

        Artist artist = new Artist(artistDTO.getName(), artistDTO.getBio(), image);
        artist.setCreatedAt(LocalDateTime.now());
        artist.setUpdatedAt(LocalDateTime.now());
        artist.setRatingCount(0);
        artist.setAvgRating(0d);
        artist = artistDao.create(artist);
        LOGGER.info("Artist created successfully with ID: {}", artist.getId());

        if (artistDTO.getAlbums() != null) {
            LOGGER.info("Creating albums for artist: {} (ID: {})", artist.getName(), artist.getId());
            albumService.createAll(artistDTO.getAlbums(), artist.getId());
        }
        return artistMapper.toDTO(artist);
    }

    @Override
    @Transactional
    public ArtistDTO update(ArtistDTO artistDTO) {
        LOGGER.info("Updating artist from DTO: {} (ID: {})", artistDTO.getName(), artistDTO.getId());

        Artist artist = artistDao.findById(artistDTO.getId()).orElseThrow(() -> new ArtistNotFoundException("Artist with id " + artistDTO.getId() + " not found"));
        artist.setImage(imageService.findById(artistDTO.getImageId()));
        MergeUtils.mergeArtistFields(artist, artistDTO);

        artist = artistDao.update(artist);
        LOGGER.info("Artist updated successfully");

        if (artistDTO.getAlbums() != null) {
            LOGGER.info("Updating albums for artist: {} (ID: {})", artist.getName(), artist.getId());
            albumService.updateAll(artistDTO.getAlbums(), artist.getId());
        }
        return artistMapper.toDTO(artist);
    }

    @Override
    @Transactional
    public Boolean updateRating(Long artistId, Double roundedAvgRating, Integer ratingAmount) {
        LOGGER.info("Updating rating for artist with ID: {}", artistId);
        boolean updated = artistDao.updateRating(artistId, roundedAvgRating, ratingAmount);
        if (updated) {
            LOGGER.info("Rating updated successfully for artist ID: {}", artistId);
        } else {
            LOGGER.error("Failed to update rating for artist ID: {}", artistId);
        }
        return updated;
    }

    @Override
    @Transactional(readOnly = true)
    public Boolean hasUserReviewed(Long userId, Long artistId) {
        return artistDao.hasUserReviewed(userId, artistId);
    }

    @Override
    @Transactional(readOnly = true)
    public Long countAll() {
        return artistDao.countAll();
    }
}
