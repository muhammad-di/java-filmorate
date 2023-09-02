package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ru.yandex.practicum.filmorate.exception.FilmDoesNotExistException;
import ru.yandex.practicum.filmorate.exception.InvalidReviewPropertiesException;
import ru.yandex.practicum.filmorate.exception.UserDoesNotExistException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.review.dao.ReviewStorage;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class ReviewService {

    private final ReviewStorage storage;

    @Autowired
    public ReviewService(ReviewStorage storage) {
        this.storage = storage;
    }

    public Review createReview(Review review)
            throws FilmDoesNotExistException, UserDoesNotExistException, InvalidReviewPropertiesException {
        return storage.createReview(review);
    }

    public Optional<Review> updateReview(Review review) {
        return storage.updateReview(review);
    }

    public Optional<Review> getReviewById(Long id) {
        return storage.getReviewById(id);
    }

    public void deleteReviewById(Long id) {
        storage.deleteReviewById(id);
    }

    public List<Review> getAllReviews(Long filmId, Long count) {
        return storage.getAllReviews(filmId, count);
    }

    public void addLike(Long reviewId, Long userId) {
        storage.addLike(reviewId, userId);
    }

    public void addDislike(Long reviewId, Long userId) {
        storage.addDislike(reviewId, userId);
    }

    public void removeLike(Long reviewId, Long userId) {
        storage.removeLike(reviewId, userId);
    }

    public void removeDislike(Long reviewId, Long userId) {
        storage.removeDislike(reviewId, userId);
    }
}
