package ru.yandex.practicum.filmorate.storage.director.dao;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;


public interface DirectorStorage {
    Collection<Director> getAllDirectors();

    Director getDirectorById(Long id);

    Boolean containsDirector(Long id);

    Boolean containsDirector(String name);

    Director createDirector(Director director);

    Director updateDirector(Director director);

    void deleteDirectorById(Long id);
}
