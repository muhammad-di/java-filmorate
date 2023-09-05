package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Sorting;
import ru.yandex.practicum.filmorate.storage.director.dao.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.film.dao.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.dao.UserStorage;
import ru.yandex.practicum.filmorate.validation.FilmValidation;

import java.util.Collection;

import java.util.List;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage storage;
    private final UserStorage userStorage;
    private final DirectorStorage directorStorage;

    @Autowired
    public FilmService(FilmStorage storage, UserStorage userStorage, DirectorStorage directorStorage) {
        this.storage = storage;
        this.userStorage = userStorage;
        this.directorStorage = directorStorage;
    }

    public Collection<Film> findAll() {
        return storage.findAll();
    }

    public List<Film> findFilmBySearchByTitleOrDirector(String title, String by) throws IncorrectParameterException {
        boolean isDirectorCheck = false;
        boolean isTitleCheck = false;
        for (String value : by.split(",")) {
            if (value.equals("director")) {
                isDirectorCheck = true;
            }
            if (value.equals("title")) {
                isTitleCheck = true;
            }
        }
        if (!isDirectorCheck && !isTitleCheck) {
            throw new IncorrectParameterException("Не заполнен параметр поиска by");
        }
        return storage.findFilmBySearchByTitleOrDirector(title, isDirectorCheck, isTitleCheck);
    }

    public Film create(Film film) throws InvalidFilmPropertiesException, FilmAlreadyExistException {
        if (FilmValidation.validate(film)) {
            log.info("film validation error");
            throw new InvalidFilmPropertiesException("Invalid name of a film", 406);
        }
        if (storage.contains(film.getId())) {
            log.info("Film already exists error");
            throw new FilmAlreadyExistException("Film already exists", 409);
        }
        return storage.create(film);
    }

    public Film update(Film film) throws FilmDoesNotExistException, InvalidFilmPropertiesException {
        if (!storage.contains(film.getId())) {
            throw new FilmDoesNotExistException("Film with such id {" + film.getId() + "} does not exist", 404);
        }
        if (FilmValidation.validate(film)) {
            log.info("film validation error");
            throw new InvalidFilmPropertiesException("Invalid name if a film", 406);
        }
        return storage.update(film);
    }

    public void addLike(Long idOfFilm, Long idOfUser)
            throws FilmDoesNotExistException, UserDoesNotExistException {
        if (!storage.contains(idOfFilm)) {
            throw new FilmDoesNotExistException("Film " +
                    "with such id {" + idOfFilm + "} does not exist", 404);
        }
        if (!userStorage.contains(idOfUser)) {
            throw new UserDoesNotExistException("User " +
                    "with such id {" + idOfUser + "} does not exist", 404);
        }
        storage.addLike(idOfFilm, idOfUser);
    }

    public void deleteLike(Long idOfFilm, Long idOfUser)
            throws FilmDoesNotExistException, UserDoesNotExistException {
        if (!storage.contains(idOfFilm)) {
            throw new FilmDoesNotExistException("Film " +
                    "with such id {" + idOfFilm + "} does not exist", 404);
        }
        if (!userStorage.contains(idOfUser)) {
            throw new UserDoesNotExistException("User " +
                    "with such id {" + idOfUser + "} does not exist", 404);
        }
        storage.deleteLike(idOfFilm, idOfUser);
    }

    public Collection<Film> findMostPopularFilms(Integer count, Integer genreId, Integer year) {
        return storage.findMostPopularFilms(count, genreId, year);
    }

    public List<Film> findCommonFilms(Integer userId, Integer friendId) {
        return storage.findCommonFilms(userId, friendId);
    }

    public Film findById(Long id) throws FilmDoesNotExistException {
        Film film = storage.findById(id);
        if (film == null) {
            throw new FilmDoesNotExistException("Film " +
                    "with such id {" + id + "} does not exist", 404);
        }
        return film;
    }

    public void deleteById(Long id) throws FilmDoesNotExistException {
        if (!storage.contains(id)) {
            throw new FilmDoesNotExistException("Film " +
                    "with such id {" + id + "} does not exist", 404);
        }
        storage.deleteById(id);
    }

    public Collection<Film> findFilmsWithDirectorSorted(Long directorId, Sorting sortBy)
            throws DirectorDoesNotExistException {
        if (!directorStorage.contains(directorId)) {
            String message = String.format("Director with such id {%s} does not exist", directorId);
            throw new DirectorDoesNotExistException(message);
        }
        return storage.findSortedFilmsByDirectorId(directorId, sortBy);
    }
}
