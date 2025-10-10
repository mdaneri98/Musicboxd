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
import ar.edu.itba.paw.services.mappers.SongMapper;

import javax.mail.MessagingException;
import java.util.List;
import java.util.Optional;


@Service
public class ReviewServiceImpl implements ReviewService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReviewServiceImpl.class);

    private final ReviewDao reviewDao;
    private final SongService songService;
    private final UserDao userDao;
    private final ArtistService artistService;
    private final AlbumService albumService;
    private final UserService userService;
    private final EmailService emailService;
    private final NotificationService notificationService;
    private final ArtistMapper artistMapper;
    private final AlbumMapper albumMapper;
    private final SongMapper songMapper;
    private final UserMapper userMapper;    

    @Autowired
    public ReviewServiceImpl(ReviewDao reviewDao, SongService songService, ArtistService artistService, AlbumService albumService, UserService userService, EmailService emailService, NotificationService notificationService, UserDao userDao, ArtistMapper artistMapper, AlbumMapper albumMapper, SongMapper songMapper, UserMapper userMapper) {
        this.reviewDao = reviewDao;
        this.songService = songService;
        this.artistService = artistService;
        this.albumService = albumService;
        this.userService = userService;
        this.emailService = emailService;
        this.notificationService = notificationService;
        this.userDao = userDao;
        this.artistMapper = artistMapper;
        this.albumMapper = albumMapper;
        this.songMapper = songMapper;
        this.userMapper = userMapper;
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
        return reviewDao.findById(id).orElseThrow(() -> new ReviewNotFoundException("Review with id " + id + " not found")).toDTO();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewDTO> findAll() {
        return reviewDao.findAll().stream().map(Review::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewDTO> findPaginated(FilterType filterType, Integer page, Integer pageSize) {
        return reviewDao.findPaginated(filterType, page, pageSize).stream().map(Review::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ReviewDTO create(ReviewDTO review) {
        LOGGER.info("Creating new review: {}", review);
        if (review.getItemType().equals("artist")) return createArtistReview(review);
        else if (review.getItemType().equals("album")) return createAlbumReview(review);
        else if (review.getItemType().equals("song")) return createSongReview(review);
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
        LOGGER.info("Review created successfully with ID: {}", createdReview.getId());
        notificationService.notifyNewReview(createdReview, createdReview.getUser());
        return createdReview.toDTO();
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
        LOGGER.info("Review created successfully with ID: {}", createdReview.getId());
        notificationService.notifyNewReview(createdReview, createdReview.getUser());
        return createdReview.toDTO();
    }

    @Override
    @Transactional
    public ReviewDTO createSongReview(ReviewDTO review) {
        SongReview entity = new SongReview();
        entity.setUser(userDao.findById(review.getUserId()).orElseThrow(() -> new UserNotFoundException("User with id " + review.getUserId() + " not found")));
        Song song = songMapper.toEntity(songService.findById(review.getItemId()));
        entity.setSong(song);
        MergeUtils.mergeReviewFields(entity, review);
        Review createdReview = reviewDao.create(entity);
        updateUserReviewAmount(createdReview.getUser().getId());
        LOGGER.info("Review created successfully with ID: {}", createdReview.getId());
        notificationService.notifyNewReview(createdReview, createdReview.getUser());
        return createdReview.toDTO();
    }

    @Override
    @Transactional
    public ReviewDTO update(ReviewDTO review) {
        LOGGER.info("Updating review with ID: {}", review.getId());
        Review reviewEntity = reviewDao.findById(review.getId()).orElseThrow(() -> new ReviewNotFoundException("Review with id " + review.getId() + " not found"));
        MergeUtils.mergeReviewFields(reviewEntity, review);
        Review updatedReview = reviewDao.update(reviewEntity);
        LOGGER.info("Review updated successfully");
        return updatedReview.toDTO();
    }

    @Override
    @Transactional
    public Boolean delete(Long id) {
        LOGGER.info("Attempting to delete review with ID: {}", id);
        boolean res = false;
        if (isArtistReview(id)) {
            ArtistReview r = reviewDao.findArtistReviewById(id).orElseThrow(() -> new ReviewNotFoundException("Review with id " + id + " not found"));
            res = reviewDao.delete(id);
            updateUserReviewAmount(r.getUser().getId());
            updateArtistRating(r.getArtist().getId());
        }
        if (isAlbumReview(id)) {
            AlbumReview r = reviewDao.findAlbumReviewById(id).get();
            res = reviewDao.delete(id);
            updateUserReviewAmount(r.getUser().getId());
            updateAlbumRating(r.getAlbum().getId());
        }
        if (isSongReview(id)) {
            SongReview r = reviewDao.findSongReviewById(id).get();
            res = reviewDao.delete(id);
            updateUserReviewAmount(r.getUser().getId());
            updateSongRating(r.getSong().getId());
        }
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

    // @Override
    // @Transactional
    // public ReviewDTO updateArtistReview(ReviewDTO review) {
    //     LOGGER.info("Updating artist review with ID: {}", review.getId());
    //     ArtistReview reviewEntity = reviewDao.findArtistReviewById(review.getId()).orElseThrow(() -> new ReviewNotFoundException("Review with id " + review.getId() + " not found"));
    //     MergeUtils.mergeReviewFields(reviewEntity, review);
    //     Review r = reviewDao.update(reviewEntity);
    //     updateArtistRating(reviewEntity.getArtist().getId());
    //     LOGGER.info("Artist review updated and artist rating recalculated for artist ID: {}", review.getItemId());
    //     return r.toDTO();
    // }

    // @Override
    // @Transactional
    // public ReviewDTO updateAlbumReview(ReviewDTO review) {
    //     LOGGER.info("Updating album review with ID: {}", review.getId());
    //     AlbumReview reviewEntity = reviewDao.findAlbumReviewById(review.getId()).orElseThrow(() -> new ReviewNotFoundException("Review with id " + review.getId() + " not found"));
    //     MergeUtils.mergeReviewFields(reviewEntity, review);
    //     Review r = reviewDao.update(reviewEntity);
    //     updateAlbumRating(reviewEntity.getAlbum().getId());
    //     LOGGER.info("Album review updated and album rating recalculated for album ID: {}", review.getItemId());
    //     return r.toDTO();
    // }

    // @Override
    // @Transactional
    // public ReviewDTO updateSongReview(ReviewDTO review) {
    //     LOGGER.info("Updating song review with ID: {}", review.getId());
    //     SongReview reviewEntity = reviewDao.findSongReviewById(review.getId()).orElseThrow(() -> new ReviewNotFoundException("Review with id " + review.getId() + " not found"));
    //     MergeUtils.mergeReviewFields(reviewEntity, review);
    //     Review r = reviewDao.update(reviewEntity);
    //     updateSongRating(reviewEntity.getSong().getId());
    //     LOGGER.info("Song review updated and song rating recalculated for song ID: {}", review.getItemId());
    //     return r.toDTO();
    // }

    @Override
    @Transactional(readOnly = true)
    public ReviewDTO findArtistReviewById(Long id, Long loggedUserId) {
        ArtistReview review = reviewDao.findArtistReviewById(id).get();
        review.setTimeAgo(TimeUtils.formatTimeAgo(review.getCreatedAt()));
        review.setLiked(isLiked(loggedUserId, review.getId()));
        return review.toDTO();
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
        return result.toDTO();
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
        return review.toDTO();
    }

    @Override
    @Transactional(readOnly = true)
    public ReviewDTO findArtistReviewByUserId(Long userId, Long artistId, Long loggedUserId) {
        ArtistReview reviewOptional = reviewDao.findArtistReviewByUserId(userId, artistId).orElseThrow(() -> new ReviewNotFoundException("Review with user id " + userId + " and artist id " + artistId + " not found"));
        reviewOptional.setTimeAgo(TimeUtils.formatTimeAgo(reviewOptional.getCreatedAt()));
        reviewOptional.setLiked(isLiked(loggedUserId, reviewOptional.getId()));
        return reviewOptional.toDTO();
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
        return reviewOptional.get().toDTO();
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
        return reviewOptional.get().toDTO();
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
        return result.toDTO();
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
        return review.toDTO();
    }


    @Override
    @Transactional
    public ReviewDTO saveSongReview(ReviewDTO review) {
        SongReview reviewEntity = new SongReview();
        reviewEntity.setUser(userDao.findById(review.getUserId()).orElseThrow(() -> new UserNotFoundException("User with id " + review.getUserId() + " not found")));
        Song song = songMapper.toEntity(songService.findById(review.getItemId()));
        reviewEntity.setSong(song);
        MergeUtils.mergeReviewFields(reviewEntity, review);
        LOGGER.info("Saving new song review for song ID: {}", review.getItemId());
        SongReview result = reviewDao.saveSongReview(reviewEntity);
        updateUserReviewAmount(reviewEntity.getUser().getId());
        updateSongRating(reviewEntity.getSong().getId());
        notificationService.notifyNewReview(result, result.getUser());
        LOGGER.info("Song review saved, user review amount updated, and song rating recalculated");
        return result.toDTO();
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
        List<ReviewDTO> reviews = songService.findReviewsBySongId(songId);
        Double avgRating = reviews.stream().mapToInt(ReviewDTO::getRating).average().orElse(0.0);
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
        return reviews.stream().map(Review::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewDTO> findAlbumReviewsPaginated(Long albumId, Integer page, Integer pageSize, Long loggedUserId) {
        List<AlbumReview> reviews = reviewDao.findAlbumReviewsPaginated(albumId, page, pageSize);
        setIsLiked(reviews, loggedUserId);
        setTimeAgo(reviews);
        return reviews.stream().map(Review::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewDTO> findSongReviewsPaginated(Long songId, Integer page, Integer pageSize, Long loggedUserId) {
        List<SongReview> reviews = reviewDao.findSongReviewsPaginated(songId, page, pageSize);
        setIsLiked(reviews, loggedUserId);
        setTimeAgo(reviews);
        return reviews.stream().map(Review::toDTO).collect(Collectors.toList());
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
        return list.stream().map(Review::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewDTO> getPopularReviewsPaginated(Integer page, Integer pageSize, Long loggedUserId) {
        List<Review> list = reviewDao.getPopularReviewsPaginated(page, pageSize);
        setIsLiked(list, loggedUserId);
        setTimeAgo(list);
        return list.stream().map(Review::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewDTO> getReviewsFromFollowedUsersPaginated(Long userId, Integer page, Integer pageSize, Long loggedUserId) {
        List<Review> list = reviewDao.getReviewsFromFollowedUsersPaginated(userId, page, pageSize);
        setIsLiked(list, loggedUserId);
        setTimeAgo(list);
        return list.stream().map(Review::toDTO).collect(Collectors.toList());
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
