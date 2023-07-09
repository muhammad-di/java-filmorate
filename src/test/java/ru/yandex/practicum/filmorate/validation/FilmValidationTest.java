package ru.yandex.practicum.filmorate.validation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.time.Month;

public class FilmValidationTest {

    private Film film;

    @Test
    public void shouldReturnTrueForFilmNull() {
        Boolean actual = FilmValidation.validate(null);
        assertTrue(actual);
    }

    @Test
    public void shouldReturnTrueForNameBlank() {
        film = Film.builder()
                .id(1)
                .name("  ")
                .description("Lucky Number Slevin (also known as The Wrong Man in Australia) is a 2006 neo-noir crime" +
                        " thriller film directed by Paul McGuigan")
                .releaseDate(LocalDate.of(2006, Month.APRIL, 7))
                .duration(110).build();
        Boolean actual = FilmValidation.validate(film);

        assertTrue(actual);
    }

    @Test
    public void shouldReturnTrueForNameEmpty() {
        film = Film.builder()
                .id(1)
                .name("")
                .description("Lucky Number Slevin (also known as The Wrong Man in Australia) is a 2006 neo-noir crime" +
                        " thriller film directed by Paul McGuigan")
                .releaseDate(LocalDate.of(2006, Month.APRIL, 7))
                .duration(110).build();
        Boolean actual = FilmValidation.validate(film);

        assertTrue(actual);
    }

    @Test
    public void shouldReturnTrueForNameNull() {
        film = Film.builder()
                .id(1)
                .name(null)
                .description("Lucky Number Slevin (also known as The Wrong Man in Australia) is a 2006 neo-noir crime" +
                        " thriller film directed by Paul McGuigan")
                .releaseDate(LocalDate.of(2006, Month.APRIL, 7))
                .duration(110).build();
        Boolean actual = FilmValidation.validate(film);

        assertTrue(actual);
    }

    @Test
    public void shouldReturnFalseForNameFiledIn() {
        film = Film.builder()
                .id(1)
                .name("Lucky number Slevin")
                .description("Lucky Number Slevin (also known as The Wrong Man in Australia) is a 2006 neo-noir crime" +
                        " thriller film directed by Paul McGuigan")
                .releaseDate(LocalDate.of(2006, Month.APRIL, 7))
                .duration(110).build();
        Boolean actual = FilmValidation.validate(film);

        assertFalse(actual);
    }

    @Test
    public void shouldReturnTrueForDescriptionLengthExceedingMaxAt201() {
        film = Film.builder()
                .id(1)
                .name("Lucky number Slevin")
                .description("Lucky Number Slevin (also known as The Wrong Man in Australia) is a 2006 neo-noir crime " +
                        "thriller film directed by Paul McGuigan and written by Jason Smilovic.[4][5] The film stars" +
                        " Josh Hartnett, Morgan")
                .releaseDate(LocalDate.of(2006, Month.APRIL, 7))
                .duration(110).build();
        Boolean actual = FilmValidation.validate(film);

        assertTrue(actual);
    }

    @Test
    public void shouldReturnTrueForDescriptionLengthExceedingMaxAt267() {
        film = Film.builder()
                .id(1)
                .name("Lucky number Slevin")
                .description("Lucky Number Slevin (also known as The Wrong Man in Australia) is a 2006 neo-noir crime" +
                        " thriller film directed by Paul McGuigan and written by Jason Smilovic.[4][5] The film stars" +
                        " Josh Hartnett, Morgan Freeman, Ben Kingsley, Lucy Liu, Stanley Tucci, and")
                .releaseDate(LocalDate.of(2006, Month.APRIL, 7))
                .duration(110).build();
        Boolean actual = FilmValidation.validate(film);

        assertTrue(actual);
    }

    @Test
    public void shouldReturnTrueForDescriptionLengthExceedingMaxAt317() {
        film = Film.builder()
                .id(1)
                .name("Lucky number Slevin")
                .description("Lucky Number Slevin (also known as The Wrong Man in Australia) is a 2006 neo-noir" +
                        " crime thriller film directed by Paul McGuigan and written by Jason Smilovic.[4][5] The" +
                        " film stars Josh Hartnett, Morgan Freeman, Ben Kingsley, Lucy Liu, Stanley Tucci, and" +
                        " Bruce Willis. It revolves around an innocent man dragged into the middle of a war being" +
                        " plotted by two of New York City's rival crime bosses.")
                .releaseDate(LocalDate.of(2006, Month.APRIL, 7))
                .duration(110).build();
        Boolean actual = FilmValidation.validate(film);

        assertTrue(actual);
    }

    @Test
    public void shouldReturnFalseForDescriptionLengthAt200() {
        film = Film.builder()
                .id(1)
                .name("Lucky number Slevin")
                .description("Lucky Number Slevin (also known as The Wrong Man in Australia) is a 2006 neo-noir crime" +
                        " thriller film directed by Paul McGuigan and written by Jason Smilovic.[4][5] The film stars " +
                        "Josh Hartnett, Morga")
                .releaseDate(LocalDate.of(2006, Month.APRIL, 7))
                .duration(110).build();
        Boolean actual = FilmValidation.validate(film);

        assertFalse(actual);
    }

    @Test
    public void shouldReturnFalseForDescriptionLengthAt150() {
        film = Film.builder()
                .id(1)
                .name("Lucky number Slevin")
                .description("Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget" +
                        " dolor. Aenean massa. Cum sociis natoque penatibus et magnis dis p")
                .releaseDate(LocalDate.of(2006, Month.APRIL, 7))
                .duration(110).build();
        Boolean actual = FilmValidation.validate(film);

        assertFalse(actual);
    }

    @Test
    public void shouldReturnFalseForDescriptionLengthAt10() {
        film = Film.builder()
                .id(1)
                .name("Lucky number Slevin")
                .description("Lorem ipsu")
                .releaseDate(LocalDate.of(2006, Month.APRIL, 7))
                .duration(110).build();
        Boolean actual = FilmValidation.validate(film);

        assertFalse(actual);
    }

    @Test
    public void shouldReturnTrueForReleaseDateAt7OctoberOf1700() {
        film = Film.builder()
                .id(1)
                .name("Lucky number Slevin")
                .description("Lorem ipsu")
                .releaseDate(LocalDate.of(1700, Month.OCTOBER, 7))
                .duration(110).build();
        Boolean actual = FilmValidation.validate(film);

        assertTrue(actual);
    }

    @Test
    public void shouldReturnTrueForReleaseDateAt27DecemberOf1895() {
        film = Film.builder()
                .id(1)
                .name("Lucky number Slevin")
                .description("Lorem ipsu")
                .releaseDate(LocalDate.of(1895, Month.DECEMBER, 27))
                .duration(110).build();
        Boolean actual = FilmValidation.validate(film);

        assertTrue(actual);
    }

    @Test
    public void shouldReturnFalseForReleaseDateAt28DecemberOf1895() {
        film = Film.builder()
                .id(1)
                .name("Lucky number Slevin")
                .description("Lorem ipsu")
                .releaseDate(LocalDate.of(1895, Month.DECEMBER, 28))
                .duration(110).build();
        Boolean actual = FilmValidation.validate(film);

        assertFalse(actual);
    }

    @Test
    public void shouldReturnFalseForReleaseDateAt29DecemberOf1895() {
        film = Film.builder()
                .id(1)
                .name("Lucky number Slevin")
                .description("Lorem ipsu")
                .releaseDate(LocalDate.of(1895, Month.DECEMBER, 28))
                .duration(110).build();
        Boolean actual = FilmValidation.validate(film);

        assertFalse(actual);
    }

    @Test
    public void shouldReturnFalseForReleaseDateAt29DecemberOf2006() {
        film = Film.builder()
                .id(1)
                .name("Lucky number Slevin")
                .description("Lorem ipsu")
                .releaseDate(LocalDate.of(2006, Month.DECEMBER, 29))
                .duration(110).build();
        Boolean actual = FilmValidation.validate(film);

        assertFalse(actual);
    }

    @Test
    public void shouldReturnTrueForDuration0Minutes() {
        film = Film.builder()
                .id(1)
                .name("Lucky number Slevin")
                .description("Lorem ipsu")
                .releaseDate(LocalDate.of(2006, Month.DECEMBER, 29))
                .duration(0).build();
        Boolean actual = FilmValidation.validate(film);

        assertTrue(actual);
    }

    @Test
    public void shouldReturnTrueForDurationMinus1Minute() {
        film = Film.builder()
                .id(1)
                .name("Lucky number Slevin")
                .description("Lorem ipsu")
                .releaseDate(LocalDate.of(2006, Month.DECEMBER, 29))
                .duration(-1).build();
        Boolean actual = FilmValidation.validate(film);

        assertTrue(actual);
    }

    @Test
    public void shouldReturnTrueForDurationMinus18Minutes() {
        film = Film.builder()
                .id(1)
                .name("Lucky number Slevin")
                .description("Lorem ipsu")
                .releaseDate(LocalDate.of(2006, Month.DECEMBER, 29))
                .duration(-18).build();
        Boolean actual = FilmValidation.validate(film);

        assertTrue(actual);
    }

    @Test
    public void shouldReturnFalseForDuration1Minute() {
        film = Film.builder()
                .id(1)
                .name("Lucky number Slevin")
                .description("Lorem ipsu")
                .releaseDate(LocalDate.of(2006, Month.DECEMBER, 29))
                .duration(1).build();
        Boolean actual = FilmValidation.validate(film);

        assertFalse(actual);
    }

    @Test
    public void shouldReturnFalseForDuration2Minutes() {
        film = Film.builder()
                .id(1)
                .name("Lucky number Slevin")
                .description("Lorem ipsu")
                .releaseDate(LocalDate.of(2006, Month.DECEMBER, 29))
                .duration(2).build();
        Boolean actual = FilmValidation.validate(film);

        assertFalse(actual);
    }

    @Test
    public void shouldReturnFalseForDuration40Minutes() {
        film = Film.builder()
                .id(1)
                .name("Lucky number Slevin")
                .description("Lorem ipsu")
                .releaseDate(LocalDate.of(2006, Month.DECEMBER, 29))
                .duration(2).build();
        Boolean actual = FilmValidation.validate(film);

        assertFalse(actual);
    }
}
