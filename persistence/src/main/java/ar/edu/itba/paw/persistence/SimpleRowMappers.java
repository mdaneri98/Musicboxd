package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.*;
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
            rs.getLong("img_id"),
            rs.getBoolean("moderator"),
            rs.getInt("followers_amount"),
            rs.getInt("following_amount"),
            rs.getInt("review_amount")
    );

    public static final RowMapper<UserVerification> USER_VERIFICATION_ROW_MAPPER = (rs, rowNum) -> new UserVerification(
            rs.getLong("id"),
            rs.getLong("user_id"),
            rs.getString("code"),
            rs.getTimestamp("expire_date")
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
            rs.getLong("album.id"),
            rs.getString("title"),
            rs.getString("genre"),
            rs.getObject("release_date", LocalDate.class),
            rs.getObject("created_at", LocalDate.class),
            rs.getObject("updated_at", LocalDate.class),
            rs.getLong("album.img_id"),
            new Artist(
                    rs.getLong("artist.id"),
                    rs.getString("name"),
                    rs.getLong("artist.img_id")
            )
    );

    public static final RowMapper<Song> SONG_ROW_MAPPER = (rs, rowNum) -> new Song(
            rs.getLong("song.id"),
            rs.getString("song.title"),
            rs.getString("duration"),
            rs.getInt("track_number"),
            rs.getObject("song.created_at", LocalDate.class),
            rs.getObject("song.updated_at", LocalDate.class),
            new Album(
                    rs.getLong("album.id"),
                    rs.getString("album.title"),
                    rs.getLong("album.img_id"),
                    new Artist(
                            rs.getLong("artist.id"),
                            rs.getString("name"),
                            rs.getLong("artist.img_id")
                    )
            )
    );

}
