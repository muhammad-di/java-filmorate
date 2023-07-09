package ru.yandex.practicum.filmorate.model;

import lombok.*;

import java.time.LocalDate;


@Setter
@Builder
@AllArgsConstructor
public class FilmDto {
    private final String name;
    private final LocalDate releaseDate;
    private final long duration;
    private String description;
}