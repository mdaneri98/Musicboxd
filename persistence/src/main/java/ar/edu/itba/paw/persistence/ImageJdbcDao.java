package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.Image;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class ImageJdbcDao implements ImageDao {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    private static final RowMapper<Image> ROW_MAPPER = (rs, rowNum) -> new Image(
            rs.getLong("id"),
            rs.getBytes("content")
    );


    public ImageJdbcDao(final DataSource ds) {
        jdbcTemplate = new JdbcTemplate(ds);
        jdbcInsert = new SimpleJdbcInsert(ds)
                .withTableName("image")
                .usingGeneratedKeyColumns("id");
    }

    @Override
    public Optional<Image> findById(long imageId) {
        return jdbcTemplate.query(
                "SELECT * FROM image WHERE id = ?",
                new Object[]{imageId},
                new int[]{Types.BIGINT},
                ROW_MAPPER
        ).stream().findFirst();
    }

    @Override
    public Image save(byte[] bytes) {
        Map<String, byte[]> imageData = Map.of("content",bytes);
        Number generatedId = jdbcInsert.executeAndReturnKey(imageData);
        return new Image(generatedId.longValue(), bytes);
    }

    @Override
    public boolean update(long imageId, byte[] bytes) {
        return jdbcTemplate.update("UPDATE image SET content = ? WHERE id = ?", bytes, imageId) > 0;
    }

    @Override
    public boolean delete(long imageId) {
        return jdbcTemplate.update("DELETE FROM image WHERE id = ?", imageId) > 0;
    }

    @Override
    public boolean exists(long imageId) {
        return findById(imageId).isPresent();
    }
}