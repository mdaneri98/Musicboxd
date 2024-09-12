package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.Album;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Types;
import java.util.List;
import java.util.Optional;


@Repository
public class AlbumJdbcDao implements AlbumDao {

   private final JdbcTemplate jdbcTemplate;



   public AlbumJdbcDao(final DataSource ds) {
       this.jdbcTemplate = new JdbcTemplate(ds);
   }

    @Override
    public Optional<Album> findById(long id) {
        // Jamás concatener valores en una query("SELECT ... WHERE username = " + id).
        return jdbcTemplate.query("SELECT * FROM album JOIN artist ON album.artist_id = artist.id WHERE id = ?",
                new Object[]{id},
                new int[]{Types.BIGINT},
                SimpleRowMappers.ALBUM_ROW_MAPPER
        ).stream().findFirst();
    }

    @Override
    public List<Album> findAll() {
        return jdbcTemplate.query("SELECT * FROM album JOIN artist ON album.artist_id = artist.id", SimpleRowMappers.ALBUM_ROW_MAPPER);
    }


    @Override
    public List<Album> findByArtistId(long id) {
        return jdbcTemplate.query("SELECT * FROM album JOIN artist ON album.artist_id = artist.id WHERE artist_id = ?",
                new Object[]{id},
                new int[]{Types.BIGINT},
                SimpleRowMappers.ALBUM_ROW_MAPPER);
    }

    @Override
    public int save(Album album) {
        return jdbcTemplate.update(
                "INSERT INTO album (title, genre, release_date , img_id, artist_id) VALUES (?, ?, ?, ?, ?)",
                album.getTitle(),
                album.getGenre(),
                album.getReleaseDate(),
                album.getImgId(),
                album.getArtist().getId()
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
                album.getImgId(),
                album.getArtist().getId(),
                album.getId()
        );
    }

    @Override
    public int deleteById(long id) {
        return jdbcTemplate.update("DELETE FROM album WHERE id = ?", id);
    }

}
