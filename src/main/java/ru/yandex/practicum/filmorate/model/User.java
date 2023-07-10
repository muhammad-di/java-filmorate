package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.time.LocalDate;

@Data
@Builder
public class User {
    @Builder.Default
    private int id = 0;
    private final String login;
    private final LocalDate birthday;
    @NonNull
    private String email;
    @NonNull
    private String name;
}
