package ar.edu.itba.paw.domain.user;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    Optional<User> findById(UserId id);

    Optional<User> findByEmail(Email email);

    Optional<User> findByUsername(String username);

    User save(User user);

    void delete(UserId id);

    List<User> findAll(Integer page, Integer size, String orderBy, String order);

    List<User> findByUsernameContaining(String search, Integer page, Integer size);

    Long countAll();

    void addFollower(UserId userId, UserId followerId);

    void removeFollower(UserId userId, UserId followerId);

    boolean isFollowing(UserId userId, UserId followedId);

    List<UserId> getFollowerIds(UserId userId, Integer page, Integer size);

    List<UserId> getFollowingIds(UserId userId, Integer page, Integer size);

    Long countFollowers(UserId userId);

    Long countFollowing(UserId userId);

    void addFavoriteAlbum(UserId userId, Long albumId);

    void removeFavoriteAlbum(UserId userId, Long albumId);

    boolean isAlbumFavorite(UserId userId, Long albumId);

    List<Long> getFavoriteAlbumIds(UserId userId, Integer page, Integer size);

    void addFavoriteArtist(UserId userId, Long artistId);

    void removeFavoriteArtist(UserId userId, Long artistId);

    boolean isArtistFavorite(UserId userId, Long artistId);

    List<Long> getFavoriteArtistIds(UserId userId, Integer page, Integer size);

    void addFavoriteSong(UserId userId, Long songId);

    void removeFavoriteSong(UserId userId, Long songId);

    boolean isSongFavorite(UserId userId, Long songId);

    List<Long> getFavoriteSongIds(UserId userId, Integer page, Integer size);
}
