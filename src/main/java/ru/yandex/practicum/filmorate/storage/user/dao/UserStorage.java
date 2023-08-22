package ru.yandex.practicum.filmorate.storage.user.dao;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;


public interface UserStorage {
    Collection<User> findAll();

    User create(User user);

    User update(User user);

    Collection<User> getAllFriends(Long id);

    void addFriend(Long id, Long idOfFriend);

    void deleteFriend(Long id, Long idOfFriend);

    Collection<User> getCommonFriends(Long id, Long idOfFriend);

    boolean containsUser(Long idOfUser);

    User getUserById(Long id);
}