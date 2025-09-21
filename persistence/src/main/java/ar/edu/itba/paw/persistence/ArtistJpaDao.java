package ar.edu.itba.paw.persistence;


import ar.edu.itba.paw.models.Artist;
import ar.edu.itba.paw.models.FilterType;
import ar.edu.itba.paw.models.reviews.ArtistReview;

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
        // Query 1: SQL nativo para obtener IDs paginados (garantiza paginación en BD)
        String nativeSQL = "SELECT a.id FROM artist a " + filterType.getFilter();
        Query nativeQuery = entityManager.createNativeQuery(nativeSQL)
                .setFirstResult(offset)
                .setMaxResults(limit);
        
        @SuppressWarnings("unchecked")
        List<Object> rawResults = nativeQuery.getResultList();
        List<Long> artistIds = rawResults.stream()
                .map(n -> ((Number)n).longValue())
                .collect(Collectors.toList());
        
        if (artistIds.isEmpty()) {
            return Collections.emptyList();
        }
        
        // Query 2: JPQL para obtener entidades completas manteniendo el orden del filtro
        String entityJpql = "SELECT a FROM Artist a WHERE a.id IN :ids " + filterType.getFilter();
        TypedQuery<Artist> query = entityManager.createQuery(entityJpql, Artist.class)
                .setParameter("ids", artistIds);
        
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
        // Query 1: SQL nativo para obtener IDs paginados (garantiza paginación en BD)
        Query nativeQuery = entityManager.createNativeQuery(
                "SELECT a.id FROM artist a " +
                "WHERE LOWER(a.name) LIKE LOWER(:name) " +
                "ORDER BY a.name"
        );
        nativeQuery.setParameter("name", "%" + sub + "%");
        nativeQuery.setMaxResults(10); // Límite de resultados
        
        @SuppressWarnings("unchecked")
        List<Object> rawResults = nativeQuery.getResultList();
        List<Long> artistIds = rawResults.stream()
                .map(n -> ((Number)n).longValue())
                .collect(Collectors.toList());
        
        if (artistIds.isEmpty()) {
            return Collections.emptyList();
        }
        
        // Query 2: JPQL para obtener entidades completas
        TypedQuery<Artist> query = entityManager.createQuery(
                "FROM Artist a WHERE a.id IN :ids ORDER BY a.name", 
                Artist.class);
        query.setParameter("ids", artistIds);
        
        return query.getResultList();
    }

    @Override
    public Artist create(Artist artist) {
        entityManager.persist(artist);
        return artist;
    }

    @Override
    public Artist update(Artist artist) {
        entityManager.merge(artist);  // Updates the existing entity
        return artist;
    }

    @Override
    public boolean updateRating(long artistId, Double newRating, int newRatingAmount) {
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
        String jpql = "SELECT COUNT(r) FROM ArtistReview r WHERE r.user.id = :userId AND r.artist.id = :artistId AND r.isBlocked = false";
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

    @Override
    public boolean deleteReviewsFromArtist(long artistId) {
        Query query = entityManager.createQuery(
                "DELETE FROM Review r WHERE r.id IN " +
                        "(SELECT ar.id FROM ArtistReview ar WHERE ar.artist.id = :artistId)");
        query.setParameter("artistId", artistId);
        return query.executeUpdate() >= 1;
    }

    @Override
    public List<ArtistReview> findReviewsByArtistId(long artistId) {
        final TypedQuery<ArtistReview> query = entityManager.createQuery(
                "FROM ArtistReview review WHERE review.artist.id = :artistId AND review.isBlocked = false ORDER BY review.createdAt DESC",
                ArtistReview.class
        );
        query.setParameter("artistId", artistId);
        return query.getResultList();
    }

}
