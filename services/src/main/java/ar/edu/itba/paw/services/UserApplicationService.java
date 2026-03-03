package ar.edu.itba.paw.services;

import ar.edu.itba.paw.domain.user.User;
import ar.edu.itba.paw.domain.album.Album;
import ar.edu.itba.paw.domain.artist.Artist;
import ar.edu.itba.paw.domain.song.Song;
import ar.edu.itba.paw.usecases.user.*;
import ar.edu.itba.paw.views.AlbumView;
import ar.edu.itba.paw.views.SongView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserApplicationService {

    private final GetUser getUser;
    private final GetAllUsers getAllUsers;
    private final CreateUser createUser;
    private final UpdateUserProfile updateUserProfile;
    private final UpdateUserConfig updateUserConfig;
    private final DeleteUser deleteUser;
    private final GetUserFollowers getUserFollowers;
    private final GetUserFollowing getUserFollowing;
    private final FollowUser followUser;
    private final UnfollowUser unfollowUser;
    private final GetUserFavoriteArtists getUserFavoriteArtists;
    private final GetUserFavoriteAlbums getUserFavoriteAlbums;
    private final GetUserFavoriteSongs getUserFavoriteSongs;
    private final GetUserFavoriteAlbumsView getUserFavoriteAlbumsView;
    private final GetUserFavoriteSongsView getUserFavoriteSongsView;
    private final AddFavoriteArtist addFavoriteArtist;
    private final RemoveFavoriteArtist removeFavoriteArtist;
    private final AddFavoriteAlbum addFavoriteAlbum;
    private final RemoveFavoriteAlbum removeFavoriteAlbum;
    private final AddFavoriteSong addFavoriteSong;
    private final RemoveFavoriteSong removeFavoriteSong;

    @Autowired
    public UserApplicationService(GetUser getUser,
                                   GetAllUsers getAllUsers,
                                   CreateUser createUser,
                                   UpdateUserProfile updateUserProfile,
                                   UpdateUserConfig updateUserConfig,
                                   DeleteUser deleteUser,
                                   GetUserFollowers getUserFollowers,
                                   GetUserFollowing getUserFollowing,
                                   FollowUser followUser,
                                   UnfollowUser unfollowUser,
                                   GetUserFavoriteArtists getUserFavoriteArtists,
                                   GetUserFavoriteAlbums getUserFavoriteAlbums,
                                   GetUserFavoriteSongs getUserFavoriteSongs,
                                   GetUserFavoriteAlbumsView getUserFavoriteAlbumsView,
                                   GetUserFavoriteSongsView getUserFavoriteSongsView,
                                   AddFavoriteArtist addFavoriteArtist,
                                   RemoveFavoriteArtist removeFavoriteArtist,
                                   AddFavoriteAlbum addFavoriteAlbum,
                                   RemoveFavoriteAlbum removeFavoriteAlbum,
                                   AddFavoriteSong addFavoriteSong,
                                   RemoveFavoriteSong removeFavoriteSong) {
        this.getUser = getUser;
        this.getAllUsers = getAllUsers;
        this.createUser = createUser;
        this.updateUserProfile = updateUserProfile;
        this.updateUserConfig = updateUserConfig;
        this.deleteUser = deleteUser;
        this.getUserFollowers = getUserFollowers;
        this.getUserFollowing = getUserFollowing;
        this.followUser = followUser;
        this.unfollowUser = unfollowUser;
        this.getUserFavoriteArtists = getUserFavoriteArtists;
        this.getUserFavoriteAlbums = getUserFavoriteAlbums;
        this.getUserFavoriteSongs = getUserFavoriteSongs;
        this.getUserFavoriteAlbumsView = getUserFavoriteAlbumsView;
        this.getUserFavoriteSongsView = getUserFavoriteSongsView;
        this.addFavoriteArtist = addFavoriteArtist;
        this.removeFavoriteArtist = removeFavoriteArtist;
        this.addFavoriteAlbum = addFavoriteAlbum;
        this.removeFavoriteAlbum = removeFavoriteAlbum;
        this.addFavoriteSong = addFavoriteSong;
        this.removeFavoriteSong = removeFavoriteSong;
    }

    public User getUser(Long userId) {
        return getUser.execute(userId);
    }

    public List<User> getAllUsers(String search, Integer page, Integer size) {
        return getAllUsers.execute(search, page, size);
    }

    public Long countAllUsers() {
        return getAllUsers.count();
    }

    public User createUser(CreateUserCommand command) {
        return createUser.execute(command);
    }

    public User updateUserProfile(UpdateUserProfileCommand command) {
        return updateUserProfile.execute(command);
    }

    public User updateUserConfig(UpdateUserConfigCommand command) {
        return updateUserConfig.execute(command);
    }

    public void deleteUser(DeleteUserCommand command) {
        deleteUser.execute(command);
    }

    public List<User> getUserFollowers(Long userId, Integer page, Integer size) {
        return getUserFollowers.execute(userId, page, size);
    }

    public Long countUserFollowers(Long userId) {
        return getUserFollowers.count(userId);
    }

    public List<User> getUserFollowing(Long userId, Integer page, Integer size) {
        return getUserFollowing.execute(userId, page, size);
    }

    public Long countUserFollowing(Long userId) {
        return getUserFollowing.count(userId);
    }

    public void followUser(Long userId, Long targetUserId) {
        followUser.execute(userId, targetUserId);
    }

    public void unfollowUser(Long userId, Long targetUserId) {
        unfollowUser.execute(userId, targetUserId);
    }

    public List<Artist> getUserFavoriteArtists(Long userId, Integer page, Integer size) {
        return getUserFavoriteArtists.execute(userId, page, size);
    }

    public List<Album> getUserFavoriteAlbums(Long userId, Integer page, Integer size) {
        return getUserFavoriteAlbums.execute(userId, page, size);
    }

    public List<Song> getUserFavoriteSongs(Long userId, Integer page, Integer size) {
        return getUserFavoriteSongs.execute(userId, page, size);
    }

    public List<AlbumView> getUserFavoriteAlbumsView(Long userId, Integer page, Integer size) {
        return getUserFavoriteAlbumsView.execute(userId, page, size);
    }

    public List<SongView> getUserFavoriteSongsView(Long userId, Integer page, Integer size) {
        return getUserFavoriteSongsView.execute(userId, page, size);
    }

    public void addFavoriteArtist(Long userId, Long artistId) {
        addFavoriteArtist.execute(userId, artistId);
    }

    public void removeFavoriteArtist(Long userId, Long artistId) {
        removeFavoriteArtist.execute(userId, artistId);
    }

    public void addFavoriteAlbum(Long userId, Long albumId) {
        addFavoriteAlbum.execute(userId, albumId);
    }

    public void removeFavoriteAlbum(Long userId, Long albumId) {
        removeFavoriteAlbum.execute(userId, albumId);
    }

    public void addFavoriteSong(Long userId, Long songId) {
        addFavoriteSong.execute(userId, songId);
    }

    public void removeFavoriteSong(Long userId, Long songId) {
        removeFavoriteSong.execute(userId, songId);
    }
}
