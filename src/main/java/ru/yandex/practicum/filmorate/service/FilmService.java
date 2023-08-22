package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeption.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.dao.FilmStorage;
import ru.yandex.practicum.filmorate.validation.FilmValidation;

import java.util.Collection;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage storage;

    @Autowired
    public FilmService(FilmStorage storage) {
        this.storage = storage;
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

    public void addLike(Long idOfFilm, Long idOfUser) {
        storage.addLike(idOfFilm, idOfUser);
    }

    public void deleteLike(Long idOfFilm, Long idOfUse) {
        storage.deleteLike(idOfFilm, idOfUse);
    }

    public Collection<Film> getMostPopularFilms(Integer count) {
        return storage.getMostPopularFilms(count);
    }

    public Film getFilmById(Long id) throws FilmDoesNotExistException {
        if (!storage.containsFilm(id)) {
            throw new FilmDoesNotExistException("Film with such id {" + id + "} does not exist", 404);
        }
        return storage.getFilmById(id);
    }
}
