package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Integer, User> users = new ConcurrentHashMap<>();

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

    public Collection<User> getAllFriends(Integer id) {
        return users.get(id).getFriends().stream()
                .map(users::get)
                .collect(Collectors.toList());
    }

    public void addFriend(int id, int idOfFriend) {
        User user = users.get(id);
        User friend = users.get(idOfFriend);

        user.addFriend(idOfFriend);
        friend.addFriend(id);
    }

    public void deleteFriend(int id, int idOfFriend) {
        User user = users.get(id);
        User friend = users.get(idOfFriend);

        user.deleteFriend(idOfFriend);
        friend.deleteFriend(id);
    }

    public Collection<User> getCommonFriends(int id, int idOfFriend) {
        User user = users.get(id);
        User friend = users.get(idOfFriend);

        if (user.getFriends() == null || friend.getFriends() == null) {
            return Collections.emptySet();
        }
        Set<Integer> common = new HashSet<>(user.getFriends());
        common.retainAll(friend.getFriends());
        return common.stream()
                .map(users::get)
                .collect(Collectors.toList());
    }

    public boolean containsUser(int idOfUser) {
        return users.containsKey(idOfUser);
    }

    public User getUserById(Integer id) {
        return users.getOrDefault(id, null);
    }

    ;

}
