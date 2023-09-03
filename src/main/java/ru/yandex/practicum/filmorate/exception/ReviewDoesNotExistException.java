package ru.yandex.practicum.filmorate.exception;

public class ReviewDoesNotExistException extends RuntimeException {

    private int errorCode;
    private String errorMessage;

    public ReviewDoesNotExistException(String message, int errorCode) {
        super();
        this.errorMessage = message;
        this.errorCode = errorCode;
    }

    public ReviewDoesNotExistException(String message) {
        super(message);
    }

    public ReviewDoesNotExistException() {
        super();
    }
}
