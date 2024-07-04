package ru.yandex.practicum.filmorate.storage.review;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Qualifier
@AllArgsConstructor
public class ReviewRatingDbStorage implements ReviewRatingStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addLikeToReview(Integer reviewId, Integer userId) {
        addReactionToReview(reviewId, userId, true);
    }

    @Override
    public void addDislikeToReview(Integer reviewId, Integer userId) {
        addReactionToReview(reviewId, userId, false);
    }

    @Override
    public void deleteLikeFromReview(Integer reviewId, Integer userId) {
        deleteReactionFromReview(reviewId, userId, true);
    }

    @Override
    public void deleteDislikeFromReview(Integer reviewId, Integer userId) {
        deleteReactionFromReview(reviewId, userId, false);
    }


    private void addReactionToReview(Integer reviewId, Integer userId, Boolean isHelpful) {
        int useful = getUseful(reviewId);

        if (isReactionExist(reviewId, userId) && isHelpful(reviewId, userId) != isHelpful) {
            useful = useful - 2;
            String sql = "UPDATE review_rating SET is_helpful = ? WHERE review_id = ? AND user_id = ?";
            jdbcTemplate.update(sql, isHelpful, reviewId, userId);
        } else {
            String sql = "INSERT INTO review_rating (review_id, user_id, is_helpful) VALUES (?, ?, ?)";
            jdbcTemplate.update(sql, reviewId, userId, isHelpful);
            useful = useful + (isHelpful ? 1 : -1);
        }

        setUseful(reviewId, useful);
    }

    private void deleteReactionFromReview(Integer reviewId, Integer userId, Boolean isHelpful) {
        String sql = "DELETE FROM review_rating WHERE review_id = ? AND user_id = ?";

        jdbcTemplate.update(sql, reviewId, userId);

        int useful = getUseful(reviewId) + (isHelpful ? -1 : 1);
        setUseful(reviewId, useful);
    }

    private int getUseful(int reviewId) {
        String sql = "SELECT useful FROM reviews WHERE review_id = ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, reviewId);
    }

    private void setUseful(int reviewId, int useful) {
        String sqlForReview = "UPDATE reviews SET useful = ? WHERE review_id = ?";
        jdbcTemplate.update(sqlForReview, useful, reviewId);
    }

    private boolean isReactionExist(int reviewId, int userId) {
        String checkSql = "SELECT COUNT(*) FROM review_rating WHERE review_id = ? AND user_id = ?";
        Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class, reviewId, userId);
        return count != null && count > 0;
    }

    private boolean isHelpful(Integer reviewId, Integer userId) {
        String sql = "SELECT is_helpful FROM review_rating WHERE review_id = ? AND user_id = ?";
        Boolean isHelpful = jdbcTemplate.queryForObject(sql, Boolean.class, reviewId, userId);
        return isHelpful != null && isHelpful;
    }
}
