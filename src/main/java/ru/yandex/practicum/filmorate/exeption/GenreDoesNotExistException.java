package ru.yandex.practicum.filmorate.exeption;


public class GenreDoesNotExistException extends Exception {

    private int errorCode;
    private String errorMessage;

    public GenreDoesNotExistException(Throwable throwable) {
        super(throwable);
    }

    public GenreDoesNotExistException(String msg, Throwable throwable) {
        super(msg, throwable);
    }

    public GenreDoesNotExistException(String msg) {
        super(msg);
    }

    public GenreDoesNotExistException(String message, int errorCode) {
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
