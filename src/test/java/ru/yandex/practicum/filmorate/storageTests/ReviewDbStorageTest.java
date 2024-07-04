package ru.yandex.practicum.filmorate.storageTests;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ReviewDbStorageTest {
    private final ReviewStorage reviewStorage;

    @Test
    @Sql(scripts = {"/test-review-data.sql"})
    @DirtiesContext
    public void shouldAddReviewTest() {
        Review review = Review.builder()
                .content("super.")
                .isPositive(true)
                .userId(1)
                .filmId(1)
                .build();

        reviewStorage.add(review);
        Review receivedReview = reviewStorage.getById(2);

        assertThat(receivedReview).hasFieldOrPropertyWithValue("content", "super.");
    }

    @Test
    @Sql(scripts = {"/test-review-data.sql"})
    @DirtiesContext
    public void shouldUpdateReviewTest() {
        Review newReview = Review.builder()
                .reviewId(1)
                .content("Not cool.")
                .isPositive(false)
                .userId(1)
                .filmId(1)
                .build();

        reviewStorage.update(newReview);

        Review updatedReview = reviewStorage.getById(1);
        assertThat(updatedReview).hasFieldOrPropertyWithValue("reviewId", 1);
        assertThat(updatedReview).hasFieldOrPropertyWithValue("content", "Not cool.");
        assertThat(updatedReview).hasFieldOrPropertyWithValue("isPositive", false);
    }

    @Test
    @Sql(scripts = {"/test-review-data.sql"})
    @DirtiesContext
    public void shouldGetAllReviewsForFilmTest() {
        Review review = Review.builder()
                .content("super.")
                .isPositive(true)
                .userId(2)
                .filmId(1)
                .build();

        reviewStorage.add(review);

        List<Review> reviews = reviewStorage.getFilmReviews(1, 10);
        assertEquals(2, reviews.size(), "Amount of reviews is incorrect.");
    }
}
