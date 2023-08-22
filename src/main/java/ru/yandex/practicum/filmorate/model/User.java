package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Data
@Builder
public class User {
    private final String login;
    private final LocalDate birthday;
    private Map<Long, Boolean> friends;
    private long id;
    @NonNull
    private String email;
    private String name;

    public void addFriend(Long idOfFriend) {
        if (friends == null) {
            friends = new HashMap<>(Map.of(idOfFriend, false));
        } else {
            friends.put(idOfFriend, false);
        }
    }

    public void deleteFriend(Long idOfFriend) {
        friends.remove(idOfFriend);
    }
}
