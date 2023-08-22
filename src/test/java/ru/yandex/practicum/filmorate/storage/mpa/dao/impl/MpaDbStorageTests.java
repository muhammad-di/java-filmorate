package ru.yandex.practicum.filmorate.storage.mpa.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Slf4j
@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class MpaDbStorageTests {
    private final MpaDbStorage mpaDbStorage;

    @Test
    public void testFindAll() {
        Collection<Mpa> expected =  List.of(new Mpa(1, "G"),
                new Mpa(2, "PG"),
                new Mpa(3, "PG-13"),
                new Mpa(4, "R"),
                new Mpa(5, "NC-17")
                );

        Optional<Collection<Mpa>> mpaOptional = Optional.ofNullable(mpaDbStorage.findAll());
        assertThat(mpaOptional).isPresent().hasValue(expected);
    }

    @Test
    public void testFindMpaById() {
        Mpa expected = new Mpa(1, "G");

        Optional<Mpa> mpaOptional = Optional.ofNullable(mpaDbStorage.getMpaById(1));
        assertThat(mpaOptional).isPresent().hasValue(expected);
    }


    @Test
    public void testContainsMpa() {
        Boolean expected = true;

        Optional<Boolean> mpaOptional = Optional.of(mpaDbStorage.containsMpa(2));
        assertThat(mpaOptional).isPresent().hasValue(expected);
        log.info("----------ContainsMpa {}", mpaOptional.get());
    }

    @Test
    public void testContainsFilmFalse() {
        Boolean expected = false;

        Optional<Boolean> mpaOptional = Optional.of(mpaDbStorage.containsMpa(7));
        assertThat(mpaOptional).isPresent().hasValue(expected);
        log.info("----------ContainsFilm {}", mpaOptional.get());
    }
}
