package ru.yandex.practicum.filmorate.validation;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class UserValidationTest {

    private User user;

    @Test
    public void shouldReturnTrueForUserNull() {
        Boolean actual = UserValidation.validate(null);
        assertTrue(actual);
    }

    @Test
    public void shouldReturnTrueForEmailEmpty() {
        user = User.builder().id(1).email("").login("alexsLogin").name("Alex").birthday(LocalDate.of(2002, 4, 29)).build();

        Boolean actual = UserValidation.validate(user);
        assertTrue(actual);
    }

    @Test
    public void shouldReturnTrueForEmailBlank() {
        user = User.builder().id(1).email("  ").login("alexsLogin").name("Alex").birthday(LocalDate.of(2002, 4, 29)).build();

        Boolean actual = UserValidation.validate(user);
        assertTrue(actual);
    }

    @Test
    public void shouldReturnFalseForEmailFiledIn() {
        user = User.builder().id(1).email("alex@example.uk").login("alexsLogin").name("Alex").birthday(LocalDate.of(2002, 4, 29)).build();

        Boolean actual = UserValidation.validate(user);
        assertFalse(actual);
    }

    @Test
    public void shouldReturnTrueForEmailNotContainingAtChar() {
        user = User.builder().id(1).email("alexExample.uk").login("alexsLogin").name("Alex").birthday(LocalDate.of(2002, 4, 29)).build();

        Boolean actual = UserValidation.validate(user);
        assertTrue(actual);
    }

    @Test
    public void shouldReturnTrueForLoginEmpty() {
        user = User.builder().id(1).email("alex@example.uk").login("").name("Alex").birthday(LocalDate.of(2002, 4, 29)).build();

        Boolean actual = UserValidation.validate(user);
        assertTrue(actual);
    }

    @Test
    public void shouldReturnTrueForLoginBlank() {
        user = User.builder().id(1).email("alex@example.uk").login("   ").name("Alex").birthday(LocalDate.of(2002, 4, 29)).build();

        Boolean actual = UserValidation.validate(user);
        assertTrue(actual);
    }

    @Test
    public void shouldReturnTrueForLoginContainingSpaceChar() {
        user = User.builder().id(1).email("alex@example.uk").login("  alexLogin").name("Alex").birthday(LocalDate.of(2002, 4, 29)).build();

        Boolean actual = UserValidation.validate(user);
        assertTrue(actual);
    }

    @Test
    public void shouldReturnTrueForBirthdayIsAfterNowDayOn1day() {
        user = User.builder().id(1).email("alex@example.uk").login("alexLogin").name("Alex").birthday(LocalDate.now().plusDays(1)).build();

        Boolean actual = UserValidation.validate(user);
        assertTrue(actual);
    }

    @Test
    public void shouldReturnFalseForBirthdayIsNow() {
        user = User.builder().id(1).email("alex@example.uk").login("alexLogin").name("Alex").birthday(LocalDate.now()).build();

        Boolean actual = UserValidation.validate(user);
        assertFalse(actual);
    }

    @Test
    public void shouldReturnFalseForBirthdayIsBeforeNowDate() {
        user = User.builder().id(1).email("alex@example.uk").login("alexLogin").name("Alex").birthday(LocalDate.now().minusDays(2)).build();

        Boolean actual = UserValidation.validate(user);
        assertFalse(actual);
    }

}
