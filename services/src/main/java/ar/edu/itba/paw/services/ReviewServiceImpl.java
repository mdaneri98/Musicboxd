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
import ar.edu.itba.paw.exception.email.AcknowledgementEmailException;
import ar.edu.itba.paw.exception.conflict.UserAlreadyReviewedException;
import ar.edu.itba.paw.exception.not_found.ReviewNotFoundException;
import ar.edu.itba.paw.exception.not_found.UserNotFoundException;
import ar.edu.itba.paw.exception.UnkownReviewTypeException;
import ar.edu.itba.paw.exception.not_found.ArtistNotFoundException;
import ar.edu.itba.paw.exception.not_found.AlbumNotFoundException;
import ar.edu.itba.paw.exception.not_found.SongNotFoundException;
import ar.edu.itba.paw.exception.conflict.LikeAlreadyExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ar.edu.itba.paw.services.utils.TimeUtils;
import ar.edu.itba.paw.services.utils.MergeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.stream.Collectors;
import ar.edu.itba.paw.services.mappers.UserMapper;
import ar.edu.itba.paw.services.mappers.ReviewMapper;

import javax.mail.MessagingException;
import java.util.List;


@Service
public class ReviewServiceImpl implements ReviewService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReviewServiceImpl.class);

    private final ReviewDao reviewDao;
    private final UserDao userDao;
    private final ArtistDao artistDao;
    private final AlbumDao albumDao;
    private final SongDao songDao;
    private final SongService songService;
    private final ArtistService artistService;
    private final AlbumService albumService;
    private final UserService userService;
    private final EmailService emailService;
    private final NotificationService notificationService;
    private final UserMapper userMapper;   
    private final ReviewMapper reviewMapper;

    @Autowired
    public ReviewServiceImpl(ReviewDao reviewDao, UserDao userDao, ArtistDao artistDao, AlbumDao albumDao, SongDao songDao, SongService songService, ArtistService artistService, AlbumService albumService, UserService userService, EmailService emailService, NotificationService notificationService, UserMapper userMapper, ReviewMapper reviewMapper) {
        this.reviewDao = reviewDao;
        this.userDao = userDao;
        this.artistDao = artistDao;
        this.albumDao = albumDao;
        this.songDao = songDao;
        this.songService = songService;
        this.artistService = artistService;
        this.albumService = albumService;
        this.userService = userService;
        this.emailService = emailService;
        this.notificationService = notificationService;
        this.userMapper = userMapper;
        this.reviewMapper = reviewMapper;
    }


    @Transactional
    private Integer updateAvgRatingForAll(){
        LOGGER.info("Updating average ratings for all songs, albums, and artists");
        List<SongDTO> songs = songService.findAll();
        List<AlbumDTO> albums = albumService.findAll();
        List<ArtistDTO> artists = artistService.findAll();
        for (SongDTO song : songs) {
            songService.updateRating(song.getId());
        }
        for (AlbumDTO album : albums) {
            albumService.updateRating(album.getId());
        }
        for (ArtistDTO artist : artists) {
            artistService.updateRating(artist.getId());
        }
        LOGGER.info("Finished updating average ratings for all songs, albums, and artists");
        return 0;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewDTO> findBySubstring(String substring, Integer page, Integer size) {
        return reviewMapper.toDTOList(reviewDao.findBySubstring(substring, page, size));
    }

    @Override
    @Transactional(readOnly = true)
    public ReviewDTO findById(Long id, Long loggedUserId) {
        Review review = reviewDao.findById(id).orElseThrow(() -> new ReviewNotFoundException(id));
        review.setTimeAgo(TimeUtils.formatTimeAgo(review.getCreatedAt()));
        if (loggedUserId != null) review.setLiked(isLiked(loggedUserId, review.getId()));
        return reviewMapper.toDTO(review);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewDTO> findAll() {
        return reviewDao.findAll().stream().map(reviewMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewDTO> findPaginated(FilterType filterType, Integer page, Integer pageSize) {
        return findPaginated(filterType, page, pageSize, null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewDTO> findPaginated(FilterType filterType, Integer page, Integer pageSize, Long loggedUserId) {
        List<Review> reviews = reviewDao.findPaginated(filterType, page, pageSize);
        setIsLiked(reviews, loggedUserId);
        setTimeAgo(reviews);
        return reviews.stream().map(reviewMapper::toDTO).collect(Collectors.toList());
    }

    @Override 
    @Transactional
    public ReviewDTO create(ReviewDTO review) {
        LOGGER.info("Creating new review: {}", review);
        switch (review.getItemType()) {
            case "Artist" : return createArtistReview(review);
            case "Album" : return createAlbumReview(review);
            case "Song" : return createSongReview(review);
            default : throw new UnkownReviewTypeException(review.getItemType());
        }
    }

    @Override
    @Transactional
    public ReviewDTO createArtistReview(ReviewDTO review) {
        ArtistReview entity = new ArtistReview();
        entity.setUser(userDao.findById(review.getUserId()).orElseThrow(() -> new UserNotFoundException(review.getUserId())));
        entity.setArtist(artistDao.findById(review.getItemId()).orElseThrow(() -> new ArtistNotFoundException(review.getItemId())));
        if (artistService.hasUserReviewed(review.getUserId(), review.getItemId())) throw new UserAlreadyReviewedException(review.getUserId(), review.getItemId(), "Artist");
        MergeUtils.mergeReviewFields(entity, review);
        Review createdReview = reviewDao.create(entity);
        userService.updateUserReviewAmount(createdReview.getUser().getId());
        artistService.updateRating(createdReview.getItemId());
        notificationService.notifyNewReview(createdReview);
        LOGGER.info("Review created successfully with ID: {}", createdReview.getId());
        return reviewMapper.toDTO(createdReview);
    }

    @Override
    @Transactional
    public ReviewDTO createAlbumReview(ReviewDTO review) {
        AlbumReview entity = new AlbumReview();
        entity.setUser(userDao.findById(review.getUserId()).orElseThrow(() -> new UserNotFoundException(review.getUserId())));
        entity.setAlbum(albumDao.findById(review.getItemId()).orElseThrow(() -> new AlbumNotFoundException(review.getItemId())));
        if (albumService.hasUserReviewed(review.getUserId(), review.getItemId())) throw new UserAlreadyReviewedException(review.getUserId(), review.getItemId(), "Album");
        MergeUtils.mergeReviewFields(entity, review);
        Review createdReview = reviewDao.create(entity);
        userService.updateUserReviewAmount(createdReview.getUser().getId());
        albumService.updateRating(createdReview.getItemId());
        notificationService.notifyNewReview(createdReview);
        LOGGER.info("Review created successfully with ID: {}", createdReview.getId());
        return reviewMapper.toDTO(createdReview);
    }

    @Override
    @Transactional
    public ReviewDTO createSongReview(ReviewDTO review) {
        SongReview entity = new SongReview();
        entity.setUser(userDao.findById(review.getUserId()).orElseThrow(() -> new UserNotFoundException(review.getUserId())));
        entity.setSong(songDao.findById(review.getItemId()).orElseThrow(() -> new SongNotFoundException(review.getItemId())));
        if (songService.hasUserReviewed(review.getUserId(), review.getItemId())) throw new UserAlreadyReviewedException(review.getUserId(), review.getItemId(), "Song");
        MergeUtils.mergeReviewFields(entity, review);
        Review createdReview = reviewDao.create(entity);
        userService.updateUserReviewAmount(createdReview.getUser().getId());
        songService.updateRating(createdReview.getItemId());
        notificationService.notifyNewReview(createdReview);
        LOGGER.info("Review created successfully with ID: {}", createdReview.getId());
        return reviewMapper.toDTO(createdReview);
    }

    @Override
    @Transactional
    public ReviewDTO update(ReviewDTO review) {
        LOGGER.info("Updating review with ID: {}", review.getId());
        Review reviewEntity = reviewDao.findById(review.getId()).orElseThrow(() -> new ReviewNotFoundException(review.getId()));
        MergeUtils.mergeReviewFields(reviewEntity, review);
        Review updatedReview = reviewDao.update(reviewEntity);
        notificationService.notifyNewReview(updatedReview);
        ReviewDTO updatedReviewDTO = reviewMapper.toDTO(updatedReview);
        updateRatingForItem(updatedReviewDTO);
        LOGGER.info("Review updated successfully");
        return updatedReviewDTO;
    }

    @Transactional
    private Boolean updateRatingForItem(ReviewDTO review) {
        switch (review.getItemType()) {
            case "Artist" : return artistService.updateRating(review.getItemId());
            case "Album" : return albumService.updateRating(review.getItemId());
            case "Song" : return songService.updateRating(review.getItemId());
            default : throw new UnkownReviewTypeException(review.getItemType());
        }
    }

    @Override
    @Transactional
    public Boolean delete(Long id) {
        LOGGER.info("Attempting to delete review with ID: {}", id);
        Review review = reviewDao.findById(id).orElseThrow(() -> new ReviewNotFoundException(id));
        Boolean res = reviewDao.delete(id);
        updateRatingForItem(reviewMapper.toDTO(review));
        userService.updateUserReviewAmount(review.getUser().getId());
        if (res) LOGGER.info("Review with ID: {} deleted successfully", id);
        else LOGGER.warn("Failed to delete review with ID: {}", id);
        return res;
    }

    @Override
    public List<UserDTO> likedBy(Long reviewId, Integer pageNum, Integer pageSize) {
        return reviewDao.likedBy(reviewId, pageNum, pageSize).stream().map(userMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ReviewDTO findArtistReviewById(Long id, Long loggedUserId) {
        ArtistReview review = reviewDao.findArtistReviewById(id).orElseThrow(() -> new ReviewNotFoundException(id));
        review.setTimeAgo(TimeUtils.formatTimeAgo(review.getCreatedAt()));
        review.setLiked(isLiked(loggedUserId, review.getId()));
        return reviewMapper.toDTO(review);
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
        ArtistReview review = reviewDao.findArtistReviewByUserId(userId, artistId).orElseThrow(() -> new ReviewNotFoundException(userId, artistId, "Artist"));
        review.setTimeAgo(TimeUtils.formatTimeAgo(review.getCreatedAt()));
        review.setLiked(isLiked(loggedUserId, review.getId()));
        return reviewMapper.toDTO(review);
    }

    @Override
    @Transactional(readOnly = true)
    public ReviewDTO findAlbumReviewByUserId(Long userId, Long albumId, Long loggedUserId) {
        AlbumReview review = reviewDao.findAlbumReviewByUserId(userId, albumId).orElseThrow(() -> new ReviewNotFoundException(userId, albumId, "Album"));
        review.setTimeAgo(TimeUtils.formatTimeAgo(review.getCreatedAt()));
        review.setLiked(isLiked(loggedUserId, review.getId()));
        return reviewMapper.toDTO(review);
    }

    @Override
    @Transactional(readOnly = true)
    public ReviewDTO findSongReviewByUserId(Long userId, Long songId, Long loggedUserId) {
        SongReview review = reviewDao.findSongReviewByUserId(userId, songId).orElseThrow(() -> new ReviewNotFoundException(userId, songId, "Song"));
        review.setTimeAgo(TimeUtils.formatTimeAgo(review.getCreatedAt()));
        review.setLiked(isLiked(loggedUserId, review.getId()));
        return reviewMapper.toDTO(review);
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
    public Void createLike(Long userId, Long reviewId) {
        LOGGER.info("Creating like for review ID: {} by user ID: {}", reviewId, userId);

        User user = userDao.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        Review review = reviewDao.findById(reviewId).orElseThrow(() -> new ReviewNotFoundException(reviewId));
        if (isLiked(userId, reviewId)) throw new LikeAlreadyExistsException(userId, reviewId);
 
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
    public List<ReviewDTO> getReviewsFromFollowedUsersPaginated(Integer page, Integer pageSize, Long loggedUserId) {
        List<Review> list = reviewDao.getReviewsFromFollowedUsersPaginated(loggedUserId, page, pageSize);
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

        Review review = reviewDao.findById(reviewId).orElseThrow(() -> new ReviewNotFoundException(reviewId));
        User user = review.getUser();
        try {
            emailService.sendReviewAcknowledgement(ReviewAcknowledgementType.BLOCKED, user, review.getTitle(), review.getItemName(), review.getItemType());
            LOGGER.info("Acknowledgement email sent successfully");
        } catch (MessagingException e) {
            LOGGER.error("Failed to send acknowledgement email to user: {}", user.getEmail(), e);
            throw new AcknowledgementEmailException(user.getEmail(), e);
        }

        reviewDao.block(reviewId);
        LOGGER.info("Review blocked successfully: {}", reviewId);
        return null;
    }

    @Override
    @Transactional
    public Void unblock(Long reviewId) {
        LOGGER.info("Unblocking review with ID: {}", reviewId);

        Review review = reviewDao.findById(reviewId).orElseThrow(() -> new ReviewNotFoundException(reviewId));
        User user = review.getUser();
        try {
            emailService.sendReviewAcknowledgement(ReviewAcknowledgementType.UNBLOCKED, user, review.getTitle(), review.getItemName(), review.getItemType());
            LOGGER.info("Acknowledgement email sent successfully");
        } catch (MessagingException e) {
            LOGGER.error("Failed to send acknowledgement email to user: {}", user.getEmail(), e);
            throw new AcknowledgementEmailException(user.getEmail(), e);
        }

        reviewDao.unblock(reviewId);
        LOGGER.info("Review unblocked successfully: {}", reviewId);
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public Long countReviewsByUser(Long userId) {
        return reviewDao.countReviewsByUser(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public Long countReviewsFromFollowedUsers(Long loggedUserId) {
        return reviewDao.countReviewsFromFollowedUsers(loggedUserId);
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
