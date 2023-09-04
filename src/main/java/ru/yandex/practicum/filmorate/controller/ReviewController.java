package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.*;

import ru.yandex.practicum.filmorate.exception.FilmDoesNotExistException;
import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;
import ru.yandex.practicum.filmorate.exception.InvalidReviewPropertiesException;
import ru.yandex.practicum.filmorate.exception.UserDoesNotExistException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.validation.Valid;

import java.util.List;

import static ru.yandex.practicum.filmorate.Constants.MIN_ID;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public Review create(@RequestBody Review review)
            throws InvalidReviewPropertiesException, FilmDoesNotExistException, UserDoesNotExistException {
        return reviewService.create(review);
    }

    @GetMapping
    public List<Review> findAll(@RequestParam(required = false) Long filmId,
                                      @RequestParam(required = false) Long count) {
        return reviewService.findAll(filmId, count);
    }

    @GetMapping("/{id}")
    public Review findById(@PathVariable Long id) {
        if (id < MIN_ID) {
            throw new IncorrectParameterException("id");
        }
        return reviewService.findById(id);
    }

    @PutMapping
    public Review update(@Valid @RequestBody Review review) {
        return reviewService.update(review);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Long id, @PathVariable Long userId) {
        reviewService.addLike(id, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public void addDislike(@PathVariable Long id, @PathVariable Long userId) {
        reviewService.addDislike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable Long id, @PathVariable Long userId) {
        reviewService.removeLike(id, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public void removeDislike(@PathVariable Long id, @PathVariable Long userId) {
        reviewService.removeDislike(id, userId);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) {
        if (id < MIN_ID) {
            throw new IncorrectParameterException("id");
        }
        reviewService.deleteById(id);
    }
}
