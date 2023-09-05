package ru.yandex.practicum.filmorate.exception;


public class DirectorAlreadyExistException extends Exception {

    private int errorCode;
    private String errorMessage;

    public DirectorAlreadyExistException(Throwable throwable) {
        super(throwable);
    }

    public DirectorAlreadyExistException(String msg, Throwable throwable) {
        super(msg, throwable);
    }

    public DirectorAlreadyExistException(String msg) {
        super(msg);
    }

    public DirectorAlreadyExistException(String message, int errorCode) {
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
