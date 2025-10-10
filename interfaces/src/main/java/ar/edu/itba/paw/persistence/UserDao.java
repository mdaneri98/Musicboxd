package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.*;

import java.util.List;
import java.util.Optional;

public interface UserDao {

    Optional<User> findById(Long id);
    Long countUsers();
    List<User> findAll();
    List<User> findPaginated(FilterType filterType, Integer pageNumber, Integer pageSize);
    List<User> findByUsernameContaining(String sub, Integer pageNumber, Integer pageSize);

    Optional<User> create(String username, String email, String password);

    Integer createFollowing(User loggedUser, User following);

    Integer undoFollowing(User loggedUser, User following);

    Boolean isFollowing(Long userId, Long otherId);

    List<User> getFollowers(Long userId, Integer pageNumber, Integer pageSize);
    List<User> getFollowings(Long userId, Integer pageNumber, Integer pageSize);

    Optional<User> updateUser(Long userId, User user);

    Integer deleteById(Long id);

    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String email);

    // Artistas favoritos
    List<Artist> getFavoriteArtists(Long userId);
    Boolean addFavoriteArtist(Long userId, Long artistId);
    Boolean removeFavoriteArtist(Long userId, Long artistId);
    Integer getFavoriteArtistsCount(Long userId);
    Boolean isArtistFavorite(Long userId, Long artistId);

    // Álbumes favoritos
    List<Album> getFavoriteAlbums(Long userId);
    Boolean addFavoriteAlbum(Long userId, Long albumId);
    Boolean removeFavoriteAlbum(Long userId, Long albumId);
    Integer getFavoriteAlbumsCount(Long userId);
    Boolean isAlbumFavorite(Long userId, Long albumId);

    // Canciones favoritas
    List<Song> getFavoriteSongs(Long userId);
    Boolean addFavoriteSong(Long userId, Long songId);
    Boolean removeFavoriteSong(Long userId, Long songId);
    Integer getFavoriteSongsCount(Long userId);
    Boolean isSongFavorite(Long userId, Long songId);

    void updateUserReviewAmount(Long userId);

    Integer countFollowing(Long userId);
    Integer countFollowers(Long userId);
    
    List<User> getRecommendedUsers(Long userId, Integer pageNumber, Integer pageSize);

}
