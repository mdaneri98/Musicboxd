package ar.edu.itba.paw.usecases.review;

import ar.edu.itba.paw.domain.review.AlbumReview;
import ar.edu.itba.paw.domain.review.ArtistReview;
import ar.edu.itba.paw.domain.review.Review;
import ar.edu.itba.paw.domain.review.SongReview;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewApplicationService {

    private final GetReviewById getReviewById;
    private final GetAllReviews getAllReviews;
    private final CreateReview createReview;
    private final UpdateReview updateReview;
    private final DeleteReview deleteReview;
    private final LikeReview likeReview;
    private final UnlikeReview unlikeReview;
    private final IsReviewLiked isReviewLiked;
    private final CountAllReviews countAllReviews;
    private final GetReviewsByArtistId getReviewsByArtistId;
    private final GetReviewsByAlbumId getReviewsByAlbumId;
    private final GetReviewsBySongId getReviewsBySongId;
    private final GetReviewsByUserId getReviewsByUserId;
    private final BlockReview blockReview;
    private final UnblockReview unblockReview;
    private final GetReviewByUserAndArtist getReviewByUserAndArtist;
    private final GetReviewByUserAndAlbum getReviewByUserAndAlbum;
    private final GetReviewByUserAndSong getReviewByUserAndSong;
    private final CountReviewsByUserId countReviewsByUserId;

    @Autowired
    public ReviewApplicationService(
            GetReviewById getReviewById,
            GetAllReviews getAllReviews,
            CreateReview createReview,
            UpdateReview updateReview,
            DeleteReview deleteReview,
            LikeReview likeReview,
            UnlikeReview unlikeReview,
            IsReviewLiked isReviewLiked,
            CountAllReviews countAllReviews,
            GetReviewsByArtistId getReviewsByArtistId,
            GetReviewsByAlbumId getReviewsByAlbumId,
            GetReviewsBySongId getReviewsBySongId,
            GetReviewsByUserId getReviewsByUserId,
            BlockReview blockReview,
            UnblockReview unblockReview,
            GetReviewByUserAndArtist getReviewByUserAndArtist,
            GetReviewByUserAndAlbum getReviewByUserAndAlbum,
            GetReviewByUserAndSong getReviewByUserAndSong,
            CountReviewsByUserId countReviewsByUserId) {
        this.getReviewById = getReviewById;
        this.getAllReviews = getAllReviews;
        this.createReview = createReview;
        this.updateReview = updateReview;
        this.deleteReview = deleteReview;
        this.likeReview = likeReview;
        this.unlikeReview = unlikeReview;
        this.isReviewLiked = isReviewLiked;
        this.countAllReviews = countAllReviews;
        this.getReviewsByArtistId = getReviewsByArtistId;
        this.getReviewsByAlbumId = getReviewsByAlbumId;
        this.getReviewsBySongId = getReviewsBySongId;
        this.getReviewsByUserId = getReviewsByUserId;
        this.blockReview = blockReview;
        this.unblockReview = unblockReview;
        this.getReviewByUserAndArtist = getReviewByUserAndArtist;
        this.getReviewByUserAndAlbum = getReviewByUserAndAlbum;
        this.getReviewByUserAndSong = getReviewByUserAndSong;
        this.countReviewsByUserId = countReviewsByUserId;
    }

    public Review getReviewById(Long reviewId) {
        return getReviewById.execute(reviewId);
    }

    public List<Review> getAllReviews(Integer page, Integer size) {
        return getAllReviews.execute(page, size);
    }

    public Review createReview(CreateReviewCommand command) {
        return createReview.execute(command);
    }

    public Review updateReview(UpdateReviewCommand command) {
        return updateReview.execute(command);
    }

    public void deleteReview(Long reviewId) {
        deleteReview.execute(reviewId);
    }

    public void likeReview(Long reviewId, Long userId) {
        likeReview.execute(reviewId, userId);
    }

    public void unlikeReview(Long reviewId, Long userId) {
        unlikeReview.execute(reviewId, userId);
    }

    public boolean isReviewLiked(Long reviewId, Long userId) {
        return isReviewLiked.execute(reviewId, userId);
    }

    public Long countAllReviews() {
        return countAllReviews.execute();
    }

    public List<ArtistReview> getReviewsByArtistId(Long artistId, Integer page, Integer size) {
        return getReviewsByArtistId.execute(artistId, page, size);
    }

    public List<AlbumReview> getReviewsByAlbumId(Long albumId, Integer page, Integer size) {
        return getReviewsByAlbumId.execute(albumId, page, size);
    }

    public List<SongReview> getReviewsBySongId(Long songId, Integer page, Integer size) {
        return getReviewsBySongId.execute(songId, page, size);
    }

    public List<Review> getReviewsByUserId(Long userId, Integer page, Integer size) {
        return getReviewsByUserId.execute(userId, page, size);
    }

    public void blockReview(Long reviewId) {
        blockReview.execute(reviewId);
    }

    public void unblockReview(Long reviewId) {
        unblockReview.execute(reviewId);
    }

    public java.util.Optional<ArtistReview> getReviewByUserAndArtist(Long userId, Long artistId) {
        return getReviewByUserAndArtist.execute(userId, artistId);
    }

    public java.util.Optional<AlbumReview> getReviewByUserAndAlbum(Long userId, Long albumId) {
        return getReviewByUserAndAlbum.execute(userId, albumId);
    }

    public java.util.Optional<SongReview> getReviewByUserAndSong(Long userId, Long songId) {
        return getReviewByUserAndSong.execute(userId, songId);
    }

    public Long countReviewsByUserId(Long userId) {
        return countReviewsByUserId.execute(userId);
    }
}
