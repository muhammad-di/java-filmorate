package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exeption.FilmAlreadyExistException;
import ru.yandex.practicum.filmorate.exeption.InvalidFilmPropertiesException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.time.Month;
import java.util.*;

@RestController
@RequestMapping("/films")
public class FilmController {
    private final static int MAX_LENGTH_OF_FILM_DESCRIPTION = 200;
    private final Map<Integer, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> findAll() {
        return films.values();
    }

    @PostMapping
    public Film create(@RequestBody Film film) throws InvalidFilmPropertiesException, FilmAlreadyExistException {
        if (film == null
                || film.getName().isBlank()
                || film.getDescription().length() > MAX_LENGTH_OF_FILM_DESCRIPTION
                || film.getReleaseDate().isBefore(LocalDate.of(1895, Month.DECEMBER, 28))
                || film.getDuration() < 1) {
            throw new InvalidFilmPropertiesException("Invalid name of a film", 406);
        }
        if (films.containsKey(film.getId())) {
            throw new FilmAlreadyExistException("Film already exists", 409);
        }
        films.put(film.getId(), film);
        return films.get(film.getId());
    }

    @PutMapping
    public Film update(@RequestBody Film film) throws InvalidFilmPropertiesException {
        if (film == null
                || film.getName().isBlank()
                || film.getDescription().length() > MAX_LENGTH_OF_FILM_DESCRIPTION
                || film.getReleaseDate().isBefore(LocalDate.of(1895, Month.DECEMBER, 28))
                || film.getDuration() < 1) {
            throw new InvalidFilmPropertiesException("Invalid name if a film", 406);
        }
        films.put(film.getId(), film);
        return films.get(film.getId());
    }
}
