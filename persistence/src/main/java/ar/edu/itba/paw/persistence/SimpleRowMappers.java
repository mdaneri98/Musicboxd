package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.Album;
import ar.edu.itba.paw.Artist;
import org.springframework.jdbc.core.RowMapper;

import java.time.LocalDate;

public class SimpleRowMappers {

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
