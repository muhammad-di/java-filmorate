package ru.yandex.practicum.filmorate.validation;

import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

public class UserValidation {
    private static final LocalDate DATE_IN_FUTURE = LocalDate.now();

    public static Boolean validate(final User user) {
        return user == null
                || user.getEmail().isBlank()
                || !user.getEmail().contains("@")
                || user.getLogin().isBlank()
                || user.getLogin().contains(" ")
                || user.getBirthday().isAfter(DATE_IN_FUTURE);
    }
}
