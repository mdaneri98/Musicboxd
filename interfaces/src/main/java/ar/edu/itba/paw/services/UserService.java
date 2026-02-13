package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.*;
import java.util.List;

public interface UserService {

    User findUserById(Long id);
    Long countUsers();
    List<User> findAll();
    List<User> findPaginated(FilterType filterType, Integer pageNumber, Integer pageSize);
    List<User> findByUsernameContaining(String sub, Integer pageNumber, Integer pageSize);

    User create(String username, String email, String password);

    Integer createFollowing(Long loggedUser, Long followingUserId);
    Integer undoFollowing(Long loggedUser, Long followingUserId);
    List<User> getFollowers(Long userId, Integer pageNumber, Integer pageSize);
    List<User> getFollowings(Long userId, Integer pageNumber, Integer pageSize);

    Boolean isFollowing(Long userId, Long otherId);
    Boolean isAlbumFavorite(Long userId, Long albumId);
    Boolean isArtistFavorite(Long userId, Long albumId);
    Boolean isSongFavorite(Long userId, Long albumId);

    User updateUser(User user);
    Boolean changePassword(Long userId, String newPassword);
    Boolean changePassword(Long userId, String newPassword, String token);

    Integer deleteById(Long id);

    User findByEmail(String email);

    User findByUsername(String email);
    Boolean usernameExists(String username);
    Boolean emailExists(String email);

    Void updateUserReviewAmount(Long userId);

    Void createVerification(VerificationType type, User user);
    Long verify(VerificationType type, String code);

    // Favorite artists
    List<Artist> getFavoriteArtists(Long userId);
    Boolean addFavoriteArtist(Long userId, Long artistId);
    Boolean removeFavoriteArtist(Long userId, Long artistId);
    Integer getFavoriteArtistsCount(Long userId);

    // Favorite albums
    List<Album> getFavoriteAlbums(Long userId);
    Boolean addFavoriteAlbum(Long userId, Long albumId);
    Boolean removeFavoriteAlbum(Long userId, Long albumId);
    Integer getFavoriteAlbumsCount(Long userId);

    // Favorite songs
    List<Song> getFavoriteSongs(Long userId);
    Boolean addFavoriteSong(Long userId, Long songId);
    Boolean removeFavoriteSong(Long userId, Long songId);
    Integer getFavoriteSongsCount(Long userId);
    
    List<User> getRecommendedUsers(Long userId, Integer pageNumber, Integer pageSize);


}
