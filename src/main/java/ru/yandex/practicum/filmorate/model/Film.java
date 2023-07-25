package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
public class Film {
    private final String name;
    private final LocalDate releaseDate;
    private final long duration;
    private Set<Integer> likes;
    private int id;
    private String description;
    private Integer rate;

    public void addLike(int idOfUser) {
        if (likes == null) {
            likes = new HashSet<>(Set.of(idOfUser));
        } else {
            likes.add(idOfUser);
        }
    }

    public void deleteLike(int idOfUser) {
        likes.remove(idOfUser);
    }
}
