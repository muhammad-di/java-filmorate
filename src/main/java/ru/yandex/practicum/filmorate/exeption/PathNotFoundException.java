package ru.yandex.practicum.filmorate.exeption;

public class PathNotFoundException extends RuntimeException {
    public PathNotFoundException(String message){
        super(message);
    }
}