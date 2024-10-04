package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.Album;
import ar.edu.itba.paw.models.Artist;
import ar.edu.itba.paw.models.FilterType;
import ar.edu.itba.paw.models.dtos.ArtistDTO;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Types;
import java.time.LocalDate;
import java.util.*;

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
    public Optional<Artist> find(long id) {
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
        String sql = "SELECT * FROM artist" +
                filterType.getFilter() +
                "LIMIT ? OFFSET ?";

        return jdbcTemplate.query(sql, new Object[]{ limit, offset }, new int[]{ Types.BIGINT, Types.BIGINT }, SimpleRowMappers.ARTIST_ROW_MAPPER);
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
        String sql = "SELECT * FROM artist WHERE name ILIKE ? LIMIT 10";
        return jdbcTemplate.query(sql, new Object[]{"%" + sub + "%"}, SimpleRowMappers.ARTIST_ROW_MAPPER);
    }

    @Override
    public Artist create(Artist artist) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO artist (name, bio, img_id) VALUES (?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, artist.getName());
            ps.setString(2, artist.getBio());
            ps.setObject(3, artist.getImgId());
            return ps;
        }, keyHolder);
        Map<String, Object> keys = keyHolder.getKeys();
        if (keys == null || !keys.containsKey("id"))
            throw new IllegalStateException("Failed to insert artist or generate key.");

        return this.find(((Number) keys.get("id")).longValue()).get();
    }

    @Override
    public Artist update(Artist artist) {
        jdbcTemplate.update(
                "UPDATE artist SET name = ?, bio = ?, updated_at = NOW(), img_id = ?, avg_rating = ?, rating_amount = ? WHERE id = ?",
                artist.getName(),
                artist.getBio(),
                artist.getImgId(),
                artist.getAvgRating(),
                artist.getRatingCount(),
                artist.getId()
        );
        return this.find(artist.getId()).get();
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
    public boolean delete(long id) {
        return jdbcTemplate.update("DELETE FROM artist WHERE id = ?", id) == 1;
    }

}

