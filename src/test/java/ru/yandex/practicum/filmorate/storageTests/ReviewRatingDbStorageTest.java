package ru.yandex.practicum.filmorate.storageTests;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.storage.review.ReviewRatingStorage;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ReviewRatingDbStorageTest {
    private final ReviewRatingStorage reviewRatingStorage;
    private final ReviewStorage reviewStorage;

    @Test
    @Sql(scripts = {"/test-review-data.sql"})
    @DirtiesContext
    public void shouldAddAndDeleteLikeToReviewTest() {
        reviewRatingStorage.addLikeToReview(1, 1);
        assertThat(reviewStorage.getById(1)).hasFieldOrPropertyWithValue("useful", 1);

        reviewRatingStorage.deleteLikeFromReview(1, 1);
        assertThat(reviewStorage.getById(1)).hasFieldOrPropertyWithValue("useful", 0);
    }

    @Test
    @Sql(scripts = {"/test-review-data.sql"})
    @DirtiesContext
    public void shouldAddAndDeleteDislikeToReviewTest() {
        reviewRatingStorage.addDislikeToReview(1, 1);
        assertThat(reviewStorage.getById(1)).hasFieldOrPropertyWithValue("useful", -1);

        reviewRatingStorage.deleteDislikeFromReview(1, 1);
        assertThat(reviewStorage.getById(1)).hasFieldOrPropertyWithValue("useful", 0);
    }
}
