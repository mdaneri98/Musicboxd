package ar.edu.itba.paw.models;

import java.util.List;

public class UserFollowingData {
    private final List<User> followers;
    private final List<User> following;

    public UserFollowingData(List<User> followers, List<User> following) {
        this.followers = followers;
        this.following = following;
    }

    public List<User> getFollowers() {
        return followers;
    }

    public List<User> getFollowing() {
        return following;
    }
}