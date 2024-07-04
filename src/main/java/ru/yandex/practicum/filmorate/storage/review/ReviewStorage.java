package ru.yandex.practicum.filmorate.storage.review;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewStorage {
    Review add(Review review);

    Review update(Review newReview);

    void deleteById(Integer reviewId);

    boolean isExists(int filmId);

    Review getById(Integer id);

    List<Review> getAll(Integer count);

    List<Review> getFilmReviews(Integer filmId, Integer count);
}
