package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.models.dtos.CreateUserDTO;
import ar.edu.itba.paw.models.dtos.UserDTO;
import java.util.List;

public interface UserService{

    UserDTO findUserById(Long id);
    Long countUsers();
    List<UserDTO> findAll();
    List<UserDTO> findPaginated(FilterType filterType, int pageNumber, int pageSize);
    List<UserDTO> findByUsernameContaining(String sub, int pageNumber, int pageSize);

    UserDTO create(CreateUserDTO createUserDTO);

    int createFollowing(Long loggedUser, Long followingUserId);
    int undoFollowing(Long loggedUser, Long followingUserId);
    List<UserDTO> getFollowers(Long userId, Integer pageNumber, Integer pageSize);
    List<UserDTO> getFollowings(Long userId, Integer pageNumber, Integer pageSize);

    boolean isFollowing(Long userId, Long otherId);
    boolean isAlbumFavorite(Long userId, Long albumId);
    boolean isArtistFavorite(Long userId, Long albumId);
    boolean isSongFavorite(Long userId, Long albumId);

    UserDTO updateUser(Long userId, UserDTO user);
    boolean changePassword(Long userId, String newPassword);

    int deleteById(long id);

    UserDTO findByEmail(String email);

    UserDTO findByUsername(String email);
    boolean usernameExists(String username);

    void updateUserReviewAmount(Long userId);

    void createVerification(VerificationType type, User user);
    Long verify(VerificationType type, String code);

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
    List<UserDTO> getRecommendedUsers(Long userId, int pageNumber, int pageSize);

}
