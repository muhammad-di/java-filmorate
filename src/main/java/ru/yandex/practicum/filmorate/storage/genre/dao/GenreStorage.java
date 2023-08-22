package ru.yandex.practicum.filmorate.storage.genre.dao;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;

public interface GenreStorage {
    Collection<Genre> findAll();

    Genre getGenreById(Integer id);

    boolean containsGenre(Integer idOfGenre);
}
