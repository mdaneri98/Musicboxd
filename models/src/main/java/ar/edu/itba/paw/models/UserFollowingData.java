package ar.edu.itba.paw.models;

import java.util.List;

public class UserFollowingData {
    private final List<Long> followers;
    private final List<Long> following;

    public UserFollowingData(List<Long> followers, List<Long> following) {
        this.followers = followers;
        this.following = following;
    }

    public List<Long> getFollowers() {
        return followers;
    }

    public List<Long> getFollowing() {
        return following;
    }
}