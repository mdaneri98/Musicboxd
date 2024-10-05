package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserService {


    Optional<User> findById(long id);
    List<User> findAll();
    List<User> findByUsernameContaining(String sub);

    int create(String username, String email, String password);

    int createFollowing(User loggedUser, long followingUserId);

    int undoFollowing(User loggedUser, long followingUserId);

    boolean isFollowing(Long userId, Long otherId);
    boolean isAlbumFavorite(Long userId, Long albumId);
    boolean isArtistFavorite(Long userId, Long albumId);
    boolean isSongFavorite(Long userId, Long albumId);
    UserFollowingData getFollowingData(Long userId, int limit, int offset);

    int update(User user);
    boolean changePassword(Long userId, String newPassword);

    int update(User user, byte[] bytes);

    int deleteById(long id);

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String email);

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

}
