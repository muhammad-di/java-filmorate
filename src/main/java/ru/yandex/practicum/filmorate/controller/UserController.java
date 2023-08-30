package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.RecommendationsService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;
import java.util.Set;

import static ru.yandex.practicum.filmorate.Constants.MIN_ID;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final RecommendationsService recommendationsService;

    @GetMapping
    public Collection<User> findAll() {
        return userService.findAll();
    }

    @PostMapping
    public User create(@RequestBody User user) throws InvalidUserPropertiesException, UserAlreadyExistException {
        return userService.create(user);
    }

    @PutMapping
    public User update(@RequestBody User user) throws UserDoesNotExistException, InvalidUserPropertiesException {
        return userService.update(user);
    }

    @GetMapping("/{id}/friends")
    public Collection<User> getAllFriends(@PathVariable Long id) {
        return userService.getAllFriends(id);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable Long id, @PathVariable Long friendId) {
        if (id < MIN_ID || friendId < MIN_ID) {
            String msg = String.format("Path \"/%d/friends/%d\" does not exist", id, friendId);
            throw new PathNotFoundException(msg);
        }
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable Long id, @PathVariable Long friendId) {
        if (id < MIN_ID) {
            throw new IncorrectParameterException("id");
        }
        if (friendId < MIN_ID) {
            throw new IncorrectParameterException("friendId");
        }
        userService.deleteFriend(id, friendId);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<User> getCommonFriends(@PathVariable Long id, @PathVariable Long otherId) {
        if (id < MIN_ID) {
            throw new IncorrectParameterException("id");
        }
        if (otherId < MIN_ID) {
            throw new IncorrectParameterException("otherId");
        }
        return userService.getCommonFriends(id, otherId);
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) throws UserDoesNotExistException {
        if (id < MIN_ID) {
            throw new IncorrectParameterException("id");
        }
        return userService.getUserById(id);
    }

    @GetMapping("/{id}/recommendations")
    public Set<Film> getRecommendedFilms(@PathVariable Long id) {
        if (id < MIN_ID) {
            throw new IncorrectParameterException("id");
        }
        return recommendationsService.getRecommendedFilms(id);
    }
}
