package ru.yandex.practicum.filmorate.storage.mpa.dao;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;

public interface MpaStorage {
    Collection<Mpa> findAll();

    Mpa getMpaById(Integer id);

    Boolean containsMpa(Integer idOfMpa);
}
