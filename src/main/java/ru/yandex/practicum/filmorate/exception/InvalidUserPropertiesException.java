package ru.yandex.practicum.filmorate.exception;

public class InvalidUserPropertiesException extends Exception {

    private int errorCode;
    private String errorMessage;

    public InvalidUserPropertiesException(Throwable throwable) {
        super(throwable);
    }

    public InvalidUserPropertiesException(String msg, Throwable throwable) {
        super(msg, throwable);
    }

    public InvalidUserPropertiesException(String msg) {
        super(msg);
    }

    public InvalidUserPropertiesException(String message, int errorCode) {
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
