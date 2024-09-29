package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.Album;
import ar.edu.itba.paw.models.Artist;
import ar.edu.itba.paw.models.FilterType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class ArtistJdbcDao implements ArtistDao {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    public ArtistJdbcDao(final DataSource ds) {
        this.jdbcTemplate = new JdbcTemplate(ds);
        jdbcInsert = new SimpleJdbcInsert(ds)
                .withTableName("artist")
                .usingGeneratedKeyColumns("id");
    }

    @Override
    public Optional<Artist> findById(long id) {
        return jdbcTemplate.query("SELECT * FROM artist WHERE id = ?",
                new Object[]{id},
                new int[]{Types.BIGINT},
                SimpleRowMappers.ARTIST_ROW_MAPPER
        ).stream().findFirst();
    }

    @Override
    public List<Artist> findAll() {
        return jdbcTemplate.query("SELECT * FROM artist", SimpleRowMappers.ARTIST_ROW_MAPPER);
    }

    @Override
    public List<Artist> findPaginated(FilterType filterType, int limit, int offset) {
        String sql;
        if (filterType.equals(FilterType.NEWEST)) {
            sql = "SELECT id FROM artist ORDER BY created_at DESC LIMIT ? OFFSET ?";
        } else if (filterType.equals(FilterType.OLDEST)) {
            sql = "SELECT id FROM artist ORDER BY created_at ASC LIMIT ? OFFSET ?";
        } else {
            sql = "SELECT id FROM artist ORDER BY avg_rating LIMIT ? OFFSET ?";
        }

        List<Integer> ids = jdbcTemplate.queryForList(sql, new Object[]{ limit, offset }, new int[]{ Types.BIGINT, Types.BIGINT }, Integer.class);

        // Buscamos los álbumes correspondientes a cada id
        List<Artist> artists = new ArrayList<>();
        for (Integer id : ids) {
            Optional<Artist> artist = this.findById(id);
            artist.ifPresent(artists::add);
        }

        return artists;
    }

    @Override
    public List<Artist> findBySongId(long id) {
        return jdbcTemplate.query("SELECT DISTINCT a.* FROM artist a JOIN song_artist sa ON a.id = sa.artist_id WHERE sa.song_id = ?",
                new Object[]{id},
                new int[]{Types.BIGINT},
                SimpleRowMappers.ARTIST_ROW_MAPPER);
    }

    @Override
    public List<Artist> findByNameContaining(String sub) {
        String sql = "SELECT * FROM artist WHERE name ILIKE ?";
        return jdbcTemplate.query(sql, new Object[]{"%" + sub + "%"}, SimpleRowMappers.ARTIST_ROW_MAPPER);
    }

    @Override
    public long save(Artist artist) {
        Map<String, Object> imageData = Map.of("name", artist.getName(),
                "bio", artist.getBio(), "img_id", artist.getImgId());
        return jdbcInsert.executeAndReturnKey(imageData).longValue();
    }


    @Override
    public int update(Artist artist) {
        return jdbcTemplate.update(
                "UPDATE artist SET name = ?, bio = ?, created_at = ?, updated_at = ?, img_id = ? WHERE id = ?",
                artist.getName(),
                artist.getBio(),
                artist.getCreatedAt(),
                artist.getUpdatedAt(),
                artist.getImgId(),
                artist.getId()
        );
    }

    @Override
    public void updateRating(long artistId, float newRating, int newRatingAmount) {
        final String sql = "UPDATE artist SET avg_rating = ?, rating_amount = ? WHERE id = ?";
        jdbcTemplate.update(sql, newRating, newRatingAmount, artistId);
    }

    @Override
    public boolean hasUserReviewed(long userId, long artistId) {
        final String sql = "SELECT COUNT(*) FROM artist_review ar JOIN review r ON ar.review_id = r.id WHERE r.user_id = ? AND ar.artist_id = ?";
        int count = jdbcTemplate.queryForObject(sql, Integer.class, userId, artistId);
        return count > 0;
    }

    @Override
    public int deleteById(long id) {
        return jdbcTemplate.update("DELETE FROM artist WHERE id = ?", id);
    }
}

