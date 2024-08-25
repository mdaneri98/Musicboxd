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
            rs.getLong("artistId"),
            rs.getObject("release_at", LocalDate.class),
            rs.getObject("created_at", LocalDateTime.class),
            rs.getObject("updated_at", LocalDateTime.class)
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
                "INSERT INTO album (title, genre, artistId, release_at, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?)",
                album.getGenre(),
                album.getArtistId(),
                album.getReleaseAt(),
                album.getCreatedAt(),
                album.getUpdatedAt()
        );
    }

    @Override
    public int update(Album album) {
        return jdbcTemplate.update(
                "UPDATE album SET title = ?, genre = ?, artistId = ?, release_at = ?, created_at = ?, updated_at = ? WHERE id = ?",
                album.getGenre(),
                album.getArtistId(),
                album.getReleaseAt(),
                album.getCreatedAt(),
                album.getUpdatedAt(),
                album.getArtistId()
        );
    }

    @Override
    public int deleteById(long id) {
        return jdbcTemplate.update("DELETE FROM album WHERE id = ?", id);
    }



}
