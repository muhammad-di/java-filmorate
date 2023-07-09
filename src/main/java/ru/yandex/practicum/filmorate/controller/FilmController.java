package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exeption.FilmAlreadyExistException;
import ru.yandex.practicum.filmorate.exeption.InvalidFilmPropertiesException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validation.FilmValidation;

import java.time.LocalDate;
import java.time.Month;
import java.util.*;

@RestController
@RequestMapping("/films")
public class FilmController {
    private final Map<Integer, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> findAll() {
        return films.values();
    }

    @PostMapping
    public Film create(@RequestBody Film film) throws InvalidFilmPropertiesException, FilmAlreadyExistException {
        if (FilmValidation.validate(film)) {
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
        if (FilmValidation.validate(film)) {
            throw new InvalidFilmPropertiesException("Invalid name if a film", 406);
        }
        films.put(film.getId(), film);
        return films.get(film.getId());
    }
}
