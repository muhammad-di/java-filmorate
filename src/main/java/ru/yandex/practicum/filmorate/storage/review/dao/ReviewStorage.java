package ru.yandex.practicum.filmorate.storage.review.dao;

import ru.yandex.practicum.filmorate.exception.FilmDoesNotExistException;
import ru.yandex.practicum.filmorate.exception.InvalidReviewPropertiesException;
import ru.yandex.practicum.filmorate.exception.UserDoesNotExistException;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewStorage {
    Review createReview(Review review) throws FilmDoesNotExistException,
            UserDoesNotExistException, InvalidReviewPropertiesException;

    Review updateReview(Review review);

    Review getReviewById(Long id);

    void deleteReviewById(Long id);

    List<Review> getAllReviews(Long filmId, Long count);

    void addLike(Long reviewId, Long userId);

    void addDislike(Long reviewId, Long userId);

    void removeLike(Long reviewId, Long userId);

    void removeDislike(Long reviewId, Long userId);
}
