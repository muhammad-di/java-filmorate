package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeption.FilmAlreadyExistException;
import ru.yandex.practicum.filmorate.exeption.FilmDoesNotExistException;
import ru.yandex.practicum.filmorate.exeption.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exeption.InvalidFilmPropertiesException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.validation.FilmValidation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage storage;

    private final FilmIdGenerator filmIdGenerator;


    @Autowired
    public FilmService(FilmStorage storage) {
        this.storage = storage;
        this.filmIdGenerator = new FilmIdGenerator();
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
        if (film.getId() == 0) {
            film.setId(filmIdGenerator.getNextFreeId());
        }
        return storage.create(film);
    }

    public Film update(Film film) throws FilmDoesNotExistException, InvalidFilmPropertiesException {
        if (!storage.containsFilm(film.getId())) {
            throw new FilmDoesNotExistException("Film with such id does not exist");
        }
        if (FilmValidation.validate(film)) {
            log.info("film validation error");
            throw new InvalidFilmPropertiesException("Invalid name if a film", 406);
        }
        return storage.update(film);
    }

    public void addLike(int idOfFilm, int idOfUser) {
        storage.addLike(idOfFilm, idOfUser);
    }

    public void deleteLike(int idOfFilm, int idOfUse) {
        storage.deleteLike(idOfFilm, idOfUse);
    }

    public Collection<Film> getMostPopularFilms(Integer count) {
        return storage.getMostPopularFilms(count);
    }

    public Film getFilmById(Integer id) {
        Film film = storage.getFilmById(id);
        if (film == null) {
            throw new FilmNotFoundException("Film with such id does not exist");
        }
        return film;
    }

    private static final class FilmIdGenerator {
        private int nextFreeId = 1;

        private int getNextFreeId() {
            return nextFreeId++;
        }
    }
}
