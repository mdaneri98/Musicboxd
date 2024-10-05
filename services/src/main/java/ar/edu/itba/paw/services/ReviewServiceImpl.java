package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.Album;
import ar.edu.itba.paw.models.Artist;
import ar.edu.itba.paw.models.FilterType;
import ar.edu.itba.paw.models.Song;
import ar.edu.itba.paw.models.reviews.Review;
import ar.edu.itba.paw.models.reviews.ArtistReview;
import ar.edu.itba.paw.models.reviews.AlbumReview;
import ar.edu.itba.paw.models.reviews.SongReview;
import ar.edu.itba.paw.persistence.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewDao reviewDao;
    private final SongService songService;
    private final ArtistService artistService;
    private final AlbumService albumService;
    private final UserService userService;

    @Autowired
    public ReviewServiceImpl(ReviewDao reviewDao, SongService songService, ArtistService artistService, AlbumService albumService, UserService userService) {
        this.reviewDao = reviewDao;
        this.songService = songService;
        this.artistService = artistService;
        this.albumService = albumService;
        this.userService = userService;
    }

    @Override
    @Transactional
    public int updateAvgRatingForAll(){
        List<Song> songs = songService.findAll();
        List<Album> albums = albumService.findAll();
        List<Artist> artists = artistService.findAll();
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
    @Transactional(readOnly = true)
    public Optional<Review> find(long id) {
        return Optional.empty();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Review> findAll() {
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Review> findPaginated(FilterType filterType, int page, int pageSize) {
        return null;
    }

    @Override
    @Transactional
    public Review create(Review entity) {
        return null;
    }

    @Override
    @Transactional
    public Review update(Review review) {
        return reviewDao.update(review);
    }

    @Override
    @Transactional
    public boolean delete(long id) {
        boolean res = false;
        if (isArtistReview(id)) {
            res = reviewDao.delete(id);
            updateArtistRating(id);
        }
        if (isAlbumReview(id)) {
            res = reviewDao.delete(id);
            updateAlbumRating(id);
        }
        if (isSongReview(id)) {
            res = reviewDao.delete(id);
            updateSongRating(id);
        }
        return res;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ArtistReview> findArtistReviewById(long id) {
        return reviewDao.findArtistReviewById(id);
    }

    @Override
    @Transactional
    public ArtistReview saveArtistReview(ArtistReview review) {
        ArtistReview result = reviewDao.saveArtistReview(review);
        updateUserReviewAmount(review.getUser().getId());
        updateArtistRating(review.getArtist().getId());
        return result;
    }

    @Transactional
    private void updateArtistRating(long artistId) {
        List<ArtistReview> reviews = reviewDao.findReviewsByArtistId(artistId);
        float avgRating = (float) reviews.stream().mapToInt(ArtistReview::getRating).average().orElse(0.0);
        float roundedAvgRating = Math.round(avgRating * 100.0) / 100.0f;
        int ratingAmount = reviews.size();
        artistService.updateRating(artistId, roundedAvgRating, ratingAmount);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasUserReviewedArtist(long userId, long artistId) {
        return artistService.hasUserReviewed(userId, artistId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AlbumReview> findAlbumReviewById(long id) {
        return reviewDao.findAlbumReviewById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ArtistReview> findArtistReviewByUserId(long userId, long artistId) {
        return reviewDao.findArtistReviewByUserId(userId, artistId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AlbumReview> findAlbumReviewByUserId(long userId, long albumId) {
        return reviewDao.findAlbumReviewByUserId(userId, albumId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SongReview> findSongReviewByUserId(long userId, long songId) {
        return reviewDao.findSongReviewByUserId(userId, songId);
    }

    @Override
    @Transactional
    public AlbumReview saveAlbumReview(AlbumReview review) {
        AlbumReview result = reviewDao.saveAlbumReview(review);
        updateUserReviewAmount(review.getUser().getId());
        updateAlbumRating(review.getAlbum().getId());
        return result;
    }

    @Transactional
    private void updateAlbumRating(long albumId) {
        List<AlbumReview> reviews = reviewDao.findReviewsByAlbumId(albumId);
        float avgRating = (float) reviews.stream().mapToInt(AlbumReview::getRating).average().orElse(0.0);
        float roundedAvgRating = Math.round(avgRating * 100.0) / 100.0f;
        int ratingAmount = reviews.size();
        albumService.updateRating(albumId, roundedAvgRating, ratingAmount);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasUserReviewedAlbum(long userId, long albumId) {
        return albumService.hasUserReviewed(userId, albumId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SongReview> findSongReviewById(long id) {
        return reviewDao.findSongReviewById(id);
    }


    @Override
    @Transactional
    public SongReview saveSongReview(SongReview review) {
        SongReview result = reviewDao.saveSongReview(review);
        updateUserReviewAmount(review.getUser().getId());
        updateSongRating(review.getSong().getId());
        return result;
    }

    @Transactional
    private void updateUserReviewAmount(long userId) {
        userService.updateUserReviewAmount(userId);
    }

    @Transactional
    private void updateSongRating(long songId) {
        List<SongReview> reviews = reviewDao.findReviewsBySongId(songId);
        float avgRating = (float) reviews.stream().mapToInt(SongReview::getRating).average().orElse(0.0);
        float roundedAvgRating = Math.round(avgRating * 100.0) / 100.0f;
        int ratingAmount = reviews.size();
        songService.updateRating(songId, roundedAvgRating, ratingAmount);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasUserReviewedSong(long userId, long songId) {
        return songService.hasUserReviewed(userId, songId);
    }

    @Override
    @Transactional
    public void createLike(long userId, long reviewId) {
        reviewDao.createLike(userId, reviewId);
        reviewDao.incrementLikes(reviewId);
    }

    @Override
    @Transactional
    public void removeLike(long userId, long reviewId) {
        reviewDao.deleteLike(userId, reviewId);
        reviewDao.decrementLikes(reviewId);
    }

    @Override
    @Transactional(readOnly = true)
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
    @Transactional(readOnly = true)
    public List<ArtistReview> findArtistReviewsPaginated(long artistId, int page, int pageSize, long loggedUserId) {
        List<ArtistReview> reviews = reviewDao.findArtistReviewsPaginated(artistId, page, pageSize);
        setIsLiked(reviews, loggedUserId);
        return reviews;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AlbumReview> findAlbumReviewsPaginated(long albumId, int page, int pageSize, long loggedUserId) {
        List<AlbumReview> reviews = reviewDao.findAlbumReviewsPaginated(albumId, page, pageSize);
        setIsLiked(reviews, loggedUserId);
        return reviews;
    }

    @Override
    @Transactional(readOnly = true)
    public List<SongReview> findSongReviewsPaginated(long songId, int page, int pageSize, long loggedUserId) {
        List<SongReview> reviews = reviewDao.findSongReviewsPaginated(songId, page, pageSize);
        setIsLiked(reviews, loggedUserId);
        return reviews;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isArtistReview(long reviewId) {
        return reviewDao.isArtistReview(reviewId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isAlbumReview(long reviewId) {
        return reviewDao.isAlbumReview(reviewId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isSongReview(long reviewId) {
        return reviewDao.isSongReview(reviewId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Review> findReviewsByUserPaginated(long userId, int page, int pageSize, long loggedUserId) {
        List<Review> list = reviewDao.findReviewsByUserPaginated(userId, page, pageSize);
        setIsLiked(list, loggedUserId);
        return list;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Review> getPopularReviewsPaginated(int page, int pageSize, long loggedUserId) {
        List<Review> list = reviewDao.getPopularReviewsPaginated(page, pageSize);
        setIsLiked(list, loggedUserId);
        return list;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Review> getReviewsFromFollowedUsersPaginated(Long userId, int page, int pageSize, long loggedUserId) {
        List<Review> list = reviewDao.getReviewsFromFollowedUsersPaginated(userId, page, pageSize);
        setIsLiked(list, loggedUserId);
        return list;
    }

    @Override
    @Transactional
    public void block(Long reviewId) {
        reviewDao.block(reviewId);
    }

    @Override
    @Transactional
    public void unblock(Long reviewId) {
        reviewDao.unblock(reviewId);
    }

}