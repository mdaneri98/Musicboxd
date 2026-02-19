package ar.edu.itba.paw.webapp.dto.links;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.net.URI;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserLinksDTO {

    private URI self;
    private URI image;
    private URI reviews;
    private URI followers;
    private URI following;
    private URI favoriteArtists;
    private URI favoriteAlbums;
    private URI favoriteSongs;
    private URI likedReviews;

    public UserLinksDTO() {
    }

    public URI getSelf() {
        return self;
    }

    public void setSelf(URI self) {
        this.self = self;
    }

    public URI getImage() {
        return image;
    }

    public void setImage(URI image) {
        this.image = image;
    }

    public URI getReviews() {
        return reviews;
    }

    public void setReviews(URI reviews) {
        this.reviews = reviews;
    }

    public URI getFollowers() {
        return followers;
    }

    public void setFollowers(URI followers) {
        this.followers = followers;
    }

    public URI getFollowing() {
        return following;
    }

    public void setFollowing(URI following) {
        this.following = following;
    }

    public URI getFavoriteArtists() {
        return favoriteArtists;
    }

    public void setFavoriteArtists(URI favoriteArtists) {
        this.favoriteArtists = favoriteArtists;
    }

    public URI getFavoriteAlbums() {
        return favoriteAlbums;
    }

    public void setFavoriteAlbums(URI favoriteAlbums) {
        this.favoriteAlbums = favoriteAlbums;
    }

    public URI getFavoriteSongs() {
        return favoriteSongs;
    }

    public void setFavoriteSongs(URI favoriteSongs) {
        this.favoriteSongs = favoriteSongs;
    }

    public URI getLikedReviews() {
        return likedReviews;
    }

    public void setLikedReviews(URI likedReviews) {
        this.likedReviews = likedReviews;
    }
}
