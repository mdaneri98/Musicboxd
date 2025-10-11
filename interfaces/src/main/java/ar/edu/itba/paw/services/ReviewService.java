package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.dtos.UserDTO;
import ar.edu.itba.paw.models.dtos.ReviewDTO;
import java.util.List;


public interface ReviewService extends CrudService<ReviewDTO> {

    ReviewDTO findArtistReviewById(Long id, Long loggedUserId);
    ReviewDTO findArtistReviewByUserId(Long userId, Long artistId, Long loggedUserId);
    ReviewDTO saveArtistReview(ReviewDTO review);

    ReviewDTO findAlbumReviewById(Long id, Long loggedUserId);
    ReviewDTO findAlbumReviewByUserId(Long userId, Long albumId, Long loggedUserId);
    ReviewDTO saveAlbumReview(ReviewDTO review);

    ReviewDTO findSongReviewById(Long id, Long loggedUserId);
    ReviewDTO findSongReviewByUserId(Long userId, Long songId, Long loggedUserId);
    ReviewDTO saveSongReview(ReviewDTO review);


    List<UserDTO> likedBy(Long reviewId, Integer pageNum, Integer pageSize);
    Void createLike(Long userId, Long reviewId);
    Void removeLike(Long userId, Long reviewId);
    Boolean isLiked(Long userId, Long reviewId);

    List<ReviewDTO> findReviewsByUserPaginated(Long userId, Integer page, Integer pageSize, Long loggedUserId);
    List<ReviewDTO> getPopularReviewsPaginated(Integer page, Integer pageSize, Long loggedUserId);
    List<ReviewDTO> getReviewsFromFollowedUsersPaginated(Long userId, Integer page, Integer pageSize, Long loggedUserId);

    List<ReviewDTO> findArtistReviewsPaginated(Long artistId, Integer page, Integer pageSize, Long loggedUserId);
    List<ReviewDTO> findAlbumReviewsPaginated(Long albumId, Integer page, Integer pageSize, Long loggedUserId);
    List<ReviewDTO> findSongReviewsPaginated(Long songId, Integer page, Integer pageSize, Long loggedUserId);

    Boolean hasUserReviewedArtist(Long userId, Long artistId);
    Boolean hasUserReviewedAlbum(Long userId, Long albumId);
    Boolean hasUserReviewedSong(Long userId, Long songId);
    Integer updateAvgRatingForAll();

    Boolean isArtistReview(Long reviewId);
    Boolean isAlbumReview(Long reviewId);
    Boolean isSongReview(Long reviewId);

    Void block(Long reviewId);
    Void unblock(Long reviewId);

    Void updateUserReviewAmount(Long userId);
    Void updateSongRating(Long songId);
    Void updateAlbumRating(Long albumId);
    Void updateArtistRating(Long artistId);
    Void updateRatingForItem(ReviewDTO review);
    // ReviewDTO updateSongReview(ReviewDTO review);
    // ReviewDTO updateArtistReview(ReviewDTO review);
    // ReviewDTO updateAlbumReview(ReviewDTO review);
    ReviewDTO createArtistReview(ReviewDTO review);
    ReviewDTO createAlbumReview(ReviewDTO review);
    ReviewDTO createSongReview(ReviewDTO review);
}
