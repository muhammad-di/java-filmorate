package ru.yandex.practicum.filmorate.exception;


public class FilmAlreadyExistException extends Exception {

    private int errorCode;
    private String errorMessage;

    public FilmAlreadyExistException(Throwable throwable) {
        super(throwable);
    }

    public FilmAlreadyExistException(String msg, Throwable throwable) {
        super(msg, throwable);
    }

    public FilmAlreadyExistException(String msg) {
        super(msg);
    }

    public FilmAlreadyExistException(String message, int errorCode) {
        super();
        this.errorCode = errorCode;
        this.errorMessage = message;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public String toString() {
        return this.errorCode + " : " + this.getErrorMessage();
    }
}
