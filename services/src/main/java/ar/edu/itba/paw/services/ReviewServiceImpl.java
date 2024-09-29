package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.Album;
import ar.edu.itba.paw.models.Artist;
import ar.edu.itba.paw.models.Song;
import ar.edu.itba.paw.models.reviews.Review;
import ar.edu.itba.paw.models.reviews.ArtistReview;
import ar.edu.itba.paw.models.reviews.AlbumReview;
import ar.edu.itba.paw.models.reviews.SongReview;
import ar.edu.itba.paw.persistence.AlbumDao;
import ar.edu.itba.paw.persistence.ArtistDao;
import ar.edu.itba.paw.persistence.ReviewDao;
import ar.edu.itba.paw.persistence.SongDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewDao reviewDao;
    private final SongDao songDao;
    private final ArtistDao artistDao;
    private final AlbumDao albumDao;

    @Autowired
    public ReviewServiceImpl(ReviewDao reviewDao, SongDao songDao, ArtistDao artistDao, AlbumDao albumDao) {
        this.reviewDao = reviewDao;
        this.songDao = songDao;
        this.artistDao = artistDao;
        this.albumDao = albumDao;
    }

    @Override
    public int updateAvgRatingForAll(){
        List<Song> songs = songDao.findAll();
        List<Album> albums = albumDao.findAll();
        List<Artist> artists = artistDao.findAll();
        for (Song song : songs) {
            updateSongRating(song.getId());
        }
        for (Album album : albums) {
            updateAlbumRating(album.getId());
        }
        for (Artist artist : artists) {
            updateArtistRating(artist.getId());
        }
        return 0;
    }

    @Override
    public int update(Review review) {
        return reviewDao.update(review);
    }

    @Override
    public int deleteById(long id) {
        int res = 0;
        if (isArtistReview(id)) {
            res = reviewDao.deleteById(id);
            updateArtistRating(id);
        }
        if (isAlbumReview(id)) {
            res = reviewDao.deleteById(id);
            updateAlbumRating(id);
        }
        if (isSongReview(id)) {
            res = reviewDao.deleteById(id);
            updateSongRating(id);
        }
        return res;
    }

    @Override
    public Optional<ArtistReview> findArtistReviewById(long id) {
        return reviewDao.findArtistReviewById(id);
    }

    @Override
    public int saveArtistReview(ArtistReview review) {
        int result = reviewDao.saveArtistReview(review);
        if (result > 0) {
            updateArtistRating(review.getArtist().getId());
        }
        return result;
    }

    private void updateArtistRating(long artistId) {
        List<ArtistReview> reviews = reviewDao.findReviewsByArtistId(artistId);
        float avgRating = (float) reviews.stream().mapToInt(ArtistReview::getRating).average().orElse(0.0);
        float roundedAvgRating = Math.round(avgRating * 100.0) / 100.0f;
        int ratingAmount = reviews.size();
        artistDao.updateRating(artistId, roundedAvgRating, ratingAmount);
    }

    @Override
    public boolean hasUserReviewedArtist(long userId, long artistId) {
        return artistDao.hasUserReviewed(userId, artistId);
    }

    @Override
    public Optional<AlbumReview> findAlbumReviewById(long id) {
        return reviewDao.findAlbumReviewById(id);
    }

    @Override
    public Optional<ArtistReview> findArtistReviewByUserId(long userId, long artistId) {
        return reviewDao.findArtistReviewByUserId(userId, artistId);
    }

    @Override
    public Optional<AlbumReview> findAlbumReviewByUserId(long userId, long albumId) {
        return reviewDao.findAlbumReviewByUserId(userId, albumId);
    }

    @Override
    public Optional<SongReview> findSongReviewByUserId(long userId, long songId) {
        return reviewDao.findSongReviewByUserId(userId, songId);
    }

    @Override
    public int saveAlbumReview(AlbumReview review) {
        int result = reviewDao.saveAlbumReview(review);
        if (result > 0) {
            updateAlbumRating(review.getAlbum().getId());
        }
        return result;
    }

    private void updateAlbumRating(long albumId) {
        List<AlbumReview> reviews = reviewDao.findReviewsByAlbumId(albumId);
        float avgRating = (float) reviews.stream().mapToInt(AlbumReview::getRating).average().orElse(0.0);
        float roundedAvgRating = Math.round(avgRating * 100.0) / 100.0f;
        int ratingAmount = reviews.size();
        albumDao.updateRating(albumId, roundedAvgRating, ratingAmount);
    }

    @Override
    public boolean hasUserReviewedAlbum(long userId, long albumId) {
        return albumDao.hasUserReviewed(userId, albumId);
    }

    @Override
    public Optional<SongReview> findSongReviewById(long id) {
        return reviewDao.findSongReviewById(id);
    }


    @Override
    public int saveSongReview(SongReview review) {
        int result = reviewDao.saveSongReview(review);
        if (result > 0) {
            updateSongRating(review.getSong().getId());
        }
        return result;
    }

    private void updateSongRating(long songId) {
        List<SongReview> reviews = reviewDao.findReviewsBySongId(songId);
        float avgRating = (float) reviews.stream().mapToInt(SongReview::getRating).average().orElse(0.0);
        float roundedAvgRating = Math.round(avgRating * 100.0) / 100.0f;
        int ratingAmount = reviews.size();
        songDao.updateRating(songId, roundedAvgRating, ratingAmount);
    }

    @Override
    public boolean hasUserReviewedSong(long userId, long songId) {
        return songDao.hasUserReviewed(userId, songId);
    }

    @Override
    public int createLike(long userId, long reviewId) {
        reviewDao.createLike(userId, reviewId);
        return reviewDao.incrementLikes(reviewId);
    }

    @Override
    public int removeLike(long userId, long reviewId) {
        reviewDao.deleteLike(userId, reviewId);
        return reviewDao.decrementLikes(reviewId);
    }

    @Override
    public boolean isLiked(long userId, long reviewId) {
        return reviewDao.isLiked(userId, reviewId);
    }

    private <T extends Review> void setIsLiked(List<T> reviews, long userId) {
        for (T review : reviews) {
            if (userId < 1)
                review.setLiked(false);
            else
                review.setLiked(isLiked(userId, review.getId()));
        }
    }

    @Override
    public List<ArtistReview> findArtistReviewsPaginated(long artistId, int page, int pageSize, long loggedUserId) {
        List<ArtistReview> reviews = reviewDao.findArtistReviewsPaginated(artistId, page, pageSize);
        setIsLiked(reviews, loggedUserId);
        return reviews;
    }

    @Override
    public List<AlbumReview> findAlbumReviewsPaginated(long albumId, int page, int pageSize, long loggedUserId) {
        List<AlbumReview> reviews = reviewDao.findAlbumReviewsPaginated(albumId, page, pageSize);
        setIsLiked(reviews, loggedUserId);
        return reviews;
    }

    @Override
    public List<SongReview> findSongReviewsPaginated(long songId, int page, int pageSize, long loggedUserId) {
        List<SongReview> reviews = reviewDao.findSongReviewsPaginated(songId, page, pageSize);
        setIsLiked(reviews, loggedUserId);
        return reviews;
    }

    @Override
    public boolean isArtistReview(long reviewId) {
        return reviewDao.isArtistReview(reviewId);
    }

    @Override
    public boolean isAlbumReview(long reviewId) {
        return reviewDao.isAlbumReview(reviewId);
    }

    @Override
    public boolean isSongReview(long reviewId) {
        return reviewDao.isSongReview(reviewId);
    }

    @Override
    public List<Review> findReviewsByUserPaginated(long userId, int page, int pageSize, long loggedUserId) {
        List<Review> list = reviewDao.findReviewsByUserPaginated(userId, page, pageSize);
        setIsLiked(list, loggedUserId);
        return list;
    }

    @Override
    public List<Review> getPopularReviewsNDaysPaginated(int days, int page, int pageSize, long loggedUserId) {
        LocalDate nDaysAgo = LocalDate.now().minusDays(days);
        List<Review> list = reviewDao.getPopularReviewsSincePaginated(nDaysAgo,page, pageSize);
        setIsLiked(list, loggedUserId);
        return list;
    }

    @Override
    public List<Review> getReviewsFromFollowedUsersPaginated(Long userId, int page, int pageSize, long loggedUserId) {
        List<Review> list = reviewDao.getReviewsFromFollowedUsersPaginated(userId, page, pageSize);
        setIsLiked(list, loggedUserId);
        return list;
    }

    @Override
    public boolean block(Long reviewId) {
        return reviewDao.block(reviewId);
    }

    @Override
    public boolean unblock(Long reviewId) {
        return reviewDao.unblock(reviewId);
    }

}