package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;
import ru.yandex.practicum.filmorate.model.ReactionType;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/reviews")
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping
    public Review add(@RequestBody @Valid Review review) {
        log.info("Received a request to add a review");
        return reviewService.add(review);
    }

    @PutMapping
    public Review update(@RequestBody @Valid Review newReview) {
        log.info("Received a request to update a review");
        return reviewService.update(newReview);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Integer id) {
        log.info("Received a request to delete a review");
        reviewService.deleteById(id);
    }

    @GetMapping("/{id}")
    public Review getById(@PathVariable Integer id) {
        log.info(String.format("Received a request to get review with id %d", id));
        return reviewService.getById(id);
    }

    @GetMapping
    public List<Review> getAllReviewsForFilm(
            @RequestParam(defaultValue = "0") Integer filmId,
            @RequestParam(defaultValue = "10") @Positive(message = "Amount of reviews must be positive") Integer count) {
        log.info("Received a request to get all reviews.");
        return reviewService.getFilmReviews(filmId, count);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLikeToReview(@PathVariable Integer id, @PathVariable Integer userId) {
        log.info(String.format("Received a request to like review with id %d from user %d", id, userId));
        reviewService.addReactionToReview(id, userId, ReactionType.LIKE);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public void addDislikeToReview(@PathVariable Integer id, @PathVariable Integer userId) {
        log.info(String.format("Received a request to dislike review with id %d from user %d", id, userId));
        reviewService.addReactionToReview(id, userId, ReactionType.DISLIKE);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLikeFromReview(@PathVariable Integer id, @PathVariable Integer userId) {
        log.info(String.format("Received a request to delete like from review %d from user %d", id, userId));
        reviewService.deleteReactionFromReview(id, userId, ReactionType.LIKE);

    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public void deleteDislikeFromReview(@PathVariable Integer id, @PathVariable Integer userId) {
        log.info(String.format("Received a request to delete dislike from review %d from user %d", id, userId));
        reviewService.deleteReactionFromReview(id, userId, ReactionType.DISLIKE);
    }

}
