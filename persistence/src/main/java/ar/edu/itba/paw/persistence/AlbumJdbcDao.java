package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.Album;
import ar.edu.itba.paw.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Repository
public class AlbumJdbcDao implements AlbumDao {

   private final JdbcTemplate jdbcTemplate;

    private static final RowMapper<Album> ROW_MAPPER = (rs, rowNum) -> new Album(
            rs.getLong("id"),
            rs.getString("title"),
            rs.getString("genre"),
            rs.getObject("release_date", LocalDate.class),
            rs.getObject("created_at", LocalDate.class),
            rs.getObject("updated_at", LocalDate.class),
            rs.getString("img_src"),
            rs.getLong("artist_id")
    );

   public AlbumJdbcDao(final DataSource ds) {
       this.jdbcTemplate = new JdbcTemplate(ds);
   }

    @Override
    public Optional<Album> findById(long id) {
        // Jamás concatener valores en una query("SELECT ... WHERE username = " + id).
        return jdbcTemplate.query("SELECT * FROM albums WHERE id = ?",
                new Object[]{id},
                new int[]{Types.BIGINT},
                ROW_MAPPER
        ).stream().findFirst();
    }

    @Override
    public List<Album> findAll() {
        return jdbcTemplate.query("SELECT * FROM albums", ROW_MAPPER);
    }

    @Override
    public int save(Album album) {
        return jdbcTemplate.update(
                "INSERT INTO album (title, genre, release_date, created_at, updated_at, img_src, artist_id) VALUES (?, ?, ?, ?, ?, ?, ?)",
                album.getTitle(),
                album.getGenre(),
                album.getReleaseDate(),
                album.getCreatedAt(),
                album.getUpdatedAt(),
                album.getImgSrc(),
                album.getArtistId()
        );
    }

    @Override
    public int update(Album album) {
        return jdbcTemplate.update(
                "UPDATE album SET title = ?, genre = ?, release_date = ?, created_at = ?, updated_at = ?, img_src = ?, artist_id = ? WHERE id = ?",
                album.getTitle(),
                album.getGenre(),
                album.getReleaseDate(),
                album.getCreatedAt(),
                album.getUpdatedAt(),
                album.getImgSrc(),
                album.getArtistId(),
                album.getId()
        );
    }

    @Override
    public int deleteById(long id) {
        return jdbcTemplate.update("DELETE FROM album WHERE id = ?", id);
    }



}
