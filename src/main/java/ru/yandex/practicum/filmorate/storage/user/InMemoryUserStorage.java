package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.dao.UserStorage;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new ConcurrentHashMap<>();

    public Collection<User> findAll() {
        return new ArrayList<>(users.values());
    }

    public User create(User user) {
        users.put(user.getId(), user);
        log.info("User entity with id {} and name {} was created", user.getId(), user.getName());
        return users.get(user.getId());
    }

    public User update(User user) {
        users.put(user.getId(), user);
        log.info("User entity with id {} and name {} was created", user.getId(), user.getName());
        return users.get(user.getId());
    }

    public Collection<User> getAllFriends(Long id) {
        return users.get(id).getFriends()
                .values().stream()
                .map(users::get)
                .collect(Collectors.toList());
    }

    public void addFriend(Long id, Long idOfFriend) {
        User user = users.get(id);
        User friend = users.get(idOfFriend);

        user.addFriend(idOfFriend);
        friend.addFriend(id);
    }

    public void deleteFriend(Long id, Long idOfFriend) {
        User user = users.get(id);
        User friend = users.get(idOfFriend);

        user.deleteFriend(idOfFriend);
        friend.deleteFriend(id);
    }

    public Collection<User> getCommonFriends(Long id, Long idOfFriend) {
        User user = users.get(id);
        User friend = users.get(idOfFriend);

        if (user.getFriends() == null || friend.getFriends() == null) {
            return Collections.emptySet();
        }
        Set<Long> common = new HashSet<>(user.getFriends().keySet());
        common.retainAll(friend.getFriends().keySet());
        return common.stream()
                .map(users::get)
                .collect(Collectors.toList());
    }

    public boolean containsUser(Long idOfUser) {
        return users.containsKey(idOfUser);
    }

    public User getUserById(Long id) {
        return users.getOrDefault(id, null);
    }

    @Override
    public void deleteUserById(Long id) {
        users.remove(id);
        log.info("User entity with id {} was deleted", id);
    }
}
