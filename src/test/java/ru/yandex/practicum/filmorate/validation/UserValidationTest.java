package ru.yandex.practicum.filmorate.validation;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class UserValidationTest {

    @Test
    public void shouldReturnTrueForUserNull() {
        Boolean actual = UserValidation.validate(null);
        assertTrue(actual);
    }

    @Test
    public void shouldReturnTrueForEmailEmpty() {
        User user = User.builder()
                .id(1L)
                .email("")
                .login("alexsLogin")
                .name("Alex")
                .birthday(LocalDate.of(2002, 4, 29))
                .build();

        Boolean actual = UserValidation.validate(user);
        assertTrue(actual);
    }

    @Test
    public void shouldReturnTrueForEmailBlank() {
        User user = User.builder()
                .id(1L)
                .email("  ")
                .login("alexsLogin")
                .name("Alex")
                .birthday(LocalDate.of(2002, 4, 29))
                .build();

        Boolean actual = UserValidation.validate(user);
        assertTrue(actual);
    }

    @Test
    public void shouldReturnFalseForEmailFiledIn() {
        User user = User.builder()
                .id(1L)
                .email("alex@example.uk")
                .login("alexsLogin")
                .name("Alex")
                .birthday(LocalDate.of(2002, 4, 29))
                .build();

        Boolean actual = UserValidation.validate(user);
        assertFalse(actual);
    }

    @Test
    public void shouldReturnTrueForEmailNotContainingAtChar() {
        User user = User.builder()
                .id(1L)
                .email("alexExample.uk")
                .login("alexsLogin")
                .name("Alex")
                .birthday(LocalDate.of(2002, 4, 29))
                .build();

        Boolean actual = UserValidation.validate(user);
        assertTrue(actual);
    }

    @Test
    public void shouldReturnTrueForLoginEmpty() {
        User user = User.builder()
                .id(1L)
                .email("alex@example.uk")
                .login("").name("Alex")
                .birthday(LocalDate.of(2002, 4, 29))
                .build();

        Boolean actual = UserValidation.validate(user);
        assertTrue(actual);
    }

    @Test
    public void shouldReturnTrueForLoginBlank() {
        User user = User.builder()
                .id(1L)
                .email("alex@example.uk")
                .login("   ")
                .name("Alex")
                .birthday(LocalDate.of(2002, 4, 29))
                .build();

        Boolean actual = UserValidation.validate(user);
        assertTrue(actual);
    }

    @Test
    public void shouldReturnTrueForLoginContainingSpaceChar() {
        User user = User.builder()
                .id(1L)
                .email("alex@example.uk")
                .login("  alexLogin")
                .name("Alex")
                .birthday(LocalDate.of(2002, 4, 29))
                .build();

        Boolean actual = UserValidation.validate(user);
        assertTrue(actual);
    }

    @Test
    public void shouldReturnTrueForBirthdayIsAfterNowDayOn1day() {
        User user = User.builder()
                .id(1L)
                .email("alex@example.uk")
                .login("alexLogin")
                .name("Alex")
                .birthday(LocalDate.now().plusDays(1))
                .build();

        Boolean actual = UserValidation.validate(user);
        assertTrue(actual);
    }

    @Test
    public void shouldReturnFalseForBirthdayIsNow() {
        User user = User.builder()
                .id(1L)
                .email("alex@example.uk")
                .login("alexLogin")
                .name("Alex")
                .birthday(LocalDate.now())
                .build();

        Boolean actual = UserValidation.validate(user);
        assertFalse(actual);
    }

    @Test
    public void shouldReturnFalseForBirthdayIsBeforeNowDate() {
        User user = User.builder()
                .id(1L)
                .email("alex@example.uk")
                .login("alexLogin")
                .name("Alex")
                .birthday(LocalDate.now().minusDays(2))
                .build();

        Boolean actual = UserValidation.validate(user);
        assertFalse(actual);
    }

}
