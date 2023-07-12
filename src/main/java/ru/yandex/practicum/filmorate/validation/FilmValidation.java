package ru.yandex.practicum.filmorate.validation;

import org.springframework.util.StringUtils;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import java.time.Month;

public class FilmValidation {
    private static final int MAX_LENGTH_OF_FILM_DESCRIPTION = 200;
    private static final LocalDate MIN_DATE_OF_RELEASE = LocalDate.of(1895, Month.DECEMBER, 28);
    private static final Integer MIN_FILM_DURATION = 1;

    public static Boolean validate(final Film film) {
        return film == null
                || !StringUtils.hasText(film.getName())
                || film.getDescription().length() > MAX_LENGTH_OF_FILM_DESCRIPTION
                || film.getReleaseDate().isBefore(MIN_DATE_OF_RELEASE)
                || film.getDuration() < MIN_FILM_DURATION;
    }
}
