package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.Album;
import ar.edu.itba.paw.models.FilterType;
import ar.edu.itba.paw.models.reviews.AlbumReview;

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
public class AlbumJpaDao implements AlbumDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Album> find(long id) {
        // Buscar una entidad por su clave primaria
        return Optional.ofNullable(entityManager.find(Album.class, id));
    }

    @Override
    public List<Album> findAll() {
        // Consultar todos los álbumes
        TypedQuery<Album> query = entityManager.createQuery("SELECT a FROM Album a JOIN FETCH a.artist", Album.class);
        return query.getResultList();
    }

    @Override
    public List<Album> findPaginated(FilterType filterType, int limit, int offset) {
        // Query 1: SQL nativo para obtener IDs paginados (garantiza paginación en BD)
        String nativeSQL = "SELECT a.id FROM album a " + filterType.getFilter();
        Query nativeQuery = entityManager.createNativeQuery(nativeSQL);
        nativeQuery.setFirstResult(offset);
        nativeQuery.setMaxResults(limit);
        
        @SuppressWarnings("unchecked")
        List<Object> rawResults = nativeQuery.getResultList();
        List<Long> albumIds = rawResults.stream()
                .map(n -> ((Number)n).longValue())
                .collect(Collectors.toList());
        
        if (albumIds.isEmpty()) {
            return Collections.emptyList();
        }
        
        // Query 2: JPQL para obtener entidades completas manteniendo el orden del filtro
        String entityQuery = "SELECT a FROM Album a WHERE a.id IN :ids " + filterType.getFilter();
        TypedQuery<Album> query = entityManager.createQuery(entityQuery, Album.class);
        query.setParameter("ids", albumIds);
        
        return query.getResultList();
    }

    @Override
    public List<Album> findByTitleContaining(String sub) {
        // Query 1: SQL nativo para obtener IDs paginados (garantiza paginación en BD)
        Query nativeQuery = entityManager.createNativeQuery(
                "SELECT a.id FROM album a " +
                "WHERE LOWER(a.title) LIKE LOWER(:sub) " +
                "ORDER BY a.title"
        );
        nativeQuery.setParameter("sub", "%" + sub + "%");
        nativeQuery.setMaxResults(10); // Límite de resultados
        
        @SuppressWarnings("unchecked")
        List<Object> rawResults = nativeQuery.getResultList();
        List<Long> albumIds = rawResults.stream()
                .map(n -> ((Number)n).longValue())
                .collect(Collectors.toList());
        
        if (albumIds.isEmpty()) {
            return Collections.emptyList();
        }
        
        // Query 2: JPQL para obtener entidades completas
        TypedQuery<Album> query = entityManager.createQuery(
                "SELECT a FROM Album a JOIN FETCH a.artist WHERE a.id IN :ids ORDER BY a.title", 
                Album.class);
        query.setParameter("ids", albumIds);
        
        return query.getResultList();
    }

    @Override
    public List<Album> findByArtistId(long artistId) {
        // Buscar álbumes por el ID del artista
        TypedQuery<Album> query = entityManager.createQuery(
                "SELECT a FROM Album a JOIN FETCH a.artist WHERE a.artist.id = :artistId", Album.class);
        query.setParameter("artistId", artistId);
        return query.getResultList();
    }

    @Override
    public Album create(Album album) {
        entityManager.persist(album);
        return album;
    }

    @Override
    public Album update(Album album) {
        // Actualizar una entidad existente
        entityManager.merge(album);
        return album;
    }

    @Override
    public boolean updateRating(long albumId, Double newRating, int newRatingAmount) {
        // Actualizar la calificación del álbum
        Album album = entityManager.find(Album.class, albumId);
        if (album != null) {
            album.setAvgRating(newRating);
            album.setRatingCount(newRatingAmount);
            return true;
        }
        return false;
    }

    @Override
    public boolean hasUserReviewed(long userId, long albumId) {
        // Comprobar si un usuario ha revisado un álbum
        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(ar) FROM AlbumReview ar WHERE ar.user.id = :userId AND ar.album.id = :albumId AND ar.isBlocked = false",
                Long.class
        );
        query.setParameter("userId", userId);
        query.setParameter("albumId", albumId);
        long count = query.getSingleResult();
        return count > 0;
    }

    @Override
    public boolean delete(long id) {
        // Eliminar un álbum por su ID
        Album album = entityManager.find(Album.class, id);
        if (album != null) {
            entityManager.remove(album);
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteReviewsFromAlbum(long albumId) {
        Query query = entityManager.createQuery(
                "DELETE FROM Review r WHERE r.id IN " +
                        "(SELECT ar.id FROM AlbumReview ar WHERE ar.album.id = :albumId)");
        query.setParameter("albumId", albumId);
        return query.executeUpdate() >= 1;
    }

    @Override
    public List<AlbumReview> findReviewsByAlbumId(long albumId) {
        final TypedQuery<AlbumReview> query = entityManager.createQuery(
                "FROM AlbumReview ar WHERE ar.album.id = :albumId AND ar.isBlocked = false ORDER BY ar.createdAt DESC",
                AlbumReview.class
        );
        query.setParameter("albumId", albumId);
        return query.getResultList();
    }

}
