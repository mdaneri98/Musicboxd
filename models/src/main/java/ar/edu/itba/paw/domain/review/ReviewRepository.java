package ar.edu.itba.paw.domain.review;

import ar.edu.itba.paw.domain.album.AlbumId;
import ar.edu.itba.paw.domain.artist.ArtistId;
import ar.edu.itba.paw.domain.song.SongId;
import ar.edu.itba.paw.domain.user.UserId;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository {

    Optional<Review> findById(ReviewId id);

    Review save(Review review);

    void delete(ReviewId id);

    List<Review> findAll(Integer page, Integer size);

    Long countAll();

    List<ArtistReview> findByArtistId(ArtistId artistId, Integer page, Integer size);

    List<AlbumReview> findByAlbumId(AlbumId albumId, Integer page, Integer size);

    List<SongReview> findBySongId(SongId songId, Integer page, Integer size);

    List<Review> findByUserId(UserId userId, Integer page, Integer size);

    Optional<ArtistReview> findArtistReviewByUserAndArtist(UserId userId, ArtistId artistId);

    Optional<AlbumReview> findAlbumReviewByUserAndAlbum(UserId userId, AlbumId albumId);

    Optional<SongReview> findSongReviewByUserAndSong(UserId userId, SongId songId);

    Long countByArtistId(ArtistId artistId);

    Long countByAlbumId(AlbumId albumId);

    Long countBySongId(SongId songId);

    Long countByUserId(UserId userId);

    void addLike(ReviewId reviewId, UserId userId);

    void removeLike(ReviewId reviewId, UserId userId);

    boolean isLikedByUser(ReviewId reviewId, UserId userId);

    List<Review> findLikedByUser(UserId userId, Integer page, Integer size);

    Long countLikedByUser(UserId userId);
}
