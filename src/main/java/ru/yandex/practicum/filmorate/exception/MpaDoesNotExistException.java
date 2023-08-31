package ru.yandex.practicum.filmorate.exception;


public class MpaDoesNotExistException extends Exception {

    private int errorCode;
    private String errorMessage;

    public MpaDoesNotExistException(Throwable throwable) {
        super(throwable);
    }

    public MpaDoesNotExistException(String msg, Throwable throwable) {
        super(msg, throwable);
    }

    public MpaDoesNotExistException(String msg) {
        super(msg);
    }

    public MpaDoesNotExistException(String message, int errorCode) {
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
