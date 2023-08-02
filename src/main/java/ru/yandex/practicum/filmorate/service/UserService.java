package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.yandex.practicum.filmorate.exeption.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.validation.UserValidation;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserIdGenerator userIdGenerator;
    private final UserStorage storage;

    @Autowired
    public UserService(UserStorage storage) {
        this.storage = storage;
        userIdGenerator = new UserIdGenerator();
    }

    public Collection<User> findAll() {
        return storage.findAll();

    }

    public User create(User user) throws InvalidUserPropertiesException, UserAlreadyExistException {
        if (UserValidation.validate(user)) {
            log.info("User validation error");
            throw new InvalidUserPropertiesException("Invalid properties for a user", 406);
        }
        if (storage.containsUser(user.getId())) {
            log.info("User already exists error");
            throw new UserAlreadyExistException("User already exists", 409);
        }
        if (user.getId() == 0) {
            user.setId(userIdGenerator.getNextFreeId());
        }
        if (!StringUtils.hasText(user.getName())) {
            user.setName(user.getLogin());
        }
        return storage.create(user);
    }

    public User update(User user) throws UserDoesNotExistException, InvalidUserPropertiesException {
        if (!storage.containsUser(user.getId())) {
            throw new UserDoesNotExistException("User with such is does not exist", 500);
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

    public Collection<User> getAllFriends(Integer id) {
        return storage.getAllFriends(id);
    }

    public void addFriend(int id, int idOfFriend) {
        storage.addFriend(id, idOfFriend);
    }

    public void deleteFriend(int id, int idOfFriend) {
        storage.deleteFriend(id, idOfFriend);
    }

    public Collection<User> getCommonFriends(int id, int idOfFriend) {
        return storage.getCommonFriends(id, idOfFriend);
    }

    public User getUserById(Integer id) {
        User user = storage.getUserById(id);
        if (user == null) {
            throw new UserNotFoundException("User with such id does not exist");
        }
        return user;
    }

    private static final class UserIdGenerator {
        private int nextFreeId = 1;

        private int getNextFreeId() {
            return nextFreeId++;
        }
    }
}
