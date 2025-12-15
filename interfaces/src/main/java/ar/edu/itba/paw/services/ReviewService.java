package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.reviews.Review;
import ar.edu.itba.paw.models.FilterType;
import java.util.List;

public interface ReviewService extends CrudService<Review> {


    Review findArtistReviewById(Long id, Long loggedUserId);
    Review findArtistReviewByUserId(Long userId, Long artistId, Long loggedUserId);

    Review findAlbumReviewById(Long id, Long loggedUserId);
    Review findAlbumReviewByUserId(Long userId, Long albumId, Long loggedUserId);

    Review findSongReviewById(Long id, Long loggedUserId);
    Review findSongReviewByUserId(Long userId, Long songId, Long loggedUserId);

    List<User> likedBy(Long reviewId, Integer pageNum, Integer pageSize);
    Void createLike(Long userId, Long reviewId);
    Void removeLike(Long userId, Long reviewId);
    Boolean isLiked(Long userId, Long reviewId);
    List<Long> getLikedReviewIds(Long userId, List<Long> reviewIds);

    List<Review> findPaginated(FilterType filterType, Integer page, Integer pageSize, Long loggedUserId);
    List<Review> getReviewsFromFollowedUsersPaginated(Integer page, Integer pageSize, Long loggedUserId);   
    
    List<Review> findBySubstring(String substring, Integer page, Integer size);
    List<Review> findReviewsByUserPaginated(Long userId, Integer page, Integer pageSize, Long loggedUserId);
    List<Review> findArtistReviewsPaginated(Long artistId, Integer page, Integer pageSize, Long loggedUserId);
    List<Review> findAlbumReviewsPaginated(Long albumId, Integer page, Integer pageSize, Long loggedUserId);
    List<Review> findSongReviewsPaginated(Long songId, Integer page, Integer pageSize, Long loggedUserId);

    Boolean isArtistReview(Long reviewId);
    Boolean isAlbumReview(Long reviewId);
    Boolean isSongReview(Long reviewId);

    Void block(Long reviewId);
    Void unblock(Long reviewId);
    
    Review createArtistReview(Review review);
    Review createAlbumReview(Review review);
    Review createSongReview(Review review);
    
    Long countReviewsByUser(Long userId);
    Long countAll();
    Long countReviewsFromFollowedUsers(Long loggedUserId);
    void setContextDependentFields(Review review, Long loggedUserId);
    Review findAndSetContextDependentFields(Long id, Long loggedUserId);
}
