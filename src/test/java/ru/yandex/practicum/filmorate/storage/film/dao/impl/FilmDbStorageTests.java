package ru.yandex.practicum.filmorate.storage.film.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.*;


import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Slf4j
@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmDbStorageTests {
    private final FilmDbStorage filmDbStorage;

    @Test
    public void testFindFilms() {
        Collection<Film> expected = new ArrayList<>(List.of(
                Film.builder()
                        .id(1)
                        .name("Lucky Number Slevin")
                        .releaseDate(LocalDate.of(2006, 2, 24))
                        .duration(110)
                        .description("Description of a Lucky Number Slevin movie")
                        .likes(new HashSet<>())
                        .genres(new ArrayList<>())
                        .mpa(Mpa.builder().id(4).name("R").build())
                        .build(),
                Film.builder()
                        .id(2)
                        .name("No Reservations")
                        .releaseDate(LocalDate.of(2007, 7, 27))
                        .duration(104)
                        .description("Description of a No Reservations movie")
                        .likes(new HashSet<>())
                        .genres(new ArrayList<>())
                        .mpa(Mpa.builder().id(2).name("PG").build())
                        .build(),
                Film.builder()
                        .id(3)
                        .name("Dune")
                        .releaseDate(LocalDate.of(2021, 10, 22))
                        .duration(155)
                        .description("Description of a Dune movie")
                        .likes(new HashSet<>())
                        .genres(new ArrayList<>())
                        .mpa(Mpa.builder().id(3).name("PG-13").build())
                        .build()
        ));

        Optional<Collection<Film>> filmOptional = Optional.of(filmDbStorage.findAll());
        log.info("List of films {}", filmOptional.get());
        assertThat(filmOptional).isPresent().hasValue(expected);
    }

    @Test
    public void testFindFilmById() {
        Film expected =
                Film.builder()
                        .id(1)
                        .name("Lucky Number Slevin")
                        .releaseDate(LocalDate.of(2006, 2, 24))
                        .duration(110)
                        .description("Description of a Lucky Number Slevin movie")
                        .likes(new HashSet<>())
                        .genres(new ArrayList<>())
                        .mpa(Mpa.builder().id(4).name("R").build())
                        .build();

        Optional<Film> filmOptional = Optional.ofNullable(filmDbStorage.getFilmById(1L));
        assertThat(filmOptional).isPresent().hasValue(expected);
    }

    @Test
    public void testCreate() {
        Film original =
                Film.builder()
                        .name("Paddington")
                        .releaseDate(LocalDate.of(2014, 11, 28))
                        .duration(95)
                        .description("Description of a Paddington movie")
                        .likes(new HashSet<>())
                        .genres(new ArrayList<>())
                        .mpa(Mpa.builder().id(4).name("R").build())
                        .build();

        Film expected =
                Film.builder()
                        .id(4l)
                        .name("Paddington")
                        .releaseDate(LocalDate.of(2014, 11, 28))
                        .duration(95)
                        .description("Description of a Paddington movie")
                        .likes(new HashSet<>())
                        .genres(new ArrayList<>())
                        .mpa(Mpa.builder().id(4).name("R").build())
                        .build();


        Optional<Film> filmOptional = Optional.ofNullable(filmDbStorage.create(original));
        assertThat(filmOptional).isPresent().hasValue(expected);
        log.info("---------- {}", filmOptional.get());
    }

    @Test
    public void testUpdate() {
        Film original =
                Film.builder()
                        .id(1)
                        .name("-Lucky Number Slevin-")
                        .releaseDate(LocalDate.of(2010, 10, 10))
                        .duration(110)
                        .description("Description of a Lucky Number Slevin movie")
                        .likes(new HashSet<>())
                        .genres(new ArrayList<>(List.of(
                                new Genre(1, null),
                                new Genre(2, null),
                                new Genre(3, null)
                        )))
                        .mpa(Mpa.builder().id(3).build())
                        .build();

        Film expected =
                Film.builder()
                        .id(1)
                        .name("-Lucky Number Slevin-")
                        .releaseDate(LocalDate.of(2010, 10, 10))
                        .duration(110)
                        .description("Description of a Lucky Number Slevin movie")
                        .likes(new HashSet<>())
                        .genres(new ArrayList<>(List.of(
                                new Genre(1, "Комедия"),
                                new Genre(2, "Драма"),
                                new Genre(3, "Мультфильм")
                        )))
                        .mpa(Mpa.builder().id(3).name("PG-13").build())
                        .build();


        Optional<Film> filmOptional = Optional.ofNullable(filmDbStorage.update(original));
        assertThat(filmOptional).isPresent().hasValue(expected);
        log.info("----------update {}", filmOptional.get());
    }

    @Test
    public void testAddLike() {
        Film expected =
                Film.builder()
                        .id(1)
                        .name("Lucky Number Slevin")
                        .releaseDate(LocalDate.of(2006, 2, 24))
                        .duration(110)
                        .description("Description of a Lucky Number Slevin movie")
                        .likes(new HashSet<>(Set.of(3L)))
                        .genres(new ArrayList<>())
                        .mpa(Mpa.builder().id(4).name("R").build())
                        .build();

        filmDbStorage.addLike(1L, 3L);
        Optional<Film> filmOptional = Optional.ofNullable(filmDbStorage.getFilmById(1L));
        assertThat(filmOptional).isPresent().hasValue(expected);
        log.info("----------AddLike {}", filmOptional.get());
    }
//
    @Test
    public void testDeleteLike() {
        Film expected =
                Film.builder()
                        .id(1)
                        .name("Lucky Number Slevin")
                        .releaseDate(LocalDate.of(2006, 2, 24))
                        .duration(110)
                        .description("Description of a Lucky Number Slevin movie")
                        .likes(new HashSet<>(Set.of(5L, 1L)))
                        .genres(new ArrayList<>())
                        .mpa(Mpa.builder().id(4).name("R").build())
                        .build();

        filmDbStorage.addLike(1L, 3L);
        filmDbStorage.addLike(1L, 5L);
        filmDbStorage.addLike(1L, 1L);
        filmDbStorage.deleteLike(1L, 3L);
        Optional<Film> filmOptional = Optional.ofNullable(filmDbStorage.getFilmById(1L));
        assertThat(filmOptional).isPresent().hasValue(expected);
        log.info("----------DeleteLike {}", filmOptional.get());
    }
//
    @Test
    public void testGetMostPopularFilms() {
        Collection<Film> expected = List.of(
                Film.builder()
                        .id(1)
                        .name("Lucky Number Slevin")
                        .releaseDate(LocalDate.of(2006, 2, 24))
                        .duration(110)
                        .description("Description of a Lucky Number Slevin movie")
                        .likes(new HashSet<>(Set.of(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L)))
                        .genres(new ArrayList<>(List.of()))
                        .mpa(Mpa.builder().id(4).name("R").build())
                        .build(),
                Film.builder()
                        .id(2)
                        .name("No Reservations")
                        .releaseDate(LocalDate.of(2007, 7, 27))
                        .duration(104)
                        .description("Description of a No Reservations movie")
                        .likes(new HashSet<>(Set.of(1L, 2L, 3L, 4L, 5L, 6L)))
                        .genres(new ArrayList<>(List.of()))
                        .mpa(Mpa.builder().id(2).name("PG").build())
                        .build()
        );

        filmDbStorage.addLike(1L, 1L);
        filmDbStorage.addLike(1L, 2L);
        filmDbStorage.addLike(1L, 3L);
        filmDbStorage.addLike(1L, 4L);
        filmDbStorage.addLike(1L, 5L);
        filmDbStorage.addLike(1L, 6L);
        filmDbStorage.addLike(1L, 7L);
        filmDbStorage.addLike(1L, 8L);
        filmDbStorage.addLike(1L, 9L);

        filmDbStorage.addLike(2L, 1L);
        filmDbStorage.addLike(2L, 2L);
        filmDbStorage.addLike(2L, 3L);
        filmDbStorage.addLike(2L, 4L);
        filmDbStorage.addLike(2L, 5L);
        filmDbStorage.addLike(2L, 6L);

        filmDbStorage.addLike(3L, 1L);
        filmDbStorage.addLike(3L, 2L);
        filmDbStorage.addLike(3L, 3L);
        filmDbStorage.addLike(3L, 4L);


        Optional<Collection<Film>> filmOptional = Optional.of(filmDbStorage.getMostPopularFilms(2));
        assertThat(filmOptional).isPresent().hasValue(expected);
        log.info("----------GetMostPopularFilms {}", filmOptional.get());
    }
//
    @Test
    public void testContainsFilm() {
        Boolean expected = true;

        Optional<Boolean> filmOptional = Optional.of(filmDbStorage.containsFilm(2L));
        assertThat(filmOptional).isPresent().hasValue(expected);
        log.info("----------ContainsFilm {}", filmOptional.get());
    }
//
    @Test
    public void testContainsFilmFalse() {
        Boolean expected = false;

        Optional<Boolean> filmOptional = Optional.of(filmDbStorage.containsFilm(7L));
        assertThat(filmOptional).isPresent().hasValue(expected);
        log.info("----------ContainsFilm {}", filmOptional.get());
    }
}
