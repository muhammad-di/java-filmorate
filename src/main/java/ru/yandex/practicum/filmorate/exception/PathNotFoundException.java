package ru.yandex.practicum.filmorate.exception;

public class PathNotFoundException extends RuntimeException {
    public PathNotFoundException(String message) {
        super(message);
    }
}