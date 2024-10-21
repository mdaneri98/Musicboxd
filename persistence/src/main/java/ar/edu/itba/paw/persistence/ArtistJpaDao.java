package ar.edu.itba.paw.persistence;


import ar.edu.itba.paw.models.Artist;
import ar.edu.itba.paw.models.FilterType;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Repository
public class ArtistJpaDao implements ArtistDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Artist> find(long id) {
        return Optional.ofNullable(entityManager.find(Artist.class, id));
    }

    @Override
    public List<Artist> findAll() {
        TypedQuery<Artist> query = entityManager.createQuery("FROM Artist", Artist.class);
        return query.getResultList();
    }

    @Override
    public List<Artist> findPaginated(FilterType filterType, int limit, int offset) {
        String jpql = "FROM Artist " + filterType.getFilter();
        TypedQuery<Artist> query = entityManager.createQuery(jpql, Artist.class)
                .setFirstResult(offset)
                .setMaxResults(limit);
        return query.getResultList();
    }

    @Override
    public List<Artist> findBySongId(long id) {
        String jpql = "SELECT DISTINCT a FROM Artist a JOIN a.songs s WHERE s.id = :songId";
        TypedQuery<Artist> query = entityManager.createQuery(jpql, Artist.class)
                .setParameter("songId", id);
        return query.getResultList();
    }

    @Override
    public List<Artist> findByNameContaining(String sub) {
        String jpql = "FROM Artist a WHERE LOWER(a.name) LIKE LOWER(:name)";
        TypedQuery<Artist> query = entityManager.createQuery(jpql, Artist.class)
                .setParameter("name", "%" + sub + "%")
                .setMaxResults(10);
        return query.getResultList();
    }

    @Override
    public Artist create(Artist artist) {
        entityManager.persist(artist);
        entityManager.flush();  // This forces the INSERT operation to happen immediately
        return artist;
    }

    @Override
    public Artist update(Artist artist) {
        entityManager.merge(artist);  // Updates the existing entity
        return artist;
    }

    @Override
    public boolean updateRating(long artistId, float newRating, int newRatingAmount) {
        String jpql = "UPDATE Artist a SET a.avgRating = :avgRating, a.ratingCount = :ratingCount WHERE a.id = :id";
        int updatedCount = entityManager.createQuery(jpql)
                .setParameter("avgRating", newRating)
                .setParameter("ratingCount", newRatingAmount)
                .setParameter("id", artistId)
                .executeUpdate();
        return updatedCount == 1;
    }

    @Override
    public boolean hasUserReviewed(long userId, long artistId) {
        String jpql = "SELECT COUNT(r) FROM Review r JOIN ArtistReview ar ON r.id = ar.artist_id WHERE r.user.id = :userId AND ar.artist.id = :artistId";
        Long count = entityManager.createQuery(jpql, Long.class)
                .setParameter("userId", userId)
                .setParameter("artistId", artistId)
                .getSingleResult();
        return count > 0;
    }

    @Override
    public boolean delete(long id) {
        Artist artist = entityManager.find(Artist.class, id);
        if (artist != null) {
            entityManager.remove(artist);
            return true;
        }
        return false;
    }
}
