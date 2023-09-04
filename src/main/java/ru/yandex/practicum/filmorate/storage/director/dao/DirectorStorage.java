package ru.yandex.practicum.filmorate.storage.director.dao;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;


public interface DirectorStorage {
    Collection<Director> findAll();

    Director findById(Long id);

    Boolean contains(Long id);

    Boolean contains(Director director);

    Director create(Director director);

    Director update(Director director);

    void deleteById(Long id);
}
