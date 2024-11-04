package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.Artist;
import ar.edu.itba.paw.models.FilterType;
import ar.edu.itba.paw.models.Song;
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
public class SongJpaDao implements SongDao {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Optional<Song> find(long id) {
        return Optional.ofNullable(em.find(Song.class, id));
    }

    @Override
    public List<Song> findByArtistId(long artistId) {
        String query = "SELECT s FROM Song s JOIN s.album a JOIN a.artist ar WHERE ar.id = :artistId";
        return em.createQuery(query, Song.class)
                .setParameter("artistId", artistId)
                .getResultList();
    }

    @Override
    public List<Song> findByAlbumId(long albumId) {
        String query = "SELECT s FROM Song s WHERE s.album.id = :albumId";
        return em.createQuery(query, Song.class)
                .setParameter("albumId", albumId)
                .getResultList();
    }

    @Override
    public List<Song> findByTitleContaining(String sub) {
        String query = "SELECT s FROM Song s WHERE LOWER(s.title) LIKE LOWER(:sub)";
        return em.createQuery(query, Song.class)
                .setParameter("sub", "%" + sub + "%")
                .setMaxResults(10)
                .getResultList();
    }

    @Override
    public List<Song> findAll() {
        String query = "SELECT s FROM Song s";
        return em.createQuery(query, Song.class).getResultList();
    }

    @Override
    public List<Song> findPaginated(FilterType filterType, int limit, int offset) {
        String baseQuery = "SELECT s FROM Song s " + filterType.getFilter();
        TypedQuery<Song> query = em.createQuery(baseQuery, Song.class)
                .setFirstResult(offset)
                .setMaxResults(limit);
        return query.getResultList();
    }

    @Override
    public Song create(Song song) {
        em.persist(song);
        return song;
    }

    @Override
    @Transactional
    public int saveSongArtist(Song song, Artist artist) {
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
    public boolean updateRating(long songId, Double newRating, int newRatingAmount) {
        Optional<Song> maybeSong = find(songId);
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
    public boolean hasUserReviewed(long userId, long songId) {
        String query = "SELECT COUNT(sr) FROM SongReview sr WHERE sr.user.id = :userId AND sr.song.id = :songId";
        Long count = em.createQuery(query, Long.class)
                .setParameter("userId", userId)
                .setParameter("songId", songId)
                .getSingleResult();
        return count > 0;
    }

    @Override
    public boolean delete(long id) {
        Optional<Song> maybeSong = find(id);
        if (maybeSong.isPresent()) {
            em.remove(maybeSong.get());
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteReviewsFromSong(long songId) {
        Query query = em.createQuery(
                "DELETE FROM Review r WHERE r.id IN " +
                        "(SELECT ar.id FROM SongReview ar WHERE ar.song.id = :songId)");
        query.setParameter("songId", songId);
        return query.executeUpdate() >= 1;
    }

}

