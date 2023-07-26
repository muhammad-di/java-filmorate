package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

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

    public Collection<Film> getMostPopularFilms(Integer count) {
        List<Film> list = films.values().stream()
                .sorted((x, y) -> {
                    if (x.getLikes() == null) return -1;
                    if (y.getLikes() == null) return 1;
                    return y.getLikes().size() - x.getLikes().size();
                })
                .collect(Collectors.toList());
        if (list.size() <= count) {
            return list;
        };
        return list.subList(list.size() - count, list.size());
    }

    public boolean containsFilm(int idOfFilm) {
        return films.containsKey(idOfFilm);
    }

    public Film getFilmById(Integer id) {
        return films.getOrDefault(id, null);
    }
}
