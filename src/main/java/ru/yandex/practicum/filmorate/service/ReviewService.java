package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.EventOperation;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.ReactionType;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.review.ReviewRatingStorage;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewStorage reviewStorage;
    private final ReviewRatingStorage reviewRatingStorage;
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
    private final EventService eventService;

    public Review add(Review review) {
        checkUserId(review.getUserId());
        checkFilmId(review.getFilmId());

        Review filmReview = reviewStorage.add(review);

        eventService.createEvent(filmReview.getUserId(), EventType.REVIEW, EventOperation.ADD, filmReview.getReviewId());
        return filmReview;
    }

    public Review update(Review newReview) {
        checkReviewId(newReview.getReviewId());
        checkUserId(newReview.getUserId());
        checkFilmId(newReview.getFilmId());
        Review filmReview = reviewStorage.update(newReview);

        eventService.createEvent(newReview.getUserId(), EventType.REVIEW, EventOperation.UPDATE, filmReview.getReviewId());
        return filmReview;
    }

    public void deleteById(Integer reviewId) {
        checkReviewId(reviewId);
        eventService.createEvent(reviewStorage.getById(reviewId).getUserId(), EventType.REVIEW, EventOperation.REMOVE, reviewId);
        reviewStorage.deleteById(reviewId);
    }

    public Review getById(Integer id) {
        checkReviewId(id);
        return reviewStorage.getById(id);
    }

    public List<Review> getFilmReviews(Integer filmId, Integer count) {
        if (filmId == 0) {
            return reviewStorage.getAll(count);
        }
        checkFilmId(filmId);
        return reviewStorage.getFilmReviews(filmId, count);
    }

    public void addReactionToReview(Integer reviewId, Integer userId, ReactionType reactionType) {
        checkReviewId(reviewId);
        checkUserId(userId);

        if (reactionType == ReactionType.LIKE) {
            reviewRatingStorage.addLikeToReview(reviewId, userId);
        } else if (reactionType == ReactionType.DISLIKE) {
            reviewRatingStorage.addDislikeToReview(reviewId, userId);
        }
    }

    public void deleteReactionFromReview(Integer reviewId, Integer userId, ReactionType reactionType) {
        checkReviewId(reviewId);
        checkUserId(userId);

        if (reactionType == ReactionType.LIKE) {
            reviewRatingStorage.deleteLikeFromReview(reviewId, userId);
        } else if (reactionType == ReactionType.DISLIKE) {
            reviewRatingStorage.deleteDislikeFromReview(reviewId, userId);
        }
    }

    private void checkUserId(Integer userId) {
        if (userId == null) {
            throw new ValidationException(String.format("User with ID %d does not exist.", userId));
        }
        if (!userStorage.isExists(userId)) {
            log.warn(String.format("There is no user with id %d", userId));
            throw new NotFoundException(String.format("User with ID %d does not exist.", userId));
        }
    }

    private void checkReviewId(int reviewId) {
        if (!reviewStorage.isExists(reviewId)) {
            log.warn(String.format("There is no review with id %d", reviewId));
            throw new NotFoundException(String.format("Review with ID %d does not exist.", reviewId));
        }
    }

    private void checkFilmId(Integer filmId) {
        if (filmId == null) {
            throw new ValidationException(String.format("Film with ID %d does not exist.", filmId));
        }
        if (!filmStorage.isExists(filmId)) {
            log.warn(String.format("There is no film with id %d", filmId));
            throw new NotFoundException(String.format("Film with ID %d does not exist.", filmId));
        }
    }
}
