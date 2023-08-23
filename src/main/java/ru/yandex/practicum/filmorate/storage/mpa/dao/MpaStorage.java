package ru.yandex.practicum.filmorate.storage.mpa.dao;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

public interface MpaStorage {
    List<Mpa> findAll();

    Mpa getMpaById(Integer id);

    boolean containsMpa(Integer idOfMpa);
}
