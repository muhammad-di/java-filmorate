package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.time.LocalDate;

@Data
@Builder
public class User {
    private final int id;
    private final String login;
    private final LocalDate birthday;
    @NonNull
    private String email;
    @NonNull
    private String name;
}
