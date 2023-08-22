//package ru.yandex.practicum.filmorate.storage.user.dao.impl;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
//import org.springframework.boot.test.context.SpringBootTest;
//import ru.yandex.practicum.filmorate.model.User;
//
//import java.time.LocalDate;
//import java.util.*;
//
//import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
//
//@Slf4j
//@SpringBootTest
//@AutoConfigureTestDatabase
//@RequiredArgsConstructor(onConstructor_ = @Autowired)
//public class UserDbStorageTests {
//    private final UserDbStorage userDbStorage;
//
//    @Test
//    public void testFindUsers() {
//        Collection<User> expected = List.of(
//                User.builder()
//                        .id(1)
//                        .login("bphripp0")
//                        .birthday(LocalDate.of(1980, 12, 25))
//                        .friends(new HashMap<>())
//                        .email("bruxton0@chicagotribune.com")
//                        .name("Brett")
//                        .build(),
//                User.builder()
//                        .id(2)
//                        .login("dmccrone1")
//                        .birthday(LocalDate.of(1984, 1, 21))
//                        .friends(new HashMap<>())
//                        .email("dskellon1@columbia.edu")
//                        .name("Dru")
//                        .build(),
//                User.builder()
//                        .id(3)
//                        .login("svaszoly2")
//                        .birthday(LocalDate.of(1979, 3, 20))
//                        .friends(new HashMap<>())
//                        .email("swalcher2@newsvine.com")
//                        .name("Sarina")
//                        .build(),
//                User.builder()
//                        .id(4)
//                        .login("jwood3")
//                        .birthday(LocalDate.of(1978, 3, 24))
//                        .friends(new HashMap<>())
//                        .email("jfletham3@yolasite.com")
//                        .name("Jard")
//                        .build(),
//                User.builder()
//                        .id(5)
//                        .login("rlevington4")
//                        .birthday(LocalDate.of(1979, 9, 9))
//                        .friends(new HashMap<>())
//                        .email("rclough4@seattletimes.com")
//                        .name("Randi")
//                        .build(),
//                User.builder()
//                        .id(6)
//                        .login("nmartel5")
//                        .birthday(LocalDate.of(1965, 12, 4))
//                        .friends(new HashMap<>())
//                        .email("ngosswell5@pen.io")
//                        .name("Nichols")
//                        .build(),
//                User.builder()
//                        .id(7)
//                        .login("ccrosen6")
//                        .birthday(LocalDate.of(1960, 9, 11))
//                        .friends(new HashMap<>(Map.of(
//                                2L, false,
//                                3L, false,
//                                4L, false,
//                                5L, false
//                        )))
//                        .email("cgerman6@live.com")
//                        .name("Clarie")
//                        .build(),
//                User.builder()
//                        .id(8)
//                        .login("wcolbron7")
//                        .birthday(LocalDate.of(1975, 6, 7))
//                        .friends(new HashMap<>())
//                        .email("wrobardey7@cbsnews.com")
//                        .name("Wesley")
//                        .build(),
//                User.builder()
//                        .id(9)
//                        .login("ldunstall8")
//                        .birthday(LocalDate.of(1981, 7, 20))
//                        .friends(new HashMap<>(Map.of(
//                                3L, false,
//                                4L, false,
//                                5L, false,
//                                6L, false
//                        )))
//                        .email("lhiseman8@home.pl")
//                        .name("Lazare")
//                        .build(),
//                User.builder()
//                        .id(10)
//                        .login("ahurley9")
//                        .birthday(LocalDate.of(1995, 11, 22))
//                        .friends(new HashMap<>())
//                        .email("aboneham9@wiley.com")
//                        .name("Alick")
//                        .build()
//        );
//
//        Optional<Collection<User>> usersOptional = Optional.of(userDbStorage.findAll());
//        log.info("---------List of users {}", usersOptional.get());
//        assertThat(usersOptional).isPresent().hasValue(expected);
//    }
//
//    @Test
//    public void testFindFilmById() {
//        User expected = User.builder()
//                .id(1)
//                .login("bphripp0")
//                .birthday(LocalDate.of(1980, 12, 25))
//                .friends(new HashMap<>())
//                .email("bruxton0@chicagotribune.com")
//                .name("Brett")
//                .build();
//
//        Optional<User> userOptional = Optional.ofNullable(userDbStorage.getUserById(1L));
//        assertThat(userOptional).isPresent().hasValue(expected);
//    }
//
//    @Test
//    public void testCreate() {
//        User original = User.builder()
//                .login("p@DDingTon")
//                .birthday(LocalDate.of(1994, 4, 29))
//                .friends(new HashMap<>())
//                .email("paddington@gmail.com")
//                .name("Paddington")
//                .build();
//
//        User expected = User.builder()
//                .id(11)
//                .login("p@DDingTon")
//                .birthday(LocalDate.of(1994, 4, 29))
//                .friends(new HashMap<>())
//                .email("paddington@gmail.com")
//                .name("Paddington")
//                .build();
//
//
//        Optional<User> userOptional = Optional.ofNullable(userDbStorage.create(original));
//        assertThat(userOptional).isPresent().hasValue(expected);
//        log.info("---------- {}", userOptional.get());
//    }
//
//    @Test
//    public void testUpdate() {
//        User expected = User.builder()
//                .id(3)
//                .login("p@DDingTon")
//                .birthday(LocalDate.of(1980, 12, 25))
//                .friends(new HashMap<>())
//                .email("paddington@gmail.com")
//                .name("Paddington")
//                .build();
//
//
//        Optional<User> userOptional = Optional.ofNullable(userDbStorage.update(expected));
//        assertThat(userOptional).isPresent().hasValue(expected);
//        log.info("----------update {}", userOptional.get());
//    }
//
//    @Test
//    public void testAddFriend() {
//        User expected = User.builder()
//                .id(7)
//                .login("ccrosen6")
//                .birthday(LocalDate.of(1960, 9, 11))
//                .friends(new HashMap<>(Map.of(
//                        2L, false,
//                        3L, false,
//                        4L, false,
//                        5L, false
//                )))
//                .email("cgerman6@live.com")
//                .name("Clarie")
//                .build();
//
//        userDbStorage.addFriend(7L, 3L);
//        userDbStorage.addFriend(7L, 4L);
//        userDbStorage.addFriend(7L, 5L);
//
//        Optional<User> userOptional = Optional.ofNullable(userDbStorage.getUserById(7L));
//        assertThat(userOptional).isPresent().hasValue(expected);
//        log.info("----------AddFriend {}", userOptional.get());
//    }
//
//    @Test
//    public void testDeleteLike() {
//        User expected = User.builder()
//                .id(7)
//                .login("ccrosen6")
//                .birthday(LocalDate.of(1960, 9, 11))
//                .friends(new HashMap<>(Map.of(
//                        2L, false,
//                        3L, false,
//                        4L, false,
//                        5L, false
//                )))
//                .email("cgerman6@live.com")
//                .name("Clarie")
//                .build();
//
//
//        userDbStorage.addFriend(7L, 3L);
//        userDbStorage.addFriend(7L, 4L);
//        userDbStorage.addFriend(7L, 5L);
//        userDbStorage.addFriend(7L, 6L);
//        userDbStorage.deleteFriend(7L, 6L);
//
//        Optional<User> userOptional = Optional.ofNullable(userDbStorage.getUserById(7L));
//        assertThat(userOptional).isPresent().hasValue(expected);
//        log.info("----------DeleteFriend {}", userOptional.get());
//    }
//
//    @Test
//    public void testGetAllFriends() {
//        Collection<User> expected = List.of(
//                User.builder()
//                        .id(3)
//                        .login("svaszoly2")
//                        .birthday(LocalDate.of(1979, 3, 20))
//                        .friends(new HashMap<>())
//                        .email("swalcher2@newsvine.com")
//                        .name("Sarina")
//                        .build(),
//                User.builder()
//                        .id(4)
//                        .login("jwood3")
//                        .birthday(LocalDate.of(1978, 3, 24))
//                        .friends(new HashMap<>())
//                        .email("jfletham3@yolasite.com")
//                        .name("Jard")
//                        .build(),
//                User.builder()
//                        .id(5)
//                        .login("rlevington4")
//                        .birthday(LocalDate.of(1979, 9, 9))
//                        .friends(new HashMap<>())
//                        .email("rclough4@seattletimes.com")
//                        .name("Randi")
//                        .build()
//        );
//
//
//        userDbStorage.addFriend(7L, 3L);
//        userDbStorage.addFriend(7L, 4L);
//        userDbStorage.addFriend(7L, 5L);
//
//        Optional<Collection<User>> usersOptional = Optional.ofNullable(userDbStorage.getAllFriends(7L));
//        assertThat(usersOptional).isPresent().hasValue(expected);
//        log.info("----------GetAllFriends {}", usersOptional.get());
//    }
//
//    @Test
//    public void testGetCommonFriends() {
//        Collection<User> expected = List.of(
//                User.builder()
//                        .id(4)
//                        .login("jwood3")
//                        .birthday(LocalDate.of(1978, 3, 24))
//                        .friends(new HashMap<>())
//                        .email("jfletham3@yolasite.com")
//                        .name("Jard")
//                        .build(),
//                User.builder()
//                        .id(5)
//                        .login("rlevington4")
//                        .birthday(LocalDate.of(1979, 9, 9))
//                        .friends(new HashMap<>())
//                        .email("rclough4@seattletimes.com")
//                        .name("Randi")
//                        .build(),
//                User.builder()
//                        .id(3)
//                        .login("svaszoly2")
//                        .birthday(LocalDate.of(1979, 3, 20))
//                        .friends(new HashMap<>())
//                        .email("swalcher2@newsvine.com")
//                        .name("Sarina")
//                        .build()
//        );
//
//        userDbStorage.addFriend(7L, 2L);
//        userDbStorage.addFriend(7L, 3L);
//        userDbStorage.addFriend(7L, 4L);
//        userDbStorage.addFriend(7L, 5L);
//
//        userDbStorage.addFriend(9L, 3L);
//        userDbStorage.addFriend(9L, 4L);
//        userDbStorage.addFriend(9L, 5L);
//        userDbStorage.addFriend(9L, 6L);
//
//        Optional<Collection<User>> usersOptional = Optional.ofNullable(userDbStorage.getCommonFriends(7L, 9L));
//        assertThat(usersOptional).isPresent().hasValue(expected);
//        log.info("----------GetCommonFriends {}", usersOptional.get());
//    }
//
//    @Test
//    public void testContainsUser() {
//        Boolean expected = true;
//
//        Optional<Boolean> userOptional = Optional.of(userDbStorage.containsUser(2L));
//        assertThat(userOptional).isPresent().hasValue(expected);
//        log.info("----------ContainsUser {}", userOptional.get());
//    }
//
//    @Test
//    public void testContainsFilmFalse() {
//        Boolean expected = false;
//
//        Optional<Boolean> userOptional = Optional.of(userDbStorage.containsUser(20L));
//        assertThat(userOptional).isPresent().hasValue(expected);
//        log.info("----------ContainsUserFalse {}", userOptional.get());
//    }
//}
