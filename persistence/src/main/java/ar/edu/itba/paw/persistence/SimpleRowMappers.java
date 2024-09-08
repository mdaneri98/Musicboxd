package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.Album;
import ar.edu.itba.paw.models.Artist;
import ar.edu.itba.paw.models.User;
import org.springframework.jdbc.core.RowMapper;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class SimpleRowMappers {

    /*
     * Se puede hacer ya que es STATELESS, sin problemas de concurrencia.
     * Todas las queries van a mappear cada row exactamente igual.
     * */
    public static final RowMapper<User> USER_ROW_MAPPER = (rs, rowNum) -> new User(
            rs.getLong("id"),
            rs.getString("username"),
            rs.getString("email"),
            rs.getString("password"),
            rs.getString("name"),
            rs.getString("bio"),
            rs.getObject("created_at", LocalDateTime.class),
            rs.getObject("updated_at", LocalDateTime.class),
            rs.getBoolean("verified"),
            rs.getLong("img_id")
    );

    public static final RowMapper<Artist> ARTIST_ROW_MAPPER = (rs, rowNum) -> new Artist(
            rs.getLong("id"),
            rs.getString("name"),
            rs.getString("bio"),
            rs.getObject("created_at", LocalDate.class),
            rs.getObject("updated_at", LocalDate.class),
            rs.getLong("img_id")
    );
    public static final RowMapper<Album> ALBUM_ROW_MAPPER = (rs, rowNum) -> new Album(
            rs.getLong("id"),
            rs.getString("title"),
            rs.getString("genre"),
            rs.getObject("release_date", LocalDate.class),
            rs.getObject("created_at", LocalDate.class),
            rs.getObject("updated_at", LocalDate.class),
            rs.getLong("img_id"),
            rs.getLong("artist_id")
    );

}
