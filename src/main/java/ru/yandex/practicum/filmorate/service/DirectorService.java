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

    public Collection<Director> findAll() {
        return storage.findAll();
    }

    public Director findById(Long id) throws DirectorDoesNotExistException {
        Director director = storage.findById(id);
        if (director == null) {
            throw new DirectorDoesNotExistException("Director with such id {" + id + "} does not exist", 404);
        }
        return director;
    }

    public Director create(Director director) throws DirectorAlreadyExistException {
        if (storage.contains(director)) {
            String message = String.format("Such director with id {%d} or name {%S} already exists",
                    director.getId(),
                    director.getName());
            throw new DirectorAlreadyExistException(message);
        }
        return storage.create(director);
    }

    public Director update(Director director) throws DirectorDoesNotExistException {
        if (!storage.contains(director.getId())) {
            String message = String.format("Director with such id {%s} does not exist", director.getId());
            throw new DirectorDoesNotExistException(message);
        }
        return storage.update(director);
    }

    public void deleteById(Long id) {
        storage.deleteById(id);
    }
}
