package ru.yandex.practicum.filmorate.exeption;




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


    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public String toString() {
        return this.errorCode + " : " + this.getErrorMessage();
    }
}
