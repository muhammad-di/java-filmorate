package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.Event;
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
        if (storage.contains(user.getId())) {
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
        contains(user.getId());
        if (UserValidation.validate(user)) {
            log.info("User validation error");
            throw new InvalidUserPropertiesException("Invalid name if a film", 406);
        }
        if (user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        return storage.update(user);
    }

    public Collection<User> findAllFriends(Long id) throws UserDoesNotExistException {
        contains(id);
        return storage.findAllFriends(id);
    }

    public void addFriend(Long id, Long idOfFriend) throws UserDoesNotExistException {
        contains(id);
        contains(idOfFriend);
        storage.addFriend(id, idOfFriend);
    }

    public void deleteFriend(Long id, Long idOfFriend) throws UserDoesNotExistException {
        contains(id);
        contains(idOfFriend);
        storage.deleteFriend(id, idOfFriend);
    }

    public Collection<User> findCommonFriends(Long id, Long idOfFriend)
            throws UserDoesNotExistException {
        contains(id);
        contains(idOfFriend);
        return storage.findCommonFriends(id, idOfFriend);
    }

    public User findById(Long id) throws UserDoesNotExistException {
        contains(id);
        return storage.findById(id);
    }

    public void deleteById(Long id) throws UserDoesNotExistException {
        contains(id);
        storage.deleteById(id);
    }

    public Collection<Event> findFeedOfUser(Long id) throws UserDoesNotExistException {
        contains(id);
        return storage.findEventByUserId(id);
    }

    private void contains(Long id) throws UserDoesNotExistException {
        if (!storage.contains(id)) {
            throw new UserDoesNotExistException("User " +
                    "with such id {" + id + "} does not exist", 404);
        }
    }
}
