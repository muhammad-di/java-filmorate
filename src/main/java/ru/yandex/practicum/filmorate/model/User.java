package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.time.LocalDate;

@Data
@Builder
public class User {
    private final String login;
    private final LocalDate birthday;
    @NonNull
    private  int id;
    @NonNull
    private String email;
    private String name;


}
