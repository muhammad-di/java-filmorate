//package ru.yandex.practicum.filmorate.storage.genre.dao.impl;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
//import org.springframework.boot.test.context.SpringBootTest;
//import ru.yandex.practicum.filmorate.model.Genre;
//
//import java.util.Collection;
//import java.util.List;
//import java.util.Optional;
//
//import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
//
//@Slf4j
//@SpringBootTest
//@AutoConfigureTestDatabase
//@RequiredArgsConstructor(onConstructor_ = @Autowired)
//public class GenreDbStorageTests {
//    private final GenreDbStorage genreDbStorage;
//
//    @Test
//    public void testFindAll() {
//        Collection<Genre> expected =  List.of(new Genre(1, "Комедия"),
//                new Genre(2, "Драма"),
//                new Genre(3, "Мультфильм"),
//                new Genre(4, "Триллер"),
//                new Genre(5, "Документальный"),
//                new Genre(6, "Боевик"),
//                new Genre(7, "DOCUMENTARY"),
//                new Genre(8, "DRAMA"),
//                new Genre(9, "FAMILY"),
//                new Genre(10, "FANTASY"),
//                new Genre(11, "HISTORY"),
//                new Genre(12, "HORROR"),
//                new Genre(13, "KIDS"),
//                new Genre(14, "MUSIC"),
//                new Genre(15, "MYSTERY"),
//                new Genre(16, "NEWS"),
//                new Genre(17, "REALITY"),
//                new Genre(18, "ROMANCE"),
//                new Genre(19, "SCI_FI"),
//                new Genre(20, "SCIENCE_FICTION"),
//                new Genre(21, "SOAP"),
//                new Genre(22, "TALK"),
//                new Genre(23, "THRILLER"),
//                new Genre(24, "WAR"),
//                new Genre(25, "POLITICS"),
//                new Genre(26, "WESTERN")
//                );
//
//        Optional<Collection<Genre>> mpaOptional = Optional.ofNullable(genreDbStorage.findAll());
//        assertThat(mpaOptional).isPresent().hasValue(expected);
//    }
//
//    @Test
//    public void testFindGenreById() {
//        Genre expected = new Genre(1, "Комедия");
//
//        Optional<Genre> genreById = Optional.ofNullable(genreDbStorage.getGenreById(1));
//        assertThat(genreById).isPresent().hasValue(expected);
//    }
//
//
//    @Test
//    public void testContainsGenre() {
//        Boolean expected = true;
//
//        Optional<Boolean> optionalT = Optional.of(genreDbStorage.containsGenre(2));
//        assertThat(optionalT).isPresent().hasValue(expected);
//        log.info("----------ContainsGenre {}", optionalT.get());
//    }
//
//    @Test
//    public void testContainsFilmFalse() {
//        Boolean expected = false;
//
//        Optional<Boolean> optionalT = Optional.of(genreDbStorage.containsGenre(40));
//        assertThat(optionalT).isPresent().hasValue(expected);
//        log.info("----------ContainsGenre {}", optionalT.get());
//    }
//}
