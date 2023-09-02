package ru.yandex.practicum.filmorate.exception;

public class UserHasNotLikedReviewException extends RuntimeException {
    private int errorCode;
    private String errorMessage;

    public UserHasNotLikedReviewException(String message, int errorCode) {
        super();
        this.errorMessage = message;
        this.errorCode = errorCode;
    }

    public UserHasNotLikedReviewException(String message) {
        super(message);
    }

    public UserHasNotLikedReviewException() {
        super();
    }
}
