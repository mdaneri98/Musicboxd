package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.Album;
import ar.edu.itba.paw.models.FilterType;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

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
        // Aplicar filtros dinámicos y paginar los resultados
        String baseQuery = "SELECT a FROM Album a " + filterType.getFilter();
        TypedQuery<Album> query = entityManager.createQuery(baseQuery, Album.class);
        query.setFirstResult(offset);
        query.setMaxResults(limit);
        return query.getResultList();
    }

    @Override
    public List<Album> findByTitleContaining(String sub) {
        // Buscar álbumes cuyo título contenga una subcadena
        TypedQuery<Album> query = entityManager.createQuery(
                "SELECT a FROM Album a JOIN FETCH a.artist WHERE LOWER(a.title) LIKE LOWER(:sub)", Album.class);
        query.setParameter("sub", "%" + sub + "%");
        query.setMaxResults(10); // Límite de resultados
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
    public boolean updateRating(long albumId, float newRating, int newRatingAmount) {
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
                "SELECT COUNT(ar) FROM AlbumReview ar WHERE ar.user.id = :userId AND ar.album.id = :albumId",
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
                        "(SELECT ar.review.id FROM AlbumReview ar WHERE ar.album.id = :albumId)");
        query.setParameter("albumId", albumId);
        return query.executeUpdate() >= 1;
    }

}
