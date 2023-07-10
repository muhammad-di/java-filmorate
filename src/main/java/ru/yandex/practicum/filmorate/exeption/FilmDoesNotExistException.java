package ru.yandex.practicum.filmorate.exeption;


public class FilmDoesNotExistException extends Exception {

    private int errorCode;
    private String errorMessage;

    public FilmDoesNotExistException(Throwable throwable) {
        super(throwable);
    }

    public FilmDoesNotExistException(String msg, Throwable throwable) {
        super(msg, throwable);
    }

    public FilmDoesNotExistException(String msg) {
        super(msg);
    }

    public FilmDoesNotExistException(String message, int errorCode) {
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
