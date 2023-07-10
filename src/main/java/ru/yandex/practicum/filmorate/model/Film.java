package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.time.LocalDate;

@Data
@Builder
public class Film {
    private final String name;
    private final LocalDate releaseDate;
    private final long duration;
    private int id;
    @NonNull
    private String description;
}
