package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;


public interface UserStorage {
    Collection<User> findAll();

    User create(User user);

    User update(User user);

    Collection<User> getAllFriends(Integer id);

    void addFriend(int id, int idOfFriend);

    void deleteFriend(int id, int idOfFriend);

    Collection<User> getCommonFriends(int id, int idOfFriend);

    boolean containsUser(int idOfUser);

    User getUserById(Integer id);
}
