package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.*;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Primary
@Repository
public class UserJpaDao implements UserDao {

    @PersistenceContext
    private EntityManager em;

    //============================ C.R.U.D ============================
    @Override
    public Optional<User> find(long id) {
        return Optional.ofNullable(em.find(User.class, id));
    }

    @Override
    public List<User> findAll(int pageNumber, int pageSize) {
        Query nativeQuery = em.createNativeQuery("SELECT id FROM cuser");
        nativeQuery.setMaxResults(pageSize);
        nativeQuery.setFirstResult((pageNumber - 1) * pageSize);

        final List<Long> idList = (List<Long>) nativeQuery.getResultList()
                .stream().map(n -> (Long)((Number)n).longValue()).collect(Collectors.toList());

        // Sino el siguiente query falla, no te deja hacer IN de una lista vacía.
        if (idList.isEmpty())
            return Collections.emptyList();

        final TypedQuery<User> query = em.createQuery("FROM User WHERE id IN :ids", User.class);
        query.setParameter("ids", idList);

        return query.getResultList();
    }


    @Override
    public Optional<User> update(User user) {
        if (em.find(User.class, user.getId()) == null) {
            return Optional.empty();
        }
        return Optional.of(em.merge(user));
    }

    @Override
    public int deleteById(long id) {
        User user = em.find(User.class, id);
        if (user == null) {
            return 0;
        }
        em.remove(user);
        return 1;
    }

    //============================ QUERIES ============================
    @Override
    public Optional<User> findByEmail(String email) {
        TypedQuery<User> query = em.createQuery("FROM User WHERE email = :email", User.class);
        query.setParameter("email", email);

        List<User> result = query.getResultList();
        if (result.isEmpty())
            return Optional.empty();

        return Optional.of(result.getFirst());
    }

    @Override
    public Optional<User> findByUsername(String username) {
        TypedQuery<User> query = em.createQuery("FROM User WHERE username = :username", User.class);
        query.setParameter("username", username);

        List<User> result = query.getResultList();
        if (result.isEmpty())
            return Optional.empty();

        return Optional.of(result.getFirst());
    }

    @Override
    public List<User> findByUsernameContaining(String sub, int pageNumber, int pageSize) {
        String jpql = "FROM User u WHERE LOWER(u.username) LIKE LOWER(:substring)";
        TypedQuery<User> query = em.createQuery(jpql, User.class)
                .setParameter("substring", "%" + sub + "%")
                .setMaxResults(pageSize)
                .setFirstResult((pageNumber - 1) * pageSize);
        return query.getResultList();
    }

    @Override
    public Optional<User> create(String username, String email, String password, Image image) {
        final User user = new User(username, password, email);
        user.setImage(image);
        user.setPreferredLanguage("es");
        em.persist(user);
        return Optional.of(user);
    }

    //============================ FOLLOWERS & FOLLOWINGS ============================
    @Override
    public int createFollowing(User loggedUser, User following) {
        User user = em.find(User.class, loggedUser.getId());
        User userToFollow = em.find(User.class, following.getId());

        if (user != null && userToFollow != null) {
            if (!user.getFollowing().contains(userToFollow)) {
                user.getFollowing().add(userToFollow);
                user.setFollowingAmount(countFollowing(user.getId()));
                userToFollow.setFollowersAmount(countFollowers(userToFollow.getId()));
                return 1;
            }
        }
        return 0;
    }

    @Override
    public int countFollowers(Long userId) {
        Query query = em.createQuery("SELECT COUNT(u) FROM User u JOIN u.following f WHERE f.id = :userId");
        query.setParameter("userId", userId);
        return ((Long) query.getSingleResult()).intValue();
    }

    @Override
    public int countFollowing(Long userId) {
        Query query = em.createQuery("SELECT COUNT(f) FROM User u JOIN u.following f WHERE u.id = :userId");
        query.setParameter("userId", userId);
        return ((Long) query.getSingleResult()).intValue();
    }

    @Override
    public int undoFollowing(User loggedUser, User following) {
        User user = em.find(User.class, loggedUser.getId());
        User userToFollow = em.find(User.class, following.getId());

        if (user != null && userToFollow != null) {
            if (user.getFollowing().contains(userToFollow)) {
                if (user.getFollowing().remove(userToFollow)) {
                    user.setFollowingAmount(countFollowing(user.getId()));
                    userToFollow.setFollowersAmount(countFollowers(userToFollow.getId()));
                    return 1;
                }
            }
        }
        return 0;
    }

    @Override
    public boolean isFollowing(Long userId, Long otherId) {
        User user = em.find(User.class, userId);
        User userToFollow = em.find(User.class, otherId);

        if (user == null || userToFollow == null)
            return false;

        return user.getFollowing().contains(userToFollow);
    }

    @Override
    public List<User> getFollowers(Long userId, int pageNumber, int pageSize) {
        Query nativeQuery = em.createNativeQuery("SELECT user_id FROM follower WHERE following = :userId");
        nativeQuery.setParameter("userId", userId);
        nativeQuery.setMaxResults(pageSize);
        nativeQuery.setFirstResult((pageNumber - 1) * pageSize);

        final List<Long> idList = (List<Long>) nativeQuery.getResultList()
                .stream().map(n -> (Long)((Number)n).longValue()).collect(Collectors.toList());

        // Sino el siguiente query falla, no te deja hacer IN de una lista vacía.
        if (idList.isEmpty())
            return Collections.emptyList();

        final TypedQuery<User> query = em.createQuery("FROM User WHERE id IN :ids", User.class);
        query.setParameter("ids", idList);

        return query.getResultList();
    }

    @Override
    public List<User> getFollowings(Long userId, int pageNumber, int pageSize) {
        Query nativeQuery = em.createNativeQuery("SELECT following FROM follower WHERE user_id = :userId");
        nativeQuery.setParameter("userId", userId);
        nativeQuery.setMaxResults(pageSize);
        nativeQuery.setFirstResult((pageNumber - 1) * pageSize);

        final List<Long> idList = (List<Long>) nativeQuery.getResultList()
                .stream().map(n -> (Long)((Number)n).longValue()).collect(Collectors.toList());

        // Sino el siguiente query falla, no te deja hacer IN de una lista vacía.
        if (idList.isEmpty())
            return Collections.emptyList();

        final TypedQuery<User> query = em.createQuery("FROM User WHERE id IN :ids", User.class);
        query.setParameter("ids", idList);

        return query.getResultList();
    }

    //============================ FAVORITE ARTISTS ============================
    @Override
    public List<Artist> getFavoriteArtists(long userId) {
        User user = em.find(User.class, userId);
        if (user == null)
            return Collections.emptyList();
        return user.getFavoriteArtists();
    }

    @Override
    public boolean addFavoriteArtist(long userId, long artistId) {
        User user = em.find(User.class, userId);
        Artist artist = em.find(Artist.class, artistId);
        if (user == null || artist == null)
            return false;
        return user.getFavoriteArtists().add(artist);
    }

    @Override
    public boolean removeFavoriteArtist(long userId, long artistId) {
        User user = em.find(User.class, userId);
        Artist artist = em.find(Artist.class, artistId);
        if (user == null || artist == null)
            return false;
        return user.getFavoriteArtists().remove(artist);
    }

    @Override
    public int getFavoriteArtistsCount(long userId) {
        User user = em.find(User.class, userId);
        if (user == null)
            return 0;
        // Consideramos un maximo de 5 artistas.
        return user.getFavoriteArtists().size();
    }

    @Override
    public boolean isArtistFavorite(Long userId, Long artistId) {
        User user = em.find(User.class, userId);
        Artist artist = em.find(Artist.class, artistId);
        if (user == null || artist == null)
            return false;
        // Consideramos un maximo de 5 artistas.
        return user.getFavoriteArtists().contains(artist);
    }

    //============================ FAVORITE ALBUMS ============================
    @Override
    public List<Album> getFavoriteAlbums(long userId) {
        User user = em.find(User.class, userId);
        if (user == null)
            return Collections.emptyList();
        return user.getFavoriteAlbums();
    }

    @Override
    public boolean addFavoriteAlbum(long userId, long albumId) {
        User user = em.find(User.class, userId);
        Album album = em.find(Album.class, albumId);
        if (user == null || album == null)
            return false;
        return user.getFavoriteAlbums().add(album);
    }

    @Override
    public boolean removeFavoriteAlbum(long userId, long albumId) {
        User user = em.find(User.class, userId);
        Album album = em.find(Album.class, albumId);
        if (user == null || album == null)
            return false;
        return user.getFavoriteAlbums().remove(album);
    }

    @Override
    public int getFavoriteAlbumsCount(long userId) {
        User user = em.find(User.class, userId);
        if (user == null)
            return 0;
        // Consideramos un maximo de 5 artistas.
        return user.getFavoriteAlbums().size();
    }

    @Override
    public boolean isAlbumFavorite(Long userId, Long albumId) {
        User user = em.find(User.class, userId);
        Album album = em.find(Album.class, albumId);
        if (user == null || album == null)
            return false;
        // Consideramos un maximo de 5 artistas.
        return user.getFavoriteAlbums().contains(album);
    }

    //============================ FAVORITE SONGS ============================
    @Override
    public List<Song> getFavoriteSongs(long userId) {
        User user = em.find(User.class, userId);
        if (user == null)
            return Collections.emptyList();
        return user.getFavoriteSongs();
    }

    @Override
    public boolean addFavoriteSong(long userId, long songId) {
        User user = em.find(User.class, userId);
        Song Song = em.find(Song.class, songId);
        if (user == null || Song == null)
            return false;
        return user.getFavoriteSongs().add(Song);
    }

    @Override
    public boolean removeFavoriteSong(long userId, long songId) {
        User user = em.find(User.class, userId);
        Song song = em.find(Song.class, songId);
        if (user == null || song == null)
            return false;
        return user.getFavoriteSongs().remove(song);
    }

    @Override
    public int getFavoriteSongsCount(long userId) {
        User user = em.find(User.class, userId);
        if (user == null)
            return 0;
        // Consideramos un maximo de 5 artistas.
        return user.getFavoriteSongs().size();
    }

    @Override
    public boolean isSongFavorite(Long userId, Long songId) {
        User user = em.find(User.class, userId);
        Song song = em.find(Song.class, songId);
        if (user == null || song == null)
            return false;
        // Consideramos un maximo de 5 artistas.
        return user.getFavoriteSongs().contains(song);
    }

    //============================ Reviews ============================
    @Override
    public void updateUserReviewAmount(Long userId) {
        Query countQuery = em.createQuery(
                "SELECT COUNT(r) FROM Review r WHERE r.user.id = :userId AND r.isBlocked = false"
        );
        countQuery.setParameter("userId", userId);
        Integer reviewCount = ((Number) countQuery.getSingleResult()).intValue();

        em.createQuery("UPDATE User u SET u.reviewAmount = :reviewCount WHERE u.id = :userId")
                .setParameter("userId", userId)
                .setParameter("reviewCount", reviewCount)
                .executeUpdate();
    }

}
