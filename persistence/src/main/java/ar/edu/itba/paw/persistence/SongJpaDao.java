package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.Artist;
import ar.edu.itba.paw.models.FilterType;
import ar.edu.itba.paw.models.Song;
import ar.edu.itba.paw.models.reviews.SongReview;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
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
public class SongJpaDao implements SongDao {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Optional<Song> findById(Long id) {
        return Optional.ofNullable(em.find(Song.class, id));
    }

    @Override
    public List<Song> findByArtistId(Long artistId, Integer pageSize, Integer offset) {
        // Query 1: SQL nativo para obtener IDs paginados (garantiza paginación en BD)
        Query nativeQuery = em.createNativeQuery(
                "SELECT s.id FROM song s " +
                "JOIN album a ON s.album_id = a.id " +
                "WHERE a.artist_id = :artistId " +
                "ORDER BY s.avg_rating DESC"
        );
        nativeQuery.setParameter("artistId", artistId);
        nativeQuery.setFirstResult(offset);
        nativeQuery.setMaxResults(pageSize);
        
        @SuppressWarnings("unchecked")
        List<Object> rawResults = nativeQuery.getResultList();
        List<Long> songIds = rawResults.stream()
                .map(n -> ((Number)n).longValue())
                .collect(Collectors.toList());
        
        if (songIds.isEmpty()) {
            return Collections.emptyList();
        }
        
        // Query 2: JPQL para obtener entidades completas
        TypedQuery<Song> query = em.createQuery(
                "FROM Song s WHERE s.id IN :ids ORDER BY s.avgRating DESC",
                Song.class
        );
        query.setParameter("ids", songIds);
        
        return query.getResultList();
    }

    @Override
    public List<Song> findByAlbumId(Long albumId) {
        String query = "SELECT s FROM Song s WHERE s.album.id = :albumId";
        return em.createQuery(query, Song.class)
                .setParameter("albumId", albumId)
                .getResultList();
    }

    @Override
    public List<Song> findByTitleContaining(String sub) {
        // Query 1: SQL nativo para obtener IDs paginados (garantiza paginación en BD)
        Query nativeQuery = em.createNativeQuery(
                "SELECT s.id FROM song s " +
                "WHERE LOWER(s.title) LIKE LOWER(:sub) " +
                "ORDER BY s.title"
        );
        nativeQuery.setParameter("sub", "%" + sub + "%");
        nativeQuery.setMaxResults(10); // Límite de resultados
        
        @SuppressWarnings("unchecked")
        List<Object> rawResults = nativeQuery.getResultList();
        List<Long> songIds = rawResults.stream()
                .map(n -> ((Number)n).longValue())
                .collect(Collectors.toList());
        
        if (songIds.isEmpty()) {
            return Collections.emptyList();
        }
        
        // Query 2: JPQL para obtener entidades completas
        TypedQuery<Song> query = em.createQuery(
                "FROM Song s WHERE s.id IN :ids ORDER BY s.title", 
                Song.class);
        query.setParameter("ids", songIds);
        
        return query.getResultList();
    }

    @Override
    public List<Song> findAll() {
        String query = "SELECT s FROM Song s";
        return em.createQuery(query, Song.class).getResultList();
    }

    @Override
    public List<Song> findPaginated(FilterType filterType, Integer limit, Integer offset) {
        // Query 1: SQL nativo para obtener IDs paginados (garantiza paginación en BD)
        String nativeSQL = "SELECT s.id FROM song s " + filterType.getFilter();
        Query nativeQuery = em.createNativeQuery(nativeSQL)
                .setFirstResult(offset)
                .setMaxResults(limit);
        
        @SuppressWarnings("unchecked")
        List<Object> rawResults = nativeQuery.getResultList();
        List<Long> songIds = rawResults.stream()
                .map(n -> ((Number)n).longValue())
                .collect(Collectors.toList());
        
        if (songIds.isEmpty()) {
            return Collections.emptyList();
        }
        
        // Query 2: JPQL para obtener entidades completas manteniendo el orden del filtro
        String entityQuery = "SELECT s FROM Song s WHERE s.id IN :ids " + filterType.getFilter();
        TypedQuery<Song> query = em.createQuery(entityQuery, Song.class)
                .setParameter("ids", songIds);
        
        return query.getResultList();
    }

    @Override
    public Song create(Song song) {
        em.persist(song);
        return song;
    }

    @Override
    @Transactional
    public Integer saveSongArtist(Song song, Artist artist) {
        try {
            song = em.find(Song.class, song.getId());
            artist = em.find(Artist.class, artist.getId());

            if (song == null || artist == null)
                return 0;

            if (!song.getArtists().contains(artist)) {
                song.getArtists().add(artist);
                artist.getSongs().add(song);
                em.merge(song);
            }

            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public Song update(Song song) {
        em.merge(song);
        return song;
    }

    @Override
    public Boolean updateRating(Long songId, Double newRating, Integer newRatingAmount) {
        Optional<Song> maybeSong = findById(songId);
        if (maybeSong.isPresent()) {
            Song song = maybeSong.get();
            song.setAvgRating(newRating);
            song.setRatingCount(newRatingAmount);
            em.merge(song);
            return true;
        }
        return false;
    }

    @Override
    public Boolean hasUserReviewed(Long userId, Long songId) {
        String query = "SELECT COUNT(sr) FROM SongReview sr WHERE sr.user.id = :userId AND sr.song.id = :songId AND sr.isBlocked = false";
        Long count = em.createQuery(query, Long.class)
                .setParameter("userId", userId)
                .setParameter("songId", songId)
                .getSingleResult();
        return count > 0;
    }

    @Override
    public Boolean delete(Long id) {
        Optional<Song> maybeSong = findById(id);
        if (maybeSong.isPresent()) {
            em.remove(maybeSong.get());
            return true;
        }
        return false;
    }

    @Override
    public Boolean deleteReviewsFromSong(Long songId) {
        Query query = em.createQuery(
                "DELETE FROM Review r WHERE r.id IN " +
                        "(SELECT ar.id FROM SongReview ar WHERE ar.song.id = :songId)");
        query.setParameter("songId", songId);
        return query.executeUpdate() >= 1;
    }


    @Override
    public List<SongReview> findReviewsBySongId(Long songId) {
        final TypedQuery<SongReview> query = em.createQuery(
                "FROM SongReview sr WHERE sr.song.id = :songId AND sr.isBlocked = false ORDER BY sr.createdAt DESC",
                SongReview.class
        );
        query.setParameter("songId", songId);
        return query.getResultList();
    }

    @Override
    public Long countAll() {
        Query query = em.createQuery("SELECT COUNT(s) FROM Song s ");
        return (Long) query.getSingleResult();
    }

}

