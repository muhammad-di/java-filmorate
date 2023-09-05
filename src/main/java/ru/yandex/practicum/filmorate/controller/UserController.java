package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.RecommendationsService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;
import java.util.List;

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
    public User create(@RequestBody User user)
            throws InvalidUserPropertiesException, UserAlreadyExistException {
        return userService.create(user);
    }

    @PutMapping
    public User update(@RequestBody User user)
            throws UserDoesNotExistException, InvalidUserPropertiesException {
        return userService.update(user);
    }

    @GetMapping("/{id}/friends")
    public Collection<User> findAllFriends(@PathVariable Long id)
            throws UserDoesNotExistException {
        return userService.findAllFriends(id);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable Long id,
                          @PathVariable Long friendId) throws UserDoesNotExistException {
        if (id < MIN_ID || friendId < MIN_ID) {
            String msg = String.format("Path \"/%d/friends/%d\" does not exist", id, friendId);
            throw new PathNotFoundException(msg);
        }
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable Long id,
                             @PathVariable Long friendId) throws UserDoesNotExistException {
        if (id < MIN_ID) {
            throw new IncorrectParameterException("id");
        }
        if (friendId < MIN_ID) {
            throw new IncorrectParameterException("friendId");
        }
        userService.deleteFriend(id, friendId);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<User> findCommonFriends(@PathVariable Long id, @PathVariable Long otherId)
            throws UserDoesNotExistException {
        if (id < MIN_ID) {
            throw new IncorrectParameterException("id");
        }
        if (otherId < MIN_ID) {
            throw new IncorrectParameterException("otherId");
        }
        return userService.findCommonFriends(id, otherId);
    }

    @GetMapping("/{id}")
    public User findById(@PathVariable Long id) throws UserDoesNotExistException {
        if (id < MIN_ID) {
            throw new IncorrectParameterException("id");
        }
        return userService.findById(id);
    }

    @GetMapping("/{id}/recommendations")
    public List<Film> findRecommendedFilms(@PathVariable Long id) throws UserDoesNotExistException {
        if (id < MIN_ID) {
            throw new IncorrectParameterException("id");
        }
        return recommendationsService.findRecommendedFilms(id);
    }

    @DeleteMapping("/{userId}")
    public void deleteById(@PathVariable Long userId) throws UserDoesNotExistException {
        if (userId < MIN_ID) {
            throw new IncorrectParameterException("id");
        }
        userService.deleteById(userId);
    }

    @GetMapping("/{id}/feed")
    public Collection<Event> getFeedOfUser(@PathVariable Long id) throws UserDoesNotExistException {
        if (id < MIN_ID) {
            throw new IncorrectParameterException("id");
        }
        return userService.findFeedOfUser(id);
    }
}
