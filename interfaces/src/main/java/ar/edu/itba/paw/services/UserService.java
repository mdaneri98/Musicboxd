package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.models.dtos.AlbumDTO;
import ar.edu.itba.paw.models.dtos.ArtistDTO;
import ar.edu.itba.paw.models.dtos.CreateUserDTO;
import ar.edu.itba.paw.models.dtos.SongDTO;
import ar.edu.itba.paw.models.dtos.UserDTO;
import java.util.List;

public interface UserService{

    UserDTO findUserById(Long id);
    Long countUsers();
    List<UserDTO> findAll();
    List<UserDTO> findPaginated(FilterType filterType, Integer pageNumber, Integer pageSize);
    List<UserDTO> findByUsernameContaining(String sub, Integer pageNumber, Integer pageSize);

    UserDTO create(CreateUserDTO createUserDTO);

    Integer createFollowing(Long loggedUser, Long followingUserId);
    Integer undoFollowing(Long loggedUser, Long followingUserId);
    List<UserDTO> getFollowers(Long userId, Integer pageNumber, Integer pageSize);
    List<UserDTO> getFollowings(Long userId, Integer pageNumber, Integer pageSize);

    Boolean isFollowing(Long userId, Long otherId);
    Boolean isAlbumFavorite(Long userId, Long albumId);
    Boolean isArtistFavorite(Long userId, Long albumId);
    Boolean isSongFavorite(Long userId, Long albumId);

    UserDTO updateUser(UserDTO user);
    Boolean changePassword(Long userId, String newPassword);

    Integer deleteById(Long id);

    UserDTO findByEmail(String email);

    UserDTO findByUsername(String email);
    Boolean usernameExists(String username);
    Boolean emailExists(String email);

    Void updateUserReviewAmount(Long userId);

    Void createVerification(VerificationType type, User user);
    Long verify(VerificationType type, String code);

    // Artistas favoritos
    List<ArtistDTO> getFavoriteArtists(Long userId);
    Boolean addFavoriteArtist(Long userId, Long artistId);
    Boolean removeFavoriteArtist(Long userId, Long artistId);
    Integer getFavoriteArtistsCount(Long userId);

    // Álbumes favoritos
    List<AlbumDTO> getFavoriteAlbums(Long userId);
    Boolean addFavoriteAlbum(Long userId, Long albumId);
    Boolean removeFavoriteAlbum(Long userId, Long albumId);
    Integer getFavoriteAlbumsCount(Long userId);

    // Canciones favoritas
    List<SongDTO> getFavoriteSongs(Long userId);
    Boolean addFavoriteSong(Long userId, Long songId);
    Boolean removeFavoriteSong(Long userId, Long songId);
    Integer getFavoriteSongsCount(Long userId);
    List<UserDTO> getRecommendedUsers(Long userId, Integer pageNumber, Integer pageSize);
}
