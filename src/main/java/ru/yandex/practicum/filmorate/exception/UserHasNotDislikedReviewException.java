package ru.yandex.practicum.filmorate.exception;

public class UserHasNotDislikedReviewException extends RuntimeException {
    private int errorCode;
    private String errorMessage;

    public UserHasNotDislikedReviewException(String message, int errorCode) {
        super();
        this.errorMessage = message;
        this.errorCode = errorCode;
    }

    public UserHasNotDislikedReviewException(String message) {
        super(message);
    }

    public UserHasNotDislikedReviewException() {
        super();
    }
}
