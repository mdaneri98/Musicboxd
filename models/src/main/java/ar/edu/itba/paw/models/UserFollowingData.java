package ar.edu.itba.paw.models;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "cuser")
public class UserFollowingData {
    @Id
    @Column(name = "id")
    private Long id;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "follower",
        joinColumns = @JoinColumn(name = "following"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> followers;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "follower",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "following")
    )
    private List<User> following;

    public UserFollowingData() {
        // Constructor vacío necesario para JPA
    }

    public UserFollowingData(Long id, List<User> followers, List<User> following) {
        this.id = id;
        this.followers = followers;
        this.following = following;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<User> getFollowers() {
        return followers;
    }

    public void setFollowers(List<User> followers) {
        this.followers = followers;
    }

    public List<User> getFollowing() {
        return following;
    }

    public void setFollowing(List<User> following) {
        this.following = following;
    }
}