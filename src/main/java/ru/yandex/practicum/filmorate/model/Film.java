package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.time.LocalDate;

@Data
@Builder
public class Film {
    @Builder.Default
    private int id = 1;
    private final String name;
    private final LocalDate releaseDate;
    private final long duration;
    @NonNull
    private String description;
}
