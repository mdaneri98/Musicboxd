package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.Album;
import ar.edu.itba.paw.models.Artist;
import ar.edu.itba.paw.models.Song;
import ar.edu.itba.paw.models.User;

import java.util.List;
import java.util.Optional;

public class UserHibernateDao implements UserDao {

    @Override
    public Optional<User> find(long id) {
        return Optional.empty();
    }

    @Override
    public List<User> findAll() {
        return List.of();
    }

    @Override
    public List<User> findByUsernameContaining(String sub) {
        return List.of();
    }

    @Override
    public Optional<User> create(String username, String email, String password, long imgId) {
        return Optional.empty();
    }

    @Override
    public int createFollowing(User loggedUser, User following) {
        return 0;
    }

    @Override
    public int undoFollowing(User loggedUser, User following) {
        return 0;
    }

    @Override
    public boolean isFollowing(Long userId, Long otherId) {
        return false;
    }

    @Override
    public List<User> getFollowers(Long userId, int limit, int offset) {
        return List.of();
    }

    @Override
    public List<User> getFollowing(Long userId, int limit, int offset) {
        return List.of();
    }

    @Override
    public int update(User user) {
        return 0;
    }

    @Override
    public boolean changePassword(Long userId, String newPassword) {
        return false;
    }

    @Override
    public int deleteById(long id) {
        return 0;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return Optional.empty();
    }

    @Override
    public Optional<User> findByUsername(String email) {
        return Optional.empty();
    }

    @Override
    public List<Artist> getFavoriteArtists(long userId) {
        return List.of();
    }

    @Override
    public boolean addFavoriteArtist(long userId, long artistId) {
        return false;
    }

    @Override
    public boolean removeFavoriteArtist(long userId, long artistId) {
        return false;
    }

    @Override
    public int getFavoriteArtistsCount(long userId) {
        return 0;
    }

    @Override
    public boolean isArtistFavorite(Long userId, Long artistId) {
        return false;
    }

    @Override
    public List<Album> getFavoriteAlbums(long userId) {
        return List.of();
    }

    @Override
    public boolean addFavoriteAlbum(long userId, long albumId) {
        return false;
    }

    @Override
    public boolean removeFavoriteAlbum(long userId, long albumId) {
        return false;
    }

    @Override
    public int getFavoriteAlbumsCount(long userId) {
        return 0;
    }

    @Override
    public boolean isAlbumFavorite(Long userId, Long albumId) {
        return false;
    }

    @Override
    public List<Song> getFavoriteSongs(long userId) {
        return List.of();
    }

    @Override
    public boolean addFavoriteSong(long userId, long songId) {
        return false;
    }

    @Override
    public boolean removeFavoriteSong(long userId, long songId) {
        return false;
    }

    @Override
    public int getFavoriteSongsCount(long userId) {
        return 0;
    }

    @Override
    public boolean isSongFavorite(Long userId, Long songId) {
        return false;
    }

    @Override
    public void updateUserReviewAmount(Long userId) {

    }
}
