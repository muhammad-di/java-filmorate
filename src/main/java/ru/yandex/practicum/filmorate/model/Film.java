package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.*;

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
    private List<Director> directors;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Film film = (Film) o;
        return id == film.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}