package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.domain.song.Song;
import ar.edu.itba.paw.domain.song.SongId;
import ar.edu.itba.paw.domain.song.SongRepository;
import ar.edu.itba.paw.infrastructure.jpa.SongJpaEntity;
import ar.edu.itba.paw.models.FilterType;
import ar.edu.itba.paw.persistence.mappers.SongMapper;
import org.springframework.beans.factory.annotation.Autowired;
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
public class SongRepositoryJpa implements SongRepository {

    @PersistenceContext
    private EntityManager em;

    private final SongMapper songMapper;

    @Autowired
    public SongRepositoryJpa(SongMapper songMapper) {
        this.songMapper = songMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Song> findById(SongId id) {
        SongJpaEntity entity = em.find(SongJpaEntity.class, id.getValue());
        return Optional.ofNullable(entity).map(songMapper::toDomain);
    }

    @Override
    @Transactional
    public Song save(Song song) {
        SongJpaEntity entity = songMapper.toJpaEntity(song);
        if (entity.getId() == null) {
            em.persist(entity);
        } else {
            entity = em.merge(entity);
        }
        return songMapper.toDomain(entity);
    }

    @Override
    @Transactional
    public void delete(SongId id) {
        SongJpaEntity entity = em.find(SongJpaEntity.class, id.getValue());
        if (entity != null) {
            em.remove(entity);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Song> findAll(int page, int size) {
        TypedQuery<SongJpaEntity> query = em.createQuery(
            "FROM SongJpaEntity s ORDER BY s.createdAt DESC",
            SongJpaEntity.class
        );
        query.setFirstResult(page * size);
        query.setMaxResults(size);
        return query.getResultList().stream()
            .map(songMapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Song> findByArtistId(Long artistId, FilterType filterType, int page, int size) {
        Query nativeQuery = em.createNativeQuery(
            "SELECT s.id FROM song s " +
            "JOIN album a ON s.album_id = a.id " +
            "WHERE a.artist_id = :artistId " +
            filterType.getFilter().replace("ORDER BY ", "ORDER BY s.")
        );
        nativeQuery.setParameter("artistId", artistId);
        nativeQuery.setFirstResult(page * size);
        nativeQuery.setMaxResults(size);

        @SuppressWarnings("unchecked")
        List<Object> rawResults = nativeQuery.getResultList();
        List<Long> songIds = rawResults.stream()
            .map(n -> ((Number) n).longValue())
            .collect(Collectors.toList());

        if (songIds.isEmpty()) {
            return Collections.emptyList();
        }

        TypedQuery<SongJpaEntity> query = em.createQuery(
            "FROM SongJpaEntity s WHERE s.id IN :ids ORDER BY s.avgRating DESC",
            SongJpaEntity.class
        );
        query.setParameter("ids", songIds);

        return query.getResultList().stream()
            .map(songMapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Song> findByAlbumId(Long albumId) {
        TypedQuery<SongJpaEntity> query = em.createQuery(
            "FROM SongJpaEntity s WHERE s.albumId = :albumId",
            SongJpaEntity.class
        );
        query.setParameter("albumId", albumId);
        return query.getResultList().stream()
            .map(songMapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Song> findByTitleContaining(String titleSubstring, int page, int size) {
        Query nativeQuery = em.createNativeQuery(
            "SELECT s.id FROM song s " +
            "WHERE LOWER(s.title) LIKE LOWER(:sub) " +
            "ORDER BY s.title"
        );
        nativeQuery.setParameter("sub", "%" + titleSubstring + "%");
        nativeQuery.setFirstResult(page * size);
        nativeQuery.setMaxResults(size);

        @SuppressWarnings("unchecked")
        List<Object> rawResults = nativeQuery.getResultList();
        List<Long> songIds = rawResults.stream()
            .map(n -> ((Number) n).longValue())
            .collect(Collectors.toList());

        if (songIds.isEmpty()) {
            return Collections.emptyList();
        }

        TypedQuery<SongJpaEntity> query = em.createQuery(
            "FROM SongJpaEntity s WHERE s.id IN :ids ORDER BY s.title",
            SongJpaEntity.class
        );
        query.setParameter("ids", songIds);

        return query.getResultList().stream()
            .map(songMapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Long countAll() {
        TypedQuery<Long> query = em.createQuery(
            "SELECT COUNT(s) FROM SongJpaEntity s",
            Long.class
        );
        return query.getSingleResult();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasUserReviewed(Long userId, Long songId) {
        TypedQuery<Long> query = em.createQuery(
            "SELECT COUNT(sr) FROM SongReview sr WHERE sr.user.id = :userId AND sr.song.id = :songId AND sr.isBlocked = false",
            Long.class
        );
        query.setParameter("userId", userId);
        query.setParameter("songId", songId);
        return query.getSingleResult() > 0;
    }

    @Override
    @Transactional
    public boolean deleteReviewsFromSong(Long songId) {
        Query query = em.createQuery(
            "DELETE FROM Review r WHERE r.id IN " +
            "(SELECT sr.id FROM SongReview sr WHERE sr.song.id = :songId)"
        );
        query.setParameter("songId", songId);
        return query.executeUpdate() >= 1;
    }

    @Override
    @Transactional
    public boolean updateRating(Long songId, double newRating, int newRatingCount) {
        SongJpaEntity entity = em.find(SongJpaEntity.class, songId);
        if (entity != null) {
            entity.setAvgRating(newRating);
            entity.setRatingCount(newRatingCount);
            em.merge(entity);
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public void saveSongArtist(Long songId, Long artistId) {
        Query query = em.createNativeQuery(
            "INSERT INTO song_artist (song_id, artist_id) VALUES (:songId, :artistId) " +
            "ON CONFLICT DO NOTHING"
        );
        query.setParameter("songId", songId);
        query.setParameter("artistId", artistId);
        query.executeUpdate();
    }
}
