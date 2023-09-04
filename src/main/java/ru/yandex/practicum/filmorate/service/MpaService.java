package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.MpaDoesNotExistException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpa.dao.MpaStorage;

import java.util.Collection;

@Slf4j
@Service
public class MpaService {
    private final MpaStorage storage;

    @Autowired
    public MpaService(MpaStorage storage) {
        this.storage = storage;
    }

    public Collection<Mpa> findAll() {
        return storage.findAll();
    }

    public Mpa getMpaById(Integer id) throws MpaDoesNotExistException {
        if (!storage.containsMpa(id)) {
            throw new MpaDoesNotExistException("mpa rating with such id {" + id + "} does not exist", 404);
        }
        return storage.getMpaById(id);
    }
}
