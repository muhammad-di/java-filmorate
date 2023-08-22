package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Builder
public class Film {
    private final String name;
    private final LocalDate releaseDate;
    private final long duration;
    private Set<Long> likes;
    private long id;
    private String description;
    private List<Genre> genres;
    private Mpa mpa;
    private int rate;

    public void addLike(long idOfUser) {
        if (likes == null) {
            likes = new HashSet<>(Set.of(idOfUser));
        } else {
            likes.add(idOfUser);
        }
    }

    public void deleteLike(long idOfUser) {
        likes.remove(idOfUser);
    }
}