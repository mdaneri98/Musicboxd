package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.models.reviews.Review;
import ar.edu.itba.paw.models.reviews.ArtistReview;
import ar.edu.itba.paw.models.reviews.AlbumReview;
import ar.edu.itba.paw.models.reviews.SongReview;
import ar.edu.itba.paw.models.dtos.SongDTO;
import ar.edu.itba.paw.models.dtos.AlbumDTO;
import ar.edu.itba.paw.models.dtos.ArtistDTO;
import ar.edu.itba.paw.models.dtos.UserDTO;
import ar.edu.itba.paw.models.dtos.ReviewDTO;
import ar.edu.itba.paw.persistence.*;
import ar.edu.itba.paw.services.exception.AcknowledgementEmailException;
import ar.edu.itba.paw.services.exception.ReviewNotFoundException;
import ar.edu.itba.paw.services.exception.UserNotFoundException;
import ar.edu.itba.paw.services.exception.UnkownReviewTypeException;
import ar.edu.itba.paw.services.exception.SongNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ar.edu.itba.paw.services.utils.TimeUtils;
import ar.edu.itba.paw.services.utils.MergeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.stream.Collectors;
import ar.edu.itba.paw.services.mappers.UserMapper;
import ar.edu.itba.paw.services.mappers.ArtistMapper;
import ar.edu.itba.paw.services.mappers.AlbumMapper;
import ar.edu.itba.paw.services.mappers.ReviewMapper;

import javax.mail.MessagingException;
import java.util.List;
import java.util.Optional;


@Service
public class ReviewServiceImpl implements ReviewService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReviewServiceImpl.class);

    private final ReviewDao reviewDao;
    private final SongDao songDao;
    private final UserDao userDao;
    private final SongService songService;
    private final ArtistService artistService;
    private final AlbumService albumService;
    private final UserService userService;
    private final EmailService emailService;
    private final NotificationService notificationService;
    private final ArtistMapper artistMapper;
    private final AlbumMapper albumMapper;
    private final UserMapper userMapper;   
    private final ReviewMapper reviewMapper;

    @Autowired
    public ReviewServiceImpl(ReviewDao reviewDao, SongDao songDao, UserDao userDao, SongService songService, ArtistService artistService, AlbumService albumService, UserService userService, EmailService emailService, NotificationService notificationService, ArtistMapper artistMapper, AlbumMapper albumMapper, UserMapper userMapper, ReviewMapper reviewMapper) {
        this.reviewDao = reviewDao;
        this.songService = songService;
        this.artistService = artistService;
        this.albumService = albumService;
        this.userService = userService;
        this.emailService = emailService;
        this.notificationService = notificationService;
        this.userDao = userDao;
        this.songDao = songDao;
        this.artistMapper = artistMapper;
        this.albumMapper = albumMapper;
        this.userMapper = userMapper;
        this.reviewMapper = reviewMapper;
    }

    @Override
    @Transactional
    public Integer updateAvgRatingForAll(){
        LOGGER.info("Updating average ratings for all songs, albums, and artists");
        List<SongDTO> songs = songService.findAll();
        List<AlbumDTO> albums = albumService.findAll();
        List<ArtistDTO> artists = artistService.findAll();
        for (SongDTO song : songs) {
            updateSongRating(song.getId());
        }
        for (AlbumDTO album : albums) {
            updateAlbumRating(album.getId());
        }
        for (ArtistDTO artist : artists) {
            updateArtistRating(artist.getId());
        }
        LOGGER.info("Finished updating average ratings for all songs, albums, and artists");
        return 0;
    }

    @Override
    @Transactional(readOnly = true)
    public ReviewDTO findById(Long id) {
        return reviewMapper.toDTO(reviewDao.findById(id).orElseThrow(() -> new ReviewNotFoundException("Review with id " + id + " not found")));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewDTO> findAll() {
        return reviewDao.findAll().stream().map(reviewMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewDTO> findPaginated(FilterType filterType, Integer page, Integer pageSize) {
        return reviewDao.findPaginated(filterType, page, pageSize).stream().map(reviewMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ReviewDTO create(ReviewDTO review) {
        LOGGER.info("Creating new review: {}", review);
        if (review.getItemType().equals("Artist")) return createArtistReview(review);
        else if (review.getItemType().equals("Album")) return createAlbumReview(review);
        else if (review.getItemType().equals("Song")) return createSongReview(review);
        else throw new UnkownReviewTypeException("Review with item type " + review.getItemType() + " not found");
    }

    @Override
    @Transactional
    public ReviewDTO createArtistReview(ReviewDTO review) {
        ArtistReview entity = new ArtistReview();
        entity.setUser(userDao.findById(review.getUserId()).orElseThrow(() -> new UserNotFoundException("User with id " + review.getUserId() + " not found")));
        Artist artist = artistMapper.toEntity(artistService.findById(review.getItemId()));
        entity.setArtist(artist);
        MergeUtils.mergeReviewFields(entity, review);
        Review createdReview = reviewDao.create(entity);
        updateUserReviewAmount(createdReview.getUser().getId());
        updateRatingForItem(review);
        LOGGER.info("Review created successfully with ID: {}", createdReview.getId());
        notificationService.notifyNewReview(createdReview, createdReview.getUser());
        return reviewMapper.toDTO(createdReview);
    }

    @Override
    @Transactional
    public ReviewDTO createAlbumReview(ReviewDTO review) {
        AlbumReview entity = new AlbumReview();
        entity.setUser(userDao.findById(review.getUserId()).orElseThrow(() -> new UserNotFoundException("User with id " + review.getUserId() + " not found")));
        Album album = albumMapper.toEntity(albumService.findById(review.getItemId()));
        entity.setAlbum(album);
        MergeUtils.mergeReviewFields(entity, review);
        Review createdReview = reviewDao.create(entity);
        updateUserReviewAmount(createdReview.getUser().getId());
        updateRatingForItem(review);
        LOGGER.info("Review created successfully with ID: {}", createdReview.getId());
        notificationService.notifyNewReview(createdReview, createdReview.getUser());
        return reviewMapper.toDTO(createdReview);
    }

    @Override
    @Transactional
    public ReviewDTO createSongReview(ReviewDTO review) {
        SongReview entity = new SongReview();
        entity.setUser(userDao.findById(review.getUserId()).orElseThrow(() -> new UserNotFoundException("User with id " + review.getUserId() + " not found")));
        Song song = songDao.findById(review.getItemId()).orElseThrow(() -> new SongNotFoundException("Song with id " + review.getItemId() + " not found"));
        entity.setSong(song);
        MergeUtils.mergeReviewFields(entity, review);
        Review createdReview = reviewDao.create(entity);
        updateUserReviewAmount(createdReview.getUser().getId());
        updateRatingForItem(review);
        LOGGER.info("Review created successfully with ID: {}", createdReview.getId());
        notificationService.notifyNewReview(createdReview, createdReview.getUser());
        return reviewMapper.toDTO(createdReview);
    }

    @Override
    @Transactional
    public ReviewDTO update(ReviewDTO review) {
        LOGGER.info("Updating review with ID: {}", review.getId());
        User user = userDao.findById(review.getUserId()).orElseThrow(() -> new UserNotFoundException("User with id " + review.getUserId() + " not found"));
        Review reviewEntity = reviewDao.findById(review.getId()).orElseThrow(() -> new ReviewNotFoundException("Review with id " + review.getId() + " not found"));
        reviewEntity.setUser(user);
        MergeUtils.mergeReviewFields(reviewEntity, review);
        Review updatedReview = reviewDao.update(reviewEntity);
        updateRatingForItem(review);
        notificationService.notifyNewReview(updatedReview, updatedReview.getUser());
        LOGGER.info("Review updated successfully");
        return reviewMapper.toDTO(updatedReview);
    }

    @Override
    @Transactional
    public Void updateRatingForItem(ReviewDTO review) {
        if(review.getItemType().equals("Artist")) {
            updateArtistRating(review.getItemId());
        } else if(review.getItemType().equals("Album")) {
            updateAlbumRating(review.getItemId());
        } else if(review.getItemType().equals("Song")) {
            updateSongRating(review.getItemId());
        }
        return null;
    }

    @Override
    @Transactional
    public Boolean delete(Long id) {
        LOGGER.info("Attempting to delete review with ID: {}", id);
        boolean res = false;
        Review review = reviewDao.findById(id).orElseThrow(() -> new ReviewNotFoundException("Review with id " + id + " not found"));
        res = reviewDao.delete(id);
        updateRatingForItem(reviewMapper.toDTO(review));
        updateUserReviewAmount(review.getUser().getId());
        if (res) {
            LOGGER.info("Review with ID: {} deleted successfully", id);
        } else {
            LOGGER.warn("Failed to delete review with ID: {}", id);
        }
        return res;
    }

    @Override
    public List<UserDTO> likedBy(Long reviewId, Integer pageNum, Integer pageSize) {
        return reviewDao.likedBy(reviewId, pageNum, pageSize).stream().map(userMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ReviewDTO findArtistReviewById(Long id, Long loggedUserId) {
        ArtistReview review = reviewDao.findArtistReviewById(id).get();
        review.setTimeAgo(TimeUtils.formatTimeAgo(review.getCreatedAt()));
        review.setLiked(isLiked(loggedUserId, review.getId()));
        return reviewMapper.toDTO(review);
    }

    @Override
    @Transactional
    public ReviewDTO saveArtistReview(ReviewDTO review) {
        ArtistReview reviewEntity = new ArtistReview();
        reviewEntity.setUser(userDao.findById(review.getUserId()).orElseThrow(() -> new UserNotFoundException("User with id " + review.getUserId() + " not found")));
        Artist artist = artistMapper.toEntity(artistService.findById(review.getItemId()));
        reviewEntity.setArtist(artist);
        MergeUtils.mergeReviewFields(reviewEntity, review);
        LOGGER.info("Saving new artist review for artist ID: {}", review.getItemId());
        ArtistReview result = reviewDao.saveArtistReview(reviewEntity);
        updateUserReviewAmount(reviewEntity.getUser().getId());
        updateArtistRating(reviewEntity.getArtist().getId());
        notificationService.notifyNewReview(result, result.getUser());
        LOGGER.info("Artist review saved, user review amount updated, and artist rating recalculated");
        return reviewMapper.toDTO(result);
    }

    @Override
    @Transactional
    public Void updateArtistRating(Long artistId) {
        LOGGER.info("Updating rating for artist ID: {}", artistId);
        List<ReviewDTO> reviews = artistService.findReviewsByArtistId(artistId);
        Double avgRating = reviews.stream().mapToInt(ReviewDTO::getRating).average().orElse(0.0);
        Double roundedAvgRating = Math.round(avgRating * 100.0) / 100.0;
        Integer ratingAmount = reviews.size();
        artistService.updateRating(artistId, roundedAvgRating, ratingAmount);
        LOGGER.info("Artist rating updated. New average rating: {}, Total reviews: {}", roundedAvgRating, ratingAmount);
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public Boolean hasUserReviewedArtist(Long userId, Long artistId) {
        return artistService.hasUserReviewed(userId, artistId);
    }

    @Override
    @Transactional(readOnly = true)
    public ReviewDTO findAlbumReviewById(Long id, Long loggedUserId) {
        AlbumReview review = reviewDao.findAlbumReviewById(id).get();
        review.setTimeAgo(TimeUtils.formatTimeAgo(review.getCreatedAt()));
        review.setLiked(isLiked(loggedUserId, review.getId()));
        return reviewMapper.toDTO(review);
    }

    @Override
    @Transactional(readOnly = true)
    public ReviewDTO findArtistReviewByUserId(Long userId, Long artistId, Long loggedUserId) {
        ArtistReview reviewOptional = reviewDao.findArtistReviewByUserId(userId, artistId).orElseThrow(() -> new ReviewNotFoundException("Review with user id " + userId + " and artist id " + artistId + " not found"));
        reviewOptional.setTimeAgo(TimeUtils.formatTimeAgo(reviewOptional.getCreatedAt()));
        reviewOptional.setLiked(isLiked(loggedUserId, reviewOptional.getId()));
        return reviewMapper.toDTO(reviewOptional);
    }

    @Override
    @Transactional(readOnly = true)
    public ReviewDTO findAlbumReviewByUserId(Long userId, Long albumId, Long loggedUserId) {
        Optional<AlbumReview> reviewOptional = reviewDao.findAlbumReviewByUserId(userId, albumId);
        if (reviewOptional.isPresent()) {
            Review review = reviewOptional.get();
            review.setTimeAgo(TimeUtils.formatTimeAgo(review.getCreatedAt()));
            review.setLiked(isLiked(loggedUserId, review.getId()));
        }
        return reviewMapper.toDTO(reviewOptional.get());
    }

    @Override
    @Transactional(readOnly = true)
    public ReviewDTO findSongReviewByUserId(Long userId, Long songId, Long loggedUserId) {
        Optional<SongReview> reviewOptional = reviewDao.findSongReviewByUserId(userId, songId);
        if (reviewOptional.isPresent()) {
            Review review = reviewOptional.get();
            review.setTimeAgo(TimeUtils.formatTimeAgo(review.getCreatedAt()));
            review.setLiked(isLiked(loggedUserId, review.getId()));
        }
        return reviewMapper.toDTO(reviewOptional.get());
    }

    @Override
    @Transactional
    public ReviewDTO saveAlbumReview(ReviewDTO review) {
        AlbumReview reviewEntity = new AlbumReview();
        reviewEntity.setUser(userDao.findById(review.getUserId()).orElseThrow(() -> new UserNotFoundException("User with id " + review.getUserId() + " not found")));
        Album album = albumMapper.toEntity(albumService.findById(review.getItemId()));
        reviewEntity.setAlbum(album);
        MergeUtils.mergeReviewFields(reviewEntity, review);
        LOGGER.info("Saving new album review for album ID: {}", review.getItemId());
        AlbumReview result = reviewDao.saveAlbumReview(reviewEntity);
        updateUserReviewAmount(reviewEntity.getUser().getId());
        updateAlbumRating(reviewEntity.getAlbum().getId());
        notificationService.notifyNewReview(result, result.getUser());
        LOGGER.info("Album review saved, user review amount updated, and album rating recalculated");
        return reviewMapper.toDTO(result);
    }

    @Override
    @Transactional
    public Void updateAlbumRating(Long albumId) {
        LOGGER.info("Updating rating for album ID: {}", albumId);
        List<ReviewDTO> reviews = albumService.findReviewsByAlbumId(albumId);
        Double avgRating = reviews.stream().mapToInt(ReviewDTO::getRating).average().orElse(0.0);
        Double roundedAvgRating = Math.round(avgRating * 100.0) / 100.0;
        int ratingAmount = reviews.size();
        albumService.updateRating(albumId, roundedAvgRating, ratingAmount);
        LOGGER.info("Album rating updated. New average rating: {}, Total reviews: {}", roundedAvgRating, ratingAmount);
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public Boolean hasUserReviewedAlbum(Long userId, Long albumId) {
        return albumService.hasUserReviewed(userId, albumId);
    }

    @Override
    @Transactional(readOnly = true)
    public ReviewDTO findSongReviewById(Long id, Long loggedUserId) {
        SongReview review = reviewDao.findSongReviewById(id).get();
        review.setTimeAgo(TimeUtils.formatTimeAgo(review.getCreatedAt()));
        review.setLiked(isLiked(loggedUserId, review.getId()));
        return reviewMapper.toDTO(review);
    }


    @Override
    @Transactional
    public ReviewDTO saveSongReview(ReviewDTO review) {
        SongReview reviewEntity = new SongReview();
        reviewEntity.setUser(userDao.findById(review.getUserId()).orElseThrow(() -> new UserNotFoundException("User with id " + review.getUserId() + " not found")));
        Song song = songDao.findById(review.getItemId()).orElseThrow(() -> new SongNotFoundException("Song with id " + review.getItemId() + " not found"));
        reviewEntity.setSong(song);
        MergeUtils.mergeReviewFields(reviewEntity, review);
        LOGGER.info("Saving new song review for song ID: {}", review.getItemId());
        SongReview result = reviewDao.saveSongReview(reviewEntity);
        updateUserReviewAmount(reviewEntity.getUser().getId());
        updateSongRating(reviewEntity.getSong().getId());
        notificationService.notifyNewReview(result, result.getUser());
        LOGGER.info("Song review saved, user review amount updated, and song rating recalculated");
        return reviewMapper.toDTO(result);
    }

    @Override
    @Transactional
    public Void updateUserReviewAmount(Long userId) {
        LOGGER.info("Updating review amount for user ID: {}", userId);
        userService.updateUserReviewAmount(userId);
        LOGGER.info("User review amount updated for user ID: {}", userId);
        return null;
    }

    @Override
    @Transactional
    public Void updateSongRating(Long songId) {
        LOGGER.info("Updating rating for song ID: {}", songId);
        List<SongReview> reviews = songDao.findReviewsBySongId(songId);
        Double avgRating = reviews.stream().mapToInt(Review::getRating).average().orElse(0.0);
        Double roundedAvgRating = Math.round(avgRating * 100.0) / 100.0;
        int ratingAmount = reviews.size();
        songService.updateRating(songId, roundedAvgRating, ratingAmount);
        LOGGER.info("Song rating updated. New average rating: {}, Total reviews: {}", roundedAvgRating, ratingAmount);
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public Boolean hasUserReviewedSong(Long userId, Long songId) {
        return songService.hasUserReviewed(userId, songId);
    }

    @Override
    @Transactional
    public Void createLike(Long userId, Long reviewId) {
        LOGGER.info("Creating like for review ID: {} by user ID: {}", reviewId, userId);

        User user = userDao.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with ID " + userId + " not found"));

        Review review = reviewDao.findById(reviewId)
                .orElseThrow(() -> new UserNotFoundException("Review with ID " + reviewId + " not found"));

        reviewDao.createLike(userId, reviewId);
        reviewDao.updateLikeCount(reviewId);
        notificationService.notifyLike(review, user);

        LOGGER.info("Like created and like count incremented for review ID: {}", reviewId);
        return null;
    }

    @Override
    @Transactional
    public Void removeLike(Long userId, Long reviewId) {
        LOGGER.info("Removing like for review ID: {} by user ID: {}", reviewId, userId);
        reviewDao.deleteLike(userId, reviewId);
        reviewDao.updateLikeCount(reviewId);
        LOGGER.info("Like removed and like count decremented for review ID: {}", reviewId);
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public Boolean isLiked(Long userId, Long reviewId) {
        return reviewDao.isLiked(userId, reviewId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewDTO> findArtistReviewsPaginated(Long artistId, Integer page, Integer pageSize, Long loggedUserId) {
        List<ArtistReview> reviews = reviewDao.findArtistReviewsPaginated(artistId, page, pageSize);
        setIsLiked(reviews, loggedUserId);
        setTimeAgo(reviews);
        return reviews.stream().map(reviewMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewDTO> findAlbumReviewsPaginated(Long albumId, Integer page, Integer pageSize, Long loggedUserId) {
        List<AlbumReview> reviews = reviewDao.findAlbumReviewsPaginated(albumId, page, pageSize);
        setIsLiked(reviews, loggedUserId);
        setTimeAgo(reviews);
        return reviews.stream().map(reviewMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewDTO> findSongReviewsPaginated(Long songId, Integer page, Integer pageSize, Long loggedUserId) {
        List<SongReview> reviews = reviewDao.findSongReviewsPaginated(songId, page, pageSize);
        setIsLiked(reviews, loggedUserId);
        setTimeAgo(reviews);
        return reviews.stream().map(reviewMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Boolean isArtistReview(Long reviewId) {
        return reviewDao.isArtistReview(reviewId);
    }

    @Override
    @Transactional(readOnly = true)
    public Boolean isAlbumReview(Long reviewId) {
        return reviewDao.isAlbumReview(reviewId);
    }

    @Override
    @Transactional(readOnly = true)
    public Boolean isSongReview(Long reviewId) {
        return reviewDao.isSongReview(reviewId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewDTO> findReviewsByUserPaginated(Long userId, Integer page, Integer pageSize, Long loggedUserId) {
        List<Review> list = reviewDao.findReviewsByUserPaginated(userId, page, pageSize);
        setIsLiked(list, loggedUserId);
        setTimeAgo(list);
        return list.stream().map(reviewMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewDTO> getPopularReviewsPaginated(Integer page, Integer pageSize, Long loggedUserId) {
        List<Review> list = reviewDao.getPopularReviewsPaginated(page, pageSize);
        setIsLiked(list, loggedUserId);
        setTimeAgo(list);
        return list.stream().map(reviewMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewDTO> getReviewsFromFollowedUsersPaginated(Long userId, Integer page, Integer pageSize, Long loggedUserId) {
        List<Review> list = reviewDao.getReviewsFromFollowedUsersPaginated(userId, page, pageSize);
        setIsLiked(list, loggedUserId);
        setTimeAgo(list);
        return list.stream().map(reviewMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Long countAll() {
        return reviewDao.countAll();
    }

    @Override
    @Transactional
    public Void block(Long reviewId) {
        LOGGER.info("Blocking review with ID: {}", reviewId);

        Optional<Review> reviewOptional = reviewDao.findById(reviewId);
        if (reviewOptional.isEmpty())
            throw new IllegalArgumentException("Review with ID: " + reviewId + " does not exist");

        Review review = reviewOptional.get();
        User user = review.getUser();
        try {
            emailService.sendReviewAcknowledgement(ReviewAcknowledgementType.BLOCKED, user, review.getTitle(), review.getItemName(), review.getItemType());
            LOGGER.info("Acknowledgement email sent successfully");
        } catch (MessagingException e) {
            LOGGER.error("Failed to send acknowledgement email to user: {}", user.getEmail(), e);
            throw new AcknowledgementEmailException("No se pudo enviar el reconocimiento del email al usuario " + user.getEmail(), e);
        }

        reviewDao.block(reviewId);
        LOGGER.info("Review blocked successfully: {}", reviewId);
        return null;
    }

    @Override
    @Transactional
    public Void unblock(Long reviewId) {
        LOGGER.info("Unblocking review with ID: {}", reviewId);

        Optional<Review> reviewOptional = reviewDao.findById(reviewId);
        if (reviewOptional.isEmpty())
            throw new IllegalArgumentException("Review with ID: " + reviewId + " does not exist");

        Review review = reviewOptional.get();
        User user = review.getUser();
        try {

            emailService.sendReviewAcknowledgement(ReviewAcknowledgementType.UNBLOCKED, user, review.getTitle(), review.getItemName(), review.getItemType());
            LOGGER.info("Acknowledgement email sent successfully");
        } catch (MessagingException e) {
            LOGGER.error("Failed to send acknowledgement email to user: {}", user.getEmail(), e);
            throw new AcknowledgementEmailException("No se pudo enviar el reconocimiento del email al usuario " + user.getEmail(), e);
        }

        reviewDao.unblock(reviewId);
        LOGGER.info("Review unblocked successfully: {}", reviewId);
        return null;
    }

    private <T extends Review> void setTimeAgo(List<T> reviews) {
        for (T review : reviews) {
            review.setTimeAgo(TimeUtils.formatTimeAgo(review.getCreatedAt()));
        }
    }

    private <T extends Review> void setIsLiked(List<T> reviews, long userId) {
        for (T review : reviews) {
            if (userId < 1)
                review.setLiked(false);
            else
                review.setLiked(isLiked(userId, review.getId()));
        }
    }
}
