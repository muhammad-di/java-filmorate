package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exeption.InvalidUserPropertiesException;
import ru.yandex.practicum.filmorate.exeption.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.exeption.UserDoesNotExistException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validation.UserValidation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserIdGenerator userIdGenerator = new UserIdGenerator();
    private final Map<Integer, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @PostMapping
    public User create(@RequestBody User user) throws InvalidUserPropertiesException, UserAlreadyExistException {

        if (UserValidation.validate(user)) {
            log.info("User validation error");
            throw new InvalidUserPropertiesException("Invalid properties for a user", 406);
        }
        if (users.containsKey(user.getId())) {
            log.info("User already exists error");
            throw new UserAlreadyExistException("User already exists", 409);
        }
        if (user.getId() == 0) {
            user.setId(userIdGenerator.getNextFreeId());
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
        log.info("User entity with id {} and name {} was created", user.getId(), user.getName());
        return users.get(user.getId());
    }

    @PutMapping
    public User update(@RequestBody User user) throws UserDoesNotExistException, InvalidUserPropertiesException {
        if (!users.containsKey(user.getId())) {
            throw new UserDoesNotExistException("User with such is does not exist",500);
        }
        if (UserValidation.validate(user)) {
            log.info("User validation error");
            throw new InvalidUserPropertiesException("Invalid name if a film", 406);
        }
        if (user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
        log.info("User entity with id {} and name {} was created", user.getId(), user.getName());
        return users.get(user.getId());
    }

    private static final class UserIdGenerator {
        private int nextFreeId = 1;

        private int getNextFreeId() {
            return nextFreeId++;
        }
    }
}
