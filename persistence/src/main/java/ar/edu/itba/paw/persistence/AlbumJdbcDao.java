package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.Album;
import ar.edu.itba.paw.models.Artist;
import ar.edu.itba.paw.models.FilterType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Types;
import java.util.ArrayList;
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
        return jdbcTemplate.query("SELECT album.id AS album_id, title, genre, release_date, album.created_at, album.updated_at, album.img_id AS album_img_id, album.avg_rating AS avg_rating, album.rating_amount AS rating_amount, artist.id AS artist_id, name, artist.img_id AS artist_img_id FROM album JOIN artist ON album.artist_id = artist.id WHERE album.id = ?",
                new Object[]{id},
                new int[]{Types.BIGINT},
                SimpleRowMappers.ALBUM_ROW_MAPPER
        ).stream().findFirst();
    }

    @Override
    public List<Album> findAll() {
        return jdbcTemplate.query("SELECT album.id AS album_id, title, genre, release_date, album.created_at, album.updated_at, album.img_id AS album_img_id, album.avg_rating AS avg_rating, album.rating_amount AS rating_amount, artist.id AS artist_id, name, artist.img_id AS artist_img_id FROM album JOIN artist ON album.artist_id = artist.id", SimpleRowMappers.ALBUM_ROW_MAPPER);
    }

    @Override
    public List<Album> findPaginated(FilterType filterType, int limit, int offset) {
       String sql = "SELECT album.id AS album_id, title, genre, release_date, album.created_at, album.updated_at, album.img_id AS album_img_id, album.avg_rating AS avg_rating, album.rating_amount AS rating_amount, artist.id AS artist_id, name, artist.img_id AS artist_img_id "+
               "FROM album JOIN artist ON album.artist_id = artist.id " +
               filterType.getFilter() +
               "LIMIT ? OFFSET ?";

       return jdbcTemplate.query(sql, new Object[]{ limit, offset }, new int[]{ Types.BIGINT, Types.BIGINT }, SimpleRowMappers.ALBUM_ROW_MAPPER);
    }

    @Override
    public List<Album> findByTitleContaining(String sub) {
        // SQL para seleccionar todos los ids que coinciden con el título
        String sql = "SELECT album.id AS album_id, title, genre, release_date, album.created_at, album.updated_at, album.img_id AS album_img_id, album.avg_rating AS avg_rating, album.rating_amount AS rating_amount, artist.id AS artist_id, name, artist.img_id AS artist_img_id "+
                "FROM album JOIN artist ON album.artist_id = artist.id " +
                "WHERE title ILIKE ? LIMIT 10";

        return jdbcTemplate.query(sql, new Object[]{"%" + sub + "%"}, SimpleRowMappers.ALBUM_ROW_MAPPER);
    }

    @Override
    public List<Album> findByArtistId(long id) {
        return jdbcTemplate.query("SELECT album.id AS album_id, title, genre, release_date, album.created_at, album.updated_at, album.img_id AS album_img_id, album.avg_rating AS avg_rating, album.rating_amount AS rating_amount, artist.id AS artist_id, name, artist.img_id AS artist_img_id FROM album JOIN artist ON album.artist_id = artist.id WHERE artist_id = ?",
                new Object[]{id},
                new int[]{Types.BIGINT},
                SimpleRowMappers.ALBUM_ROW_MAPPER);
    }

    @Override
    public long save(Album album) {
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
    public void updateRating(long albumId, float newRating, int newRatingAmount) {
        final String sql = "UPDATE album SET avg_rating = ?, rating_amount = ? WHERE id = ?";
        jdbcTemplate.update(sql, newRating, newRatingAmount, albumId);
    }

    @Override
    public boolean hasUserReviewed(long userId, long albumId) {
        final String sql = "SELECT COUNT(*) FROM album_review ar JOIN review r ON ar.review_id = r.id WHERE r.user_id = ? AND ar.album_id = ?";
        int count = jdbcTemplate.queryForObject(sql, Integer.class, userId, albumId);
        return count > 0;
    }

    @Override
    public int deleteById(long id) {
        return jdbcTemplate.update("DELETE FROM album WHERE id = ?", id);
    }

}
