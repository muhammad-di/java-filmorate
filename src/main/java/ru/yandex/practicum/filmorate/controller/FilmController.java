package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.*;

import static ru.yandex.practicum.filmorate.Constants.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/films")
public class FilmController {
    private static final String DEFAULT_MOST_FAVORITE_FILMS_COUNT = "10";
    private final FilmService filmService;

    @GetMapping
    public Collection<Film> findAll() {
        return filmService.findAll();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film)
            throws InvalidFilmPropertiesException, FilmAlreadyExistException {
        return filmService.create(film);
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film)
            throws FilmDoesNotExistException, InvalidFilmPropertiesException {
        return filmService.update(film);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Long id, @PathVariable Long userId)
            throws FilmDoesNotExistException, UserDoesNotExistException {
        if (id < MIN_ID || userId < MIN_ID) {
            String msg = String.format("Path \"/%d/like/%d\" does not exist", id, userId);
            throw new PathNotFoundException(msg);
        }
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteFriend(@PathVariable Long id, @PathVariable Long userId)
            throws FilmDoesNotExistException, UserDoesNotExistException {
        if (id < MIN_ID || userId < MIN_ID) {
            String msg = String.format("Path \"/%d/like/%d\" does not exist", id, userId);
            throw new PathNotFoundException(msg);
        }
        filmService.deleteLike(id, userId);
    }

    @GetMapping("/common")
    public Collection<Film> getCommonFilms(
            @RequestParam Integer userId,
            @RequestParam Integer friendId) {
        return filmService.findCommonFilms(userId, friendId);
    }

    @GetMapping("/popular")
    public Collection<Film> getPopularFilms(
            @RequestParam(defaultValue = DEFAULT_MOST_FAVORITE_FILMS_COUNT) Integer count,
            @RequestParam(required = false) Integer genreId,
            @RequestParam(required = false) Integer year
    ) {
        return filmService.findMostPopularFilms(count, genreId, year);
    }

    @GetMapping("/search")
    public List<Film> getFilmsBySearch(
            @RequestParam() String query,
            @RequestParam() String by
    ) throws IncorrectParameterException {
        return filmService.findFilmBySearchByTitleOrDirector(query, by);
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable Long id) throws FilmDoesNotExistException {
        if (id < MIN_ID) {
            throw new IncorrectParameterException("id");
        }
        return filmService.findById(id);
    }

    @DeleteMapping("/{filmId}")
    public void deleteFilmById(@PathVariable Long filmId) throws FilmDoesNotExistException {
        if (filmId < MIN_ID) {
            throw new IncorrectParameterException("id");
        }
        filmService.deleteById(filmId);
    }

    @GetMapping("/director/{directorId}")
    public Collection<Film> getFilmsWithDirectorSorted(@PathVariable Long directorId,
                                                       @RequestParam String sortBy)
            throws DirectorDoesNotExistException {
        if (directorId < MIN_ID) {
            throw new IncorrectParameterException("directorId");
        }
        if (!StringUtils.hasText(sortBy)) {
            throw new IncorrectParameterException("sortBy");
        }
        return filmService.findFilmsWithDirectorSorted(directorId, sortBy);
    }
}
