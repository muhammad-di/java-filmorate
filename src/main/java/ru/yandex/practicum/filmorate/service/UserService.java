package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.FeedEntity;
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
        containsUser(user.getId());
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
        containsUser(id);
        return storage.getAllFriends(id);
    }

    public void addFriend(Long id, Long idOfFriend) throws UserDoesNotExistException {
        containsUser(id);
        containsUser(idOfFriend);
        storage.addFriend(id, idOfFriend);
    }

    public void deleteFriend(Long id, Long idOfFriend) throws UserDoesNotExistException {
        containsUser(id);
        containsUser(idOfFriend);
        storage.deleteFriend(id, idOfFriend);
    }

    public Collection<User> getCommonFriends(Long id, Long idOfFriend)
            throws UserDoesNotExistException {
        containsUser(id);
        containsUser(idOfFriend);
        return storage.getCommonFriends(id, idOfFriend);
    }

    public User getUserById(Long id) throws UserDoesNotExistException {
        containsUser(id);
        return storage.getUserById(id);
    }

    public void deleteUserById(Long id) throws UserDoesNotExistException {
        containsUser(id);
        storage.deleteUserById(id);
    }

    public Collection<FeedEntity> getFeedOfUser(Long id) throws UserDoesNotExistException {
        containsUser(id);
        return storage.getFeedOfUser(id);
    }

    private void containsUser(Long id) throws UserDoesNotExistException {
        if (!storage.containsUser(id)) {
            throw new UserDoesNotExistException("User " +
                    "with such id {" + id + "} does not exist", 404);
        }
    }
}
