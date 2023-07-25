package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Integer, Film> films = new ConcurrentHashMap<>();

    public Collection<Film> findAll() {
        return new ArrayList<>(films.values());
    }

    public Film create(Film film) {
        films.put(film.getId(), film);
        log.info("Film entity with id {} and name {} was created", film.getId(), film.getName());
        return films.get(film.getId());
    }

    public Film update(Film film) {
        films.put(film.getId(), film);
        log.info("Film entity with id {} and name {} was updated", film.getId(), film.getName());
        return films.get(film.getId());
    }

    public void addLike(int idOfFilm, int idOfUser) {
        Film film = films.get(idOfFilm);

        film.addLike(idOfUser);
    }

    public void deleteLike(int idOfFilm, int idOfUser) {
        Film film = films.get(idOfFilm);

        film.deleteLike(idOfUser);
    }

    public boolean containsFilm(int idOfFilm) {
        return films.containsKey(idOfFilm);
    }

    public Film getFilmById(Integer id) {
        return films.getOrDefault(id, null);
    }
}
