package ru.yandex.practicum.filmorate.storage.user.dao;

import ru.yandex.practicum.filmorate.model.FeedEntity;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Map;


public interface UserStorage {
    Collection<User> findAll();

    User create(User user);

    User update(User user);

    Collection<User> findAllFriends(Long id);

    void addFriend(Long id, Long idOfFriend);

    void deleteFriend(Long id, Long idOfFriend);

    Collection<User> findCommonFriends(Long id, Long idOfFriend);

    Boolean contains(Long idOfUser);

    User findById(Long id);

    Map<Long, List<Long>> findAllUsersWithTheirLikedFilms();

    void deleteById(Long id);

    Collection<FeedEntity> findFeedOfUser(Long id);
}
