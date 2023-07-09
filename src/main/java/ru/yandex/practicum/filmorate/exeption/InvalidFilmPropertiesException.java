package ru.yandex.practicum.filmorate.exeption;

public class InvalidFilmPropertiesException extends Exception {

    private int errorCode;
    private String errorMessage;

    public InvalidFilmPropertiesException(Throwable throwable) {
        super(throwable);
    }

    public InvalidFilmPropertiesException(String msg, Throwable throwable) {
        super(msg, throwable);
    }

    public InvalidFilmPropertiesException(String msg) {
        super(msg);
    }

    public InvalidFilmPropertiesException(String message, int errorCode) {
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
