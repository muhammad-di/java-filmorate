package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.yandex.practicum.filmorate.exeption.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.dao.UserStorage;
import ru.yandex.practicum.filmorate.validation.UserValidation;

import java.util.*;

@Slf4j
@Service
public class UserService {
    private final UserStorage storage;

    @Autowired
    public UserService(UserStorage storage) {
        this.storage = storage;
    }

    public Collection<User> findAll() {
        return storage.findAll();
    }

    public User create(User user)
            throws InvalidUserPropertiesException, UserAlreadyExistException {
        if (UserValidation.validate(user)) {
            log.info("User validation error");
            throw new InvalidUserPropertiesException("Invalid properties for a user", 406);
        }
        if (storage.containsUser(user.getId())) {
            log.info("User already exists error");
            throw new UserAlreadyExistException("User already exists", 409);
        }
        if (!StringUtils.hasText(user.getName())) {
            user.setName(user.getLogin());
        }
        return storage.create(user);
    }

    public User update(User user)
            throws UserDoesNotExistException, InvalidUserPropertiesException {
        if (!storage.containsUser(user.getId())) {
            throw new UserDoesNotExistException("User " +
                    "with such id {" + user.getId() + "} does not exist", 404);
        }
        if (UserValidation.validate(user)) {
            log.info("User validation error");
            throw new InvalidUserPropertiesException("Invalid name if a film", 406);
        }
        if (user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        return storage.update(user);
    }

    public Collection<User> getAllFriends(Long id) throws UserDoesNotExistException {
        if (!storage.containsUser(id)) {
            throw new UserDoesNotExistException("User " +
                    "with such id {" + id + "} does not exist", 404);
        }
        return storage.getAllFriends(id);
    }

    public void addFriend(Long id, Long idOfFriend) throws UserDoesNotExistException {
        if (!storage.containsUser(id)) {
            throw new UserDoesNotExistException("User " +
                    "with such id {" + id + "} does not exist", 404);
        }
        if (!storage.containsUser(idOfFriend)) {
            throw new UserDoesNotExistException("User " +
                    "with such id {" + id + "} does not exist", 404);
        }
        storage.addFriend(id, idOfFriend);
    }

    public void deleteFriend(Long id, Long idOfFriend) throws UserDoesNotExistException {
        if (!storage.containsUser(id)) {
            throw new UserDoesNotExistException("User " +
                    "with such id {" + id + "} does not exist", 404);
        }
        if (!storage.containsUser(idOfFriend)) {
            throw new UserDoesNotExistException("User " +
                    "with such id {" + id + "} does not exist", 404);
        }
        storage.deleteFriend(id, idOfFriend);
    }

    public Collection<User> getCommonFriends(Long id, Long idOfFriend) throws UserDoesNotExistException {
        if (!storage.containsUser(id)) {
            throw new UserDoesNotExistException("User " +
                    "with such id {" + id + "} does not exist", 404);
        }
        if (!storage.containsUser(idOfFriend)) {
            throw new UserDoesNotExistException("User " +
                    "with such id {" + id + "} does not exist", 404);
        }
        return storage.getCommonFriends(id, idOfFriend);
    }

    public User getUserById(Long id) throws UserDoesNotExistException {
        if (!storage.containsUser(id)) {
            throw new UserDoesNotExistException("User " +
                    "with such id {" + id + "} does not exist", 404);
        }
        return storage.getUserById(id);
    }

    public void deleteUserById(Long id) throws UserDoesNotExistException {
        if (!storage.containsUser(id)) {
            throw new UserDoesNotExistException("User " +
                    "with such id {" + id + "} does not exist", 404);
        }
        storage.deleteUserById(id);
    }
}
