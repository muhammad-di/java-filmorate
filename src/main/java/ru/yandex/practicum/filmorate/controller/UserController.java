package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exeption.InvalidUserPropertiesException;
import ru.yandex.practicum.filmorate.exeption.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();
    private final static LocalDate DATE_IN_FUTURE = LocalDate.now();

    @GetMapping
    public Collection<User> findAll() {
        return users.values();
    }

    @PostMapping
    public User create(@RequestBody User user) throws InvalidUserPropertiesException, UserAlreadyExistException {
        if (user == null
                || user.getEmail().isBlank()
                || !user.getEmail().contains("@")
                || user.getLogin().isBlank()
                || user.getLogin().contains(" ")
                || user.getBirthday().isAfter(LocalDate.now())) {
            throw new InvalidUserPropertiesException("Invalid properties for a user", 406);
        }
        if (users.containsKey(user.getId())) {
            throw new UserAlreadyExistException("User already exists", 409);
        }
        if (user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
        return users.get(user.getId());
    }

    @PutMapping
    public User update(@RequestBody User user) throws InvalidUserPropertiesException {
        if (user == null
                || user.getEmail().isBlank()
                || !user.getEmail().contains("@")
                || user.getLogin().isBlank()
                || user.getLogin().contains(" ")
                || user.getBirthday().isAfter(DATE_IN_FUTURE)) {
            throw new InvalidUserPropertiesException("Invalid name if a film", 406);
        }
        if (user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
        return users.get(user.getId());
    }
}
