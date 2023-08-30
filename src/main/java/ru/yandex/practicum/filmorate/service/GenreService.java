package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.GenreDoesNotExistException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.dao.GenreStorage;

import java.util.List;

@Slf4j
@Service
public class GenreService {
    private final GenreStorage storage;

    @Autowired
    public GenreService(GenreStorage storage) {
        this.storage = storage;
    }

    public List<Genre> findAll() {
        return storage.findAll();
    }

    public Genre getGenreById(Integer id) throws GenreDoesNotExistException {
        if (!storage.containsGenre(id)) {
            throw new GenreDoesNotExistException("genre rating with such id {" + id + "} does not exist", 404);
        }
        return storage.getGenreById(id);
    }
}
