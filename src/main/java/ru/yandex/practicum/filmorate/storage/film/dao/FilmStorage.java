package ru.yandex.practicum.filmorate.storage.film.dao;

import ru.yandex.practicum.filmorate.model.Film;


import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface FilmStorage {
    Collection<Film> findAll();

    Film create(Film film);

    Film update(Film film);

    void addLike(Long idOfFilm, Long idOfUser);

    void deleteLike(Long id, Long idOfFriend);

    Collection<Film> getMostPopularFilms(Integer count, Integer genreId, Integer year);

    boolean containsFilm(Long idOfFilm);

    Film getFilmById(Long id);

    Set<Long> getIdLikedFilmsByUser(Long id);

    void deleteFilmById(Long id);

    List<Film> getCommonFilms(Integer userId, Integer friendId);
}
