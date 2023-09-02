package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DirectorAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.DirectorDoesNotExistException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.director.dao.DirectorStorage;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class DirectorService {
    @Autowired
    private final DirectorStorage storage;

    public Collection<Director> getAllDirectors() {
        return storage.getAllDirectors();
    }

    public Director getDirectorById(Long id) throws DirectorDoesNotExistException {
        if (!storage.containsDirector(id)) {
            throw new DirectorDoesNotExistException("Director with such id {" + id + "} does not exist", 404);
        }
        return storage.getDirectorById(id);
    }

    public Director createDirector(Director director) throws DirectorAlreadyExistException {
        if (storage.containsDirector(director.getId())) {
            String message = String.format("A director with such id {%s} already exists", director.getId());
            throw new DirectorAlreadyExistException(message);
        }
        if (storage.containsDirector(director.getName())) {
            String message = String.format("A director with such name {%s} already exists", director.getName());
            throw new DirectorAlreadyExistException(message);
        }
        return storage.createDirector(director);
    }

    public Director updateDirector(Director director) throws DirectorDoesNotExistException {
        if (!storage.containsDirector(director.getId())) {
            String message = String.format("Director with such id {%s} does not exist", director.getId());
            throw new DirectorDoesNotExistException(message);
        }
        return storage.updateDirector(director);
    }

}
