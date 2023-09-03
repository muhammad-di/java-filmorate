package ru.yandex.practicum.filmorate.storage.review.dao.impl;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.review.dao.ReviewStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Repository
@Primary
public class ReviewDbStorage implements ReviewStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ReviewDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Review makeReview(ResultSet resultSet, int rowNum) throws SQLException {
        return Review.builder()
                .reviewId(resultSet.getLong("review_id"))
                .content(resultSet.getString("content"))
                .isPositive(resultSet.getBoolean("is_positive"))
                .userId(resultSet.getLong("user_id"))
                .filmId(resultSet.getLong("film_id"))
                .useful(resultSet.getLong("useful"))
                .build();
    }

    public Long makeUserIds(ResultSet resultSet, int rowNum) throws SQLException {
        return resultSet.getLong("user_id");
    }

    @Override
    public Review createReview(Review review)
            throws FilmDoesNotExistException, UserDoesNotExistException {
        review.setUseful(0L);

        this.reviewIsValid(review);

        String sqlQuery = "INSERT INTO reviews (content, is_positive, user_id, film_id, useful) " +
                "VALUES (?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"review_id"});
            stmt.setString(1, review.getContent());
            stmt.setBoolean(2, review.getIsPositive());
            stmt.setLong(3, review.getUserId());
            stmt.setLong(4, review.getFilmId());
            stmt.setLong(5, review.getUseful());
            return stmt;
        }, keyHolder);

        review.setReviewId(keyHolder.getKey().longValue());

        this.insertValuesToUserReviewsAndFilmReviews(review.getReviewId(),
                review.getUserId(), review.getFilmId());

        return review;
    }

    @Override
    public Review updateReview(Review review) {
        List<Long> reviewIds = this.getAllReviews()
                .stream()
                .map(review1 -> review.getReviewId())
                .collect(Collectors.toList());

        if (!reviewIds.contains(review.getReviewId())) {
            throw new ReviewDoesNotExistException("Review does not exist.");
        }

        if (review.getReviewId() == null) {
            throw new IncorrectParameterException("Review id can't be null.");
        }

        String sqlQuery = "UPDATE reviews SET" +
                " content = ?, is_positive = ? WHERE review_id = ?";

        jdbcTemplate.update(sqlQuery,
                review.getContent(),
                review.getIsPositive(),
                review.getReviewId()
        );

        return this.getReviewById(review.getReviewId());
    }

    @Override
    public Review getReviewById(Long id) {
        SqlRowSet reviewRow = jdbcTemplate
                .queryForRowSet("SELECT * FROM reviews WHERE review_id = " + id);

        if (reviewRow.next()) {
            Review review = Review.builder()
                    .reviewId(reviewRow.getLong("review_id"))
                    .content(reviewRow.getString("content"))
                    .isPositive(reviewRow.getBoolean("is_positive"))
                    .userId(reviewRow.getLong("user_id"))
                    .filmId(reviewRow.getLong("film_id"))
                    .useful(reviewRow.getLong("useful"))
                    .build();

            log.info("Найден отзыв с id: {}.", review.getReviewId());

            return review;
        } else {
            log.info("Отзыв с id: {} не найден.", id);
            throw new ReviewDoesNotExistException();
        }
    }

    @Override
    public void deleteReviewById(Long id) {
        List<Long> reviewIds = this.getAllReviews()
                .stream()
                .map(review1 -> this.getReviewById(id).getReviewId())
                .collect(Collectors.toList());

        if (!reviewIds.contains(id)) {
            throw new ReviewDoesNotExistException("Review does not exist.");
        }

        String sqlQuery = "DELETE FROM reviews WHERE review_id = ?";

        jdbcTemplate.update(sqlQuery, id);
    }

    @Override
    public List<Review> getAllReviews(Long filmId, Long count) {

        String getAllReviewsByFilmId = "SELECT * " +
                "                       FROM reviews " +
                "                       WHERE film_id = " + filmId +
                "                       ORDER BY useful DESC";

        String getAllReviewsByFilmIdAndCount = "SELECT * " +
                "                               FROM reviews " +
                "                               WHERE film_id = " + filmId +
                "                               ORDER BY useful DESC" +
                "                               LIMIT " + count;

        String getAllReviews = "SELECT * " +
                "               FROM reviews" +
                "               ORDER BY useful DESC";

        if (filmId != null && count != null) {
            return jdbcTemplate.query(getAllReviewsByFilmIdAndCount,
                    this::makeReview);
        }

        if (filmId != null) {
            return jdbcTemplate.query(getAllReviewsByFilmId, this::makeReview);
        }

        return jdbcTemplate.query(getAllReviews, this::makeReview);
    }

    @Override
    public void addLike(Long reviewId, Long userId) {
        String sqlQuery = "UPDATE reviews SET useful = useful + 1 WHERE review_id = ?";

        this.insertValuesToReviewsLikes(reviewId, userId);

        jdbcTemplate.update(sqlQuery, reviewId);
    }

    @Override
    public void addDislike(Long reviewId, Long userId) {
        String sqlQuery = "UPDATE reviews SET useful = useful - 1 WHERE review_id = ?";

        this.insertValuesToReviewsDislikes(reviewId, userId);

        jdbcTemplate.update(sqlQuery, reviewId);
    }

    @Override
    public void removeLike(Long reviewId, Long userId) {
        String sqlQuery = "UPDATE reviews SET useful = useful - 1 WHERE review_id = ?";

        if (this.hasUserLikedReview(reviewId, userId)) {
            this.deleteValuesFromReviewsLikes(userId);
            jdbcTemplate.update(sqlQuery, reviewId);
        } else {
            throw new UserHasNotLikedReviewException("User hasn't liked the review yet.");
        }
    }

    @Override
    public void removeDislike(Long reviewId, Long userId) {
        String sqlQuery = "UPDATE reviews SET useful = useful + 1 WHERE review_id = ?";

        if (this.hasUserDislikedReview(reviewId, userId)) {
            this.deleteValuesFromReviewsDislikes(userId);
            jdbcTemplate.update(sqlQuery, reviewId);
        } else {
            throw new UserHasNotDislikedReviewException("User hasn't disiked the review yet.");
        }
    }

    public List<Review> getAllReviews() {
        String sqlQuery = "SELECT * FROM reviews";

        return jdbcTemplate.query(sqlQuery, this::makeReview);
    }

    public Boolean reviewIsValid(Review review)
            throws UserDoesNotExistException, FilmDoesNotExistException {
        if (review.getContent() == null) {
            throw new ReviewValidationException("Content can't be null.");
        }
        if (review.getIsPositive() == null) {
            throw new ReviewValidationException("Review can't be null");
        }
        if (review.getUserId() < 0) {
            throw new UserDoesNotExistException("User id can't be negative.");
        }
        if (review.getFilmId() < 0) {
            throw new FilmDoesNotExistException("Film id can't be negative.");
        }
        return true;
    }

    public void insertValuesToReviewsLikes(Long reviewId, Long userId) {
        String sqlQuery = "INSERT INTO reviews_likes (review_id, user_id)" +
                " VALUES (?, ?)";

        jdbcTemplate.update(sqlQuery, reviewId, userId);
    }

    public void insertValuesToReviewsDislikes(Long reviewId, Long userId) {
        String sqlQuery = "INSERT INTO reviews_dislikes (review_id, user_id)" +
                " VALUES (?, ?)";

        jdbcTemplate.update(sqlQuery, reviewId, userId);
    }

    public void deleteValuesFromReviewsLikes(Long userId) {
        String sqlQuery = "DELETE FROM reviews_likes WHERE user_id = ?";

        jdbcTemplate.update(sqlQuery, userId);
    }

    public void deleteValuesFromReviewsDislikes(Long userId) {
        String sqlQuery = "DELETE FROM reviews_dislikes WHERE user_id = ?";

        jdbcTemplate.update(sqlQuery, userId);
    }

    public boolean hasUserLikedReview(Long reviewId, Long userId) {
        String sqlQuery = "SELECT user_id FROM reviews_likes WHERE review_id = " + reviewId;

        List<Long> userIds = jdbcTemplate.query(sqlQuery, this::makeUserIds);

        if (userIds.contains(userId)) {
            return true;
        }
        return false;
    }

    public boolean hasUserDislikedReview(Long reviewId, Long userId) {
        String sqlQuery = "SELECT user_id FROM reviews_dislikes WHERE review_id = " + reviewId;

        List<Long> userIds = jdbcTemplate.query(sqlQuery, this::makeUserIds);

        if (userIds.contains(userId)) {
            return true;
        }
        return false;
    }

    public void insertValuesToUserReviewsAndFilmReviews(Long reviewId, Long userId, Long filmId) {
        String insertIntoUserReviews = "INSERT INTO user_review (user_id, review_id)" +
                "VALUES (?, ?)";

        String insertIntoFilmReviews = "INSERT INTO film_review (film_id, review_id) " +
                "VALUES (?, ?)";

        jdbcTemplate.update(insertIntoUserReviews,
                userId, reviewId);

        jdbcTemplate.update(insertIntoFilmReviews,
                filmId, reviewId);
    }
}
