package ru.yandex.practicum.filmorate.storage.film.dao;

import ru.yandex.practicum.filmorate.model.Film;


import java.util.Collection;

public interface FilmStorage {
    Collection<Film> findAll();

    Film create(Film film);

    Film update(Film film);

    void addLike(Long idOfFilm, Long idOfUser);

    void deleteLike(Long id, Long idOfFriend);

    Collection<Film> getMostPopularFilms(Integer count);

    boolean containsFilm(Long idOfFilm);

    Film getFilmById(Long id);
}