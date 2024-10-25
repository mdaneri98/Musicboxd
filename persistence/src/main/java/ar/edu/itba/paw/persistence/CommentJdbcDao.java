package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.Comment;
import ar.edu.itba.paw.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class CommentJdbcDao implements CommentDao {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    private final static RowMapper<Comment> ROW_MAPPER = (rs, rowNum) ->
            new Comment(rs.getLong("id"),
                    new User(
                            rs.getLong("user_id"),
                            rs.getString("username"),
                            rs.getString("user_name"),
                            rs.getLong("user_img_id"),
                            rs.getBoolean("verified"),
                            rs.getBoolean("moderator")
                    ),
                    rs.getLong("review_id"),
                    rs.getString("content"),
                    rs.getTimestamp("created_at").toLocalDateTime());

    @Autowired
    public CommentJdbcDao(final DataSource ds) {
        jdbcTemplate = new JdbcTemplate(ds);
        jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("comment")
                .usingGeneratedKeyColumns("id");
    }

    @Override
    public Optional<Comment> findById(long id) {
        return jdbcTemplate.query(
                "SELECT c.*, u.username, u.name AS user_name, u.img_id AS user_img_id, u.verified, u.moderator " +
                "FROM comment c " +
                "JOIN cuser u ON c.user_id = u.id " +
                "WHERE c.id = ?", ROW_MAPPER, id)
                .stream().findFirst();
    }

    @Override
    public List<Comment> findByReviewId(long reviewId) {
        return jdbcTemplate.query(
                "SELECT c.*, u.username, u.name AS user_name, u.img_id AS user_img_id, u.verified, u.moderator " +
                "FROM comment c " +
                "JOIN cuser u ON c.user_id = u.id " +
                "WHERE c.review_id = ? ORDER BY c.created_at DESC", ROW_MAPPER, reviewId);
    }

    @Override
    public Comment save(Comment comment) {
        final Map<String, Object> args = new HashMap<>();
        args.put("user_id", comment.getUser().getId());
        args.put("review_id", comment.getReviewId());
        args.put("content", comment.getContent());
        args.put("created_at", comment.getCreatedAt());

        final Number commentId = jdbcInsert.executeAndReturnKey(args);
        return new Comment(commentId.longValue(), comment.getUser(), comment.getReviewId(), comment.getContent(), comment.getCreatedAt());
    }

    @Override
    public void deleteById(long id) {
        jdbcTemplate.update("DELETE FROM comment WHERE id = ?", id);
    }
}
