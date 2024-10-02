package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.Album;
import ar.edu.itba.paw.models.Artist;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
        return jdbcTemplate.query("SELECT album.id AS album_id, title, genre, release_date, album.created_at, album.updated_at, album.img_id AS album_img_id, artist.id AS artist_id, name, artist.img_id AS artist_img_id FROM album JOIN artist ON album.artist_id = artist.id WHERE album.id = ?",
                new Object[]{id},
                new int[]{Types.BIGINT},
                SimpleRowMappers.ALBUM_ROW_MAPPER
        ).stream().findFirst();
    }

    @Override
    public List<Album> findAll() {
        return jdbcTemplate.query("SELECT album.id AS album_id, title, genre, release_date, album.created_at, album.updated_at, album.img_id AS album_img_id, artist.id AS artist_id, name, artist.img_id AS artist_img_id FROM album JOIN artist ON album.artist_id = artist.id", SimpleRowMappers.ALBUM_ROW_MAPPER);
    }

    @Override
    public List<Album> findByTitleContaining(String sub) {
        // SQL para seleccionar todos los ids que coinciden con el título
        String sql = "SELECT id FROM album WHERE title ILIKE ?";

        // Obtenemos la lista de ids
        List<Integer> ids = jdbcTemplate.queryForList(sql, new Object[]{"%" + sub + "%"}, Integer.class);

        // Buscamos los álbumes correspondientes a cada id
        List<Album> albums = new ArrayList<>();
        for (Integer id : ids) {
            Optional<Album> album = this.findById(id);
            album.ifPresent(albums::add);
        }

        return albums;
    }

    @Override
    public List<Album> findByArtistId(long id) {
        return jdbcTemplate.query("SELECT album.id AS album_id, title, genre, release_date, album.created_at, album.updated_at, album.img_id AS album_img_id, artist.id AS artist_id, name, artist.img_id AS artist_img_id FROM album JOIN artist ON album.artist_id = artist.id WHERE artist_id = ?",
                new Object[]{id},
                new int[]{Types.BIGINT},
                SimpleRowMappers.ALBUM_ROW_MAPPER);
    }

    @Override
    public long save(Album album) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        int result = jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO album (title, genre, release_date , img_id, artist_id) VALUES (?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, album.getTitle());
            ps.setString(2, album.getGenre());
            ps.setObject(3, album.getReleaseDate());
            ps.setLong(4, album.getImgId());
            ps.setLong(5, album.getArtist().getId());
            return ps;
        }, keyHolder);
        Map<String, Object> keys = keyHolder.getKeys();
        if (keys != null && keys.containsKey("id")) {
            return ((Number) keys.get("id")).longValue();
        } else {
            throw new IllegalStateException("Failed to insert album or generate key.");
        }
    }

    @Override
    public int update(Album album) {
        return jdbcTemplate.update(
                "UPDATE album SET title = ?, genre = ?, release_date = ?, created_at = ?, updated_at = ?, img_id = ?, artist_id = ? WHERE id = ?",
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
