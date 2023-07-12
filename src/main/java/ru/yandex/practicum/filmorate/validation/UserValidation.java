package ru.yandex.practicum.filmorate.validation;

import org.springframework.util.StringUtils;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

public class UserValidation {
    private static final LocalDate DATE_IN_FUTURE = LocalDate.now();

    public static Boolean validate(final User user) {
        return user == null
                || !StringUtils.hasText(user.getEmail())
                || !user.getEmail().contains("@")
                || !StringUtils.hasText(user.getLogin())
                || user.getLogin().contains(" ")
                || user.getBirthday().isAfter(DATE_IN_FUTURE);
    }
}
