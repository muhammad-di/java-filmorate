package ru.yandex.practicum.filmorate.storage.mpa.dao;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;

public interface MpaStorage {
    Collection<Mpa> findAll();

    Mpa findById(Integer id);

    Boolean contains(Integer idOfMpa);
}
