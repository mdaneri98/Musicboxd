package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.reviews.Review;
import ar.edu.itba.paw.models.reviews.ArtistReview;
import ar.edu.itba.paw.models.reviews.AlbumReview;
import ar.edu.itba.paw.models.reviews.SongReview;

import java.util.List;
import java.util.Optional;

public interface ReviewDao extends CrudDao<Review> {

    List<Review> findBySubstring(String substring, Integer page, Integer size);

    // Métodos específicos para ArtistReview
    Optional<ArtistReview> findArtistReviewById(Long id);
    Optional<ArtistReview> findArtistReviewByUserId(Long userId, Long artistId);
    ArtistReview saveArtistReview(ArtistReview review);

    // Métodos específicos para AlbumReview
    Optional<AlbumReview> findAlbumReviewById(Long id);
    Optional<AlbumReview> findAlbumReviewByUserId(Long userId, Long albumId);
    AlbumReview saveAlbumReview(AlbumReview review);

    // Métodos específicos para SongReview
    Optional<SongReview> findSongReviewById(Long id);
    Optional<SongReview> findSongReviewByUserId(Long userId, Long songId);
    SongReview saveSongReview(SongReview review);


    // Métodos para likes
    List<User> likedBy(Long reviewId, Integer page, Integer pageSize);
    Void createLike(Long userId, Long reviewId);
    Void deleteLike(Long userId, Long reviewId);
    Void updateLikeCount(Long reviewId);
    Boolean isLiked(Long userId, Long reviewId);
    List<Long> getLikedReviewIds(Long userId, List<Long> reviewIds);

    // Métodos de paginación
    List<ArtistReview> findArtistReviewsPaginated(Long artistId, Integer page, Integer pageSize);
    List<AlbumReview> findAlbumReviewsPaginated(Long albumId, Integer page, Integer pageSize);
    List<SongReview> findSongReviewsPaginated(Long songId, Integer page, Integer pageSize);

    List<Review> getReviewsFromFollowedUsersPaginated(Long userId, Integer page, Integer pageSize);
    List<Review> findReviewsByUserPaginated(Long userId, Integer page, Integer pageSize);

    Boolean isArtistReview(Long reviewId);
    Boolean isAlbumReview(Long reviewId);
    Boolean isSongReview(Long reviewId);

    Void block(Long reviewId);
    Void unblock(Long reviewId);

    Void updateCommentAmount(Long reviewId);
    
    // Count methods for pagination
    Long countAll();
    Long countReviewsByUser(Long userId);
    Long countReviewsFromFollowedUsers(Long userId);
}
