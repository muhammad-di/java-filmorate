package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;


import java.util.Collection;

public interface FilmStorage {
    Collection<Film> findAll();

    Film create(Film film);

    Film update(Film film);

    void addLike(int idOfFilm, int idOfUser);

    void deleteLike(int id, int idOfFriend);

    Collection<Film> getMostPopularFilms(Integer count);

    boolean containsFilm(int idOfFilm);

    Film getFilmById(Integer id);
}
