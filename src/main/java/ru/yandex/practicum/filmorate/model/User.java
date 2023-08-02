package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
public class User {
    private final String login;
    private final LocalDate birthday;
    private Set<Integer> friends;
    private int id;
    @NonNull
    private String email;
    private String name;

    public void addFriend(int idOfFriend) {
        if (friends == null) {
            friends = new HashSet<>(Set.of(idOfFriend));
        } else {
            friends.add(idOfFriend);
        }
    }

    public void deleteFriend(int idOfFriend) {
        friends.remove(idOfFriend);
    }
}
