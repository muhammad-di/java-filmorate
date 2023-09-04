package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.*;

@Data
@Builder
public class Film {
    @NotNull(message = "Name is mandatory")
    @NotBlank(message = "Name can not be blank")
    private final String name;
    private final LocalDate releaseDate;
    @Min(value = 10, message = "Duration of the film should not be less than 10")
    private final long duration;
    private Set<Long> likes;
    @Min(value = 0, message = "Id should not be less than 0")
    private long id;

    @Size(min = 1, max = 200)
    private String description;
    private List<Genre> genres;
    private Mpa mpa;
    private Collection<Director> directors;
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

    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("name", name);
        values.put("release_date", releaseDate);
        values.put("duration", duration);
        values.put("description", description);
        values.put("mpa", mpa.getId());
        return values;
    }
}