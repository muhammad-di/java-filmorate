package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.time.LocalDate;

@Data
@Builder
public class Film {
    private final int id;
    private final String name;
    @NonNull
    private String description;
    private final LocalDate releaseDate;
    private final long duration;
}
