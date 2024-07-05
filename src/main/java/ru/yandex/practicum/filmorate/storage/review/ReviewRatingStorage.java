package ru.yandex.practicum.filmorate.storage.review;

public interface ReviewRatingStorage {
    void addLikeToReview(Integer reviewId, Integer userId);

    void addDislikeToReview(Integer reviewId, Integer userId);

    void deleteLikeFromReview(Integer reviewId, Integer userId);

    void deleteDislikeFromReview(Integer reviewId, Integer userId);
}
