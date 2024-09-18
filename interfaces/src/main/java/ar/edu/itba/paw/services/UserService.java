package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.Album;
import ar.edu.itba.paw.models.Artist;
import ar.edu.itba.paw.models.Song;
import ar.edu.itba.paw.models.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserService {


    Optional<User> findById(long id);
    List<User> findAll();

    int create(String username, String email, String password);

    int createFollowing(User loggedUser, long followingUserId);

    int undoFollowing(User loggedUser, long followingUserId);

    boolean isFollowing(Long userId, Long otherId);
    boolean isAlbumFavorite(Long userId, Long albumId);
    boolean isArtistFavorite(Long userId, Long albumId);
    boolean isSongFavorite(Long userId, Long albumId);


    int update(Long user, String username, String email, String password, String name, String bio, LocalDateTime updated_at, boolean verified, boolean moderator, Long imgId, Integer followers_amount, Integer following_amount, Integer review_amount);

    int deleteById(long id);

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String email);

    int incrementReviewAmount(User user);
    int decrementReviewAmount(User user);
    void createVerification(User user);
    boolean verify(String code);

    // Artistas favoritos
    List<Artist> getFavoriteArtists(long userId);
    boolean addFavoriteArtist(long userId, long artistId);
    boolean removeFavoriteArtist(long userId, long artistId);
    int getFavoriteArtistsCount(long userId);

    // Álbumes favoritos
    List<Album> getFavoriteAlbums(long userId);
    boolean addFavoriteAlbum(long userId, long albumId);
    boolean removeFavoriteAlbum(long userId, long albumId);
    int getFavoriteAlbumsCount(long userId);

    // Canciones favoritas
    List<Song> getFavoriteSongs(long userId);
    boolean addFavoriteSong(long userId, long songId);
    boolean removeFavoriteSong(long userId, long songId);
    int getFavoriteSongsCount(long userId);

}
