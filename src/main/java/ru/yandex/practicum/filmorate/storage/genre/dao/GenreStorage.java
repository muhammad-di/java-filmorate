package ru.yandex.practicum.filmorate.storage.genre.dao;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface GenreStorage {
    List<Genre> findAll();

    Genre getGenreById(Integer id);

    Boolean containsGenre(Integer idOfGenre);
}
