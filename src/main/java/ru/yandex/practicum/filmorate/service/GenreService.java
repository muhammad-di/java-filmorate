package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.GenreDoesNotExistException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.dao.GenreStorage;

import java.util.Collection;

@Slf4j
@Service
public class GenreService {
    private final GenreStorage storage;

    @Autowired
    public GenreService(GenreStorage storage) {
        this.storage = storage;
    }

    public Collection<Genre> findAll() {
        return storage.findAll();
    }

    public Genre findById(Integer id) throws GenreDoesNotExistException {
        Genre genre = storage.findById(id);
        if (genre == null) {
            throw new GenreDoesNotExistException("genre rating with such id {" + id + "} does not exist", 404);
        }
        return genre;
    }
}
