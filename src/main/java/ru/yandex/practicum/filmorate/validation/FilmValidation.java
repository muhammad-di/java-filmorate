package ru.yandex.practicum.filmorate.validation;

import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import java.time.Month;

public class FilmValidation {
    private final static int MAX_LENGTH_OF_FILM_DESCRIPTION = 200;
    private final static LocalDate MIN_DATE_OF_RELEASE = LocalDate.of(1895, Month.DECEMBER, 28);
    private final static Integer MIN_FILM_DURATION = 1;

    public static Boolean validate(final Film film) {
        return film == null
                || film.getName() == null
                || film.getName().isBlank()
                || film.getDescription().length() > MAX_LENGTH_OF_FILM_DESCRIPTION
                || film.getReleaseDate().isBefore(MIN_DATE_OF_RELEASE)
                || film.getDuration() < MIN_FILM_DURATION;
    }

}
