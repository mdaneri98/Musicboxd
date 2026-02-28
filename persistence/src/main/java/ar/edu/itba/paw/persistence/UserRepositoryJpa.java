package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.domain.user.Email;
import ar.edu.itba.paw.domain.user.User;
import ar.edu.itba.paw.domain.user.UserId;
import ar.edu.itba.paw.domain.user.UserRepository;
import ar.edu.itba.paw.infrastructure.jpa.UserJpaEntity;
import ar.edu.itba.paw.persistence.mappers.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class UserRepositoryJpa implements UserRepository {

    @PersistenceContext
    private EntityManager em;

    private final UserMapper mapper;

    @Autowired
    public UserRepositoryJpa(UserMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public Optional<User> findById(UserId id) {
        UserJpaEntity entity = em.find(UserJpaEntity.class, id.getValue());
        return Optional.ofNullable(entity).map(mapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(Email email) {
        String jpql = "SELECT u FROM UserJpaEntity u WHERE u.email = :email";
        List<UserJpaEntity> results = em.createQuery(jpql, UserJpaEntity.class)
            .setParameter("email", email.getValue())
            .setMaxResults(1)
            .getResultList();

        return results.isEmpty() ? Optional.empty() : Optional.of(mapper.toDomain(results.get(0)));
    }

    @Override
    public Optional<User> findByUsername(String username) {
        String jpql = "SELECT u FROM UserJpaEntity u WHERE u.username = :username";
        List<UserJpaEntity> results = em.createQuery(jpql, UserJpaEntity.class)
            .setParameter("username", username)
            .setMaxResults(1)
            .getResultList();

        return results.isEmpty() ? Optional.empty() : Optional.of(mapper.toDomain(results.get(0)));
    }

    @Override
    public User save(User user) {
        UserJpaEntity entity = mapper.toEntity(user);

        if (user.getId() == null) {
            em.persist(entity);
        } else {
            entity = em.merge(entity);
        }

        em.flush();
        return mapper.toDomain(entity);
    }

    @Override
    public void delete(UserId id) {
        UserJpaEntity entity = em.find(UserJpaEntity.class, id.getValue());
        if (entity != null) {
            em.remove(entity);
        }
    }

    @Override
    public List<User> findAll(Integer page, Integer size, String orderBy, String order) {
        String jpql = "SELECT u FROM UserJpaEntity u ORDER BY u." + orderBy + " " + order;
        TypedQuery<UserJpaEntity> query = em.createQuery(jpql, UserJpaEntity.class);

        if (page != null && size != null) {
            query.setFirstResult(page * size);
            query.setMaxResults(size);
        }

        return query.getResultStream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<User> findByUsernameContaining(String search, Integer page, Integer size) {
        String jpql = "SELECT u FROM UserJpaEntity u WHERE LOWER(u.username) LIKE LOWER(:search)";
        TypedQuery<UserJpaEntity> query = em.createQuery(jpql, UserJpaEntity.class);
        query.setParameter("search", "%" + search + "%");

        if (page != null && size != null) {
            query.setFirstResult((page - 1) * size);
            query.setMaxResults(size);
        }

        return query.getResultStream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public Long countAll() {
        String jpql = "SELECT COUNT(u) FROM UserJpaEntity u";
        return em.createQuery(jpql, Long.class).getSingleResult();
    }

    @Override
    public void addFollower(UserId userId, UserId followerId) {
        UserJpaEntity follower = em.find(UserJpaEntity.class, followerId.getValue());
        if (follower != null) {
            if (!follower.getFollowingIds().contains(userId.getValue())) {
                follower.getFollowingIds().add(userId.getValue());
                em.merge(follower);
            }
        }
    }

    @Override
    public void removeFollower(UserId userId, UserId followerId) {
        UserJpaEntity follower = em.find(UserJpaEntity.class, followerId.getValue());
        if (follower != null) {
            follower.getFollowingIds().remove(userId.getValue());
            em.merge(follower);
        }
    }

    @Override
    public boolean isFollowing(UserId userId, UserId followedId) {
        UserJpaEntity user = em.find(UserJpaEntity.class, userId.getValue());
        return user != null && user.getFollowingIds().contains(followedId.getValue());
    }

    @Override
    public List<UserId> getFollowerIds(UserId userId, Integer page, Integer size) {
        String jpql = "SELECT u.id FROM UserJpaEntity u WHERE :userId MEMBER OF u.followingIds";
        TypedQuery<Long> query = em.createQuery(jpql, Long.class)
            .setParameter("userId", userId.getValue());

        if (page != null && size != null) {
            query.setFirstResult(page * size);
            query.setMaxResults(size);
        }

        return query.getResultStream()
            .map(UserId::new)
            .collect(Collectors.toList());
    }

    @Override
    public List<UserId> getFollowingIds(UserId userId, Integer page, Integer size) {
        UserJpaEntity user = em.find(UserJpaEntity.class, userId.getValue());
        if (user == null) {
            return List.of();
        }

        List<Long> followingIds = user.getFollowingIds();

        int start = (page != null && size != null) ? page * size : 0;
        int end = (size != null) ? Math.min(start + size, followingIds.size()) : followingIds.size();

        return followingIds.subList(start, end).stream()
            .map(UserId::new)
            .collect(Collectors.toList());
    }

    @Override
    public Long countFollowers(UserId userId) {
        String jpql = "SELECT COUNT(u) FROM UserJpaEntity u WHERE :userId MEMBER OF u.followingIds";
        return em.createQuery(jpql, Long.class)
            .setParameter("userId", userId.getValue())
            .getSingleResult();
    }

    @Override
    public Long countFollowing(UserId userId) {
        UserJpaEntity user = em.find(UserJpaEntity.class, userId.getValue());
        return user != null ? (long) user.getFollowingIds().size() : 0L;
    }

    @Override
    public void addFavoriteAlbum(UserId userId, Long albumId) {
        UserJpaEntity user = em.find(UserJpaEntity.class, userId.getValue());
        if (user != null && !user.getFavoriteAlbumIds().contains(albumId)) {
            user.getFavoriteAlbumIds().add(albumId);
            em.merge(user);
        }
    }

    @Override
    public void removeFavoriteAlbum(UserId userId, Long albumId) {
        UserJpaEntity user = em.find(UserJpaEntity.class, userId.getValue());
        if (user != null) {
            user.getFavoriteAlbumIds().remove(albumId);
            em.merge(user);
        }
    }

    @Override
    public boolean isAlbumFavorite(UserId userId, Long albumId) {
        UserJpaEntity user = em.find(UserJpaEntity.class, userId.getValue());
        return user != null && user.getFavoriteAlbumIds().contains(albumId);
    }

    @Override
    public List<Long> getFavoriteAlbumIds(UserId userId, Integer page, Integer size) {
        UserJpaEntity user = em.find(UserJpaEntity.class, userId.getValue());
        if (user == null) {
            return List.of();
        }

        List<Long> favoriteIds = user.getFavoriteAlbumIds();

        int start = (page != null && size != null) ? page * size : 0;
        int end = (size != null) ? Math.min(start + size, favoriteIds.size()) : favoriteIds.size();

        return favoriteIds.subList(start, end);
    }

    @Override
    public void addFavoriteArtist(UserId userId, Long artistId) {
        UserJpaEntity user = em.find(UserJpaEntity.class, userId.getValue());
        if (user != null && !user.getFavoriteArtistIds().contains(artistId)) {
            user.getFavoriteArtistIds().add(artistId);
            em.merge(user);
        }
    }

    @Override
    public void removeFavoriteArtist(UserId userId, Long artistId) {
        UserJpaEntity user = em.find(UserJpaEntity.class, userId.getValue());
        if (user != null) {
            user.getFavoriteArtistIds().remove(artistId);
            em.merge(user);
        }
    }

    @Override
    public boolean isArtistFavorite(UserId userId, Long artistId) {
        UserJpaEntity user = em.find(UserJpaEntity.class, userId.getValue());
        return user != null && user.getFavoriteArtistIds().contains(artistId);
    }

    @Override
    public List<Long> getFavoriteArtistIds(UserId userId, Integer page, Integer size) {
        UserJpaEntity user = em.find(UserJpaEntity.class, userId.getValue());
        if (user == null) {
            return List.of();
        }

        List<Long> favoriteIds = user.getFavoriteArtistIds();

        int start = (page != null && size != null) ? page * size : 0;
        int end = (size != null) ? Math.min(start + size, favoriteIds.size()) : favoriteIds.size();

        return favoriteIds.subList(start, end);
    }

    @Override
    public void addFavoriteSong(UserId userId, Long songId) {
        UserJpaEntity user = em.find(UserJpaEntity.class, userId.getValue());
        if (user != null && !user.getFavoriteSongIds().contains(songId)) {
            user.getFavoriteSongIds().add(songId);
            em.merge(user);
        }
    }

    @Override
    public void removeFavoriteSong(UserId userId, Long songId) {
        UserJpaEntity user = em.find(UserJpaEntity.class, userId.getValue());
        if (user != null) {
            user.getFavoriteSongIds().remove(songId);
            em.merge(user);
        }
    }

    @Override
    public boolean isSongFavorite(UserId userId, Long songId) {
        UserJpaEntity user = em.find(UserJpaEntity.class, userId.getValue());
        return user != null && user.getFavoriteSongIds().contains(songId);
    }

    @Override
    public List<Long> getFavoriteSongIds(UserId userId, Integer page, Integer size) {
        UserJpaEntity user = em.find(UserJpaEntity.class, userId.getValue());
        if (user == null) {
            return List.of();
        }

        List<Long> favoriteSongIds = user.getFavoriteSongIds();

        int start = (page != null && size != null) ? page * size : 0;
        int end = (size != null) ? Math.min(start + size, favoriteSongIds.size()) : favoriteSongIds.size();

        return favoriteSongIds.subList(start, end);
    }
}
