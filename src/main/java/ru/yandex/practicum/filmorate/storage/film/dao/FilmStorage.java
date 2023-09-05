package ru.yandex.practicum.filmorate.storage.film.dao;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Sorting;


import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface FilmStorage {
    Collection<Film> findAll();

    Film create(Film film);

    Film update(Film film);

    void addLike(Long idOfFilm, Long idOfUser);

    void deleteLike(Long id, Long idOfFriend);

    Collection<Film> findMostPopularFilms(Integer count, Integer genreId, Integer year);

    Boolean contains(Long idOfFilm);

    Film findById(Long id);

    List<Film> findFilmBySearchByTitleOrDirector(String title, boolean isDirectorCheck, boolean isTitleCheck);

    void deleteById(Long id);

    List<Film> findFilmsByIds(Set<Long> idsFilmsForRecommendations);

    List<Film> findCommonFilms(Integer userId, Integer friendId);

    Collection<Film> findSortedFilmsByDirectorId(Long directorId, Sorting sortBy);
}
