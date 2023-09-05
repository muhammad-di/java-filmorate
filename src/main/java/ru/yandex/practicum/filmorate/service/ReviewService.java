package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ru.yandex.practicum.filmorate.exception.*;

import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.review.dao.ReviewStorage;

import java.util.List;

@Slf4j
@Service
public class ReviewService {

    private final ReviewStorage storage;

    @Autowired
    public ReviewService(ReviewStorage storage) {
        this.storage = storage;
    }

    public Review create(Review review)
            throws FilmDoesNotExistException, UserDoesNotExistException, InvalidReviewPropertiesException {
        return storage.create(review);
    }

    public Review update(Review review) {
        return storage.update(review);
    }

    public Review findById(Long id) throws ReviewDoesNotExistException {
        Review review = storage.findById(id);
        if (review == null) {
            throw new ReviewDoesNotExistException("Director with such id {" + id + "} does not exist", 404);
        }
        return review;
    }

    public void deleteById(Long id) {
        storage.deleteById(id);
    }

    public List<Review> findAll(Long filmId, Long count) {
        return storage.findAll(filmId, count);
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
