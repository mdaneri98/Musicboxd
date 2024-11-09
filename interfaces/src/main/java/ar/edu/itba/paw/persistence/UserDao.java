package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.*;

import java.util.List;
import java.util.Optional;

public interface UserDao {

    Optional<User> find(long id);
    List<User> findAll(int pageNumber, int pageSize);
    List<User> findByUsernameContaining(String sub, int pageNumber, int pageSize);

    Optional<User> create(String username, String email, String password, Image image);

    int createFollowing(User loggedUser, User following);

    int undoFollowing(User loggedUser, User following);

    boolean isFollowing(Long userId, Long otherId);

    List<User> getFollowers(Long userId, int pageNumber, int pageSize);
    List<User> getFollowings(Long userId, int pageNumber, int pageSize);

    Optional<User> update(User user);

    int deleteById(long id);

    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String email);

    // Artistas favoritos
    List<Artist> getFavoriteArtists(long userId);
    boolean addFavoriteArtist(long userId, long artistId);
    boolean removeFavoriteArtist(long userId, long artistId);
    int getFavoriteArtistsCount(long userId);
    boolean isArtistFavorite(Long userId, Long artistId);

    // Álbumes favoritos
    List<Album> getFavoriteAlbums(long userId);
    boolean addFavoriteAlbum(long userId, long albumId);
    boolean removeFavoriteAlbum(long userId, long albumId);
    int getFavoriteAlbumsCount(long userId);
    boolean isAlbumFavorite(Long userId, Long albumId);

    // Canciones favoritas
    List<Song> getFavoriteSongs(long userId);
    boolean addFavoriteSong(long userId, long songId);
    boolean removeFavoriteSong(long userId, long songId);
    int getFavoriteSongsCount(long userId);
    boolean isSongFavorite(Long userId, Long songId);

    void updateUserReviewAmount(Long userId);

    int countFollowing(Long userId);
    int countFollowers(Long userId);
    
    List<User> getRecommendedUsers(Long userId, int pageNumber, int pageSize);

}
