package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.director.dao.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.film.dao.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.dao.UserStorage;
import ru.yandex.practicum.filmorate.validation.FilmValidation;

import java.util.Collection;
import java.util.Collections;

import static ru.yandex.practicum.filmorate.Constants.LIKES_SORT;
import static ru.yandex.practicum.filmorate.Constants.YEAR_SORT;

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

    public Film create(Film film) throws InvalidFilmPropertiesException, FilmAlreadyExistException {
        if (FilmValidation.validate(film)) {
            log.info("film validation error");
            throw new InvalidFilmPropertiesException("Invalid name of a film", 406);
        }
        if (storage.containsFilm(film.getId())) {
            log.info("Film already exists error");
            throw new FilmAlreadyExistException("Film already exists", 409);
        }
        return storage.create(film);
    }

    public Film update(Film film) throws FilmDoesNotExistException, InvalidFilmPropertiesException {
        if (!storage.containsFilm(film.getId())) {
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
        if (!storage.containsFilm(idOfFilm)) {
            throw new FilmDoesNotExistException("Film " +
                    "with such id {" + idOfFilm + "} does not exist", 404);
        }
        if (!userStorage.containsUser(idOfUser)) {
            throw new UserDoesNotExistException("User " +
                    "with such id {" + idOfUser + "} does not exist", 404);
        }
        storage.addLike(idOfFilm, idOfUser);
    }

    public void deleteLike(Long idOfFilm, Long idOfUser)
            throws FilmDoesNotExistException, UserDoesNotExistException {
        if (!storage.containsFilm(idOfFilm)) {
            throw new FilmDoesNotExistException("Film " +
                    "with such id {" + idOfFilm + "} does not exist", 404);
        }
        if (!userStorage.containsUser(idOfUser)) {
            throw new UserDoesNotExistException("User " +
                    "with such id {" + idOfUser + "} does not exist", 404);
        }
        storage.deleteLike(idOfFilm, idOfUser);
    }

    public Collection<Film> getMostPopularFilms(Integer count) {
        return storage.getMostPopularFilms(count);
    }

    public Film getFilmById(Long id) throws FilmDoesNotExistException {
        if (!storage.containsFilm(id)) {
            throw new FilmDoesNotExistException("Film " +
                    "with such id {" + id + "} does not exist", 404);
        }
        return storage.getFilmById(id);
    }

    public void deleteFilmById(Long id) throws FilmDoesNotExistException {
        if (!storage.containsFilm(id)) {
            throw new FilmDoesNotExistException("Film " +
                    "with such id {" + id + "} does not exist", 404);
        }
        storage.deleteFilmById(id);
    }

    public Collection<Film> getFilmsWithDirectorSorted(Long directorId, String sort)
            throws DirectorDoesNotExistException {
        if (!directorStorage.containsDirector(directorId)) {
            String message = String.format("Director with such id {%s} does not exist", directorId);
            throw new DirectorDoesNotExistException(message);
        }
        if (sort.equals(LIKES_SORT)) {
            return storage.getFilmsWithDirectorIdSortedByLikes(directorId);
        }
        if (sort.equals(YEAR_SORT)) {
            return storage.getFilmsWithDirectorIdSortedByYear(directorId);
        }
        return Collections.emptyList();
    }
}