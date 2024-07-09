package ru.yandex.practicum.filmorate.storage.review;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.mapper.ReviewRowMapper;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Slf4j
@Component
@Qualifier
@AllArgsConstructor
public class ReviewDbStorage implements ReviewStorage {
    private final JdbcTemplate jdbcTemplate;
    private final ReviewRowMapper reviewRowMapper;

    @Override
    public Review add(Review review) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        String sql = "INSERT INTO reviews (content, is_positive, user_id, film_id, useful) " +
                "VALUES (?, ?, ?, ?, ?)";

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, review.getContent());
            ps.setBoolean(2, review.getIsPositive());
            ps.setInt(3, review.getUserId());
            ps.setInt(4, review.getFilmId());
            ps.setInt(5, review.getUseful());
            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();
        if (key != null) {
            review.setReviewId(key.intValue());
        } else {
            throw new RuntimeException("Failed to add review");
        }

        log.info(String.format("Review from user %d was added to film %d.", review.getUserId(), review.getFilmId()));
        return review;
    }

    @Override
    public Review update(Review newReview) {
        String sql = "UPDATE reviews SET content = ?, is_positive = ?, useful = ? " +
                "WHERE review_id = ?";

        int id = newReview.getReviewId();
        Review oldReview = getById(id);
        newReview.setUserId(oldReview.getUserId());
        newReview.setFilmId(oldReview.getFilmId());

        jdbcTemplate.update(sql, newReview.getContent(), newReview.getIsPositive(),
                newReview.getUseful(), id);

        log.info(String.format("Review with id %d was updated", id));
        return newReview;
    }

    @Override
    public void deleteById(Integer reviewId) {
        String sql = "DELETE FROM reviews WHERE review_id = ? ";
        jdbcTemplate.update(sql, reviewId);
    }

    @Override
    public boolean isExists(int reviewId) {
        String sql = "SELECT COUNT(*) FROM reviews WHERE review_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, reviewId);
        return count != null && count > 0;
    }

    @Override
    public Review getById(Integer id) {
        String sql = "SELECT * FROM reviews WHERE review_id = ? LIMIT 1";
        Review review = jdbcTemplate.queryForObject(sql, reviewRowMapper, id);
        return review;
    }

    @Override
    public List<Review> getAll(Integer count) {
        String sql = "SELECT * FROM reviews ORDER BY useful DESC LIMIT ?";
        return jdbcTemplate.query(sql, reviewRowMapper, count);
    }

    @Override
    public List<Review> getFilmReviews(Integer filmId, Integer count) {
        String sql = "SELECT * FROM reviews WHERE film_id = ? ORDER BY useful DESC LIMIT ?";
        return jdbcTemplate.query(sql, reviewRowMapper, filmId, count);
    }
}

