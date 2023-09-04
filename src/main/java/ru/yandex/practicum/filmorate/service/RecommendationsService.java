package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.dao.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.dao.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendationsService {
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;

    public Set<Film> findRecommendedFilms(Long userId) {
        Map<Long, Set<Long>> allUsersWithTheirLikedFilm = userStorage.findAll()
                .stream()
                .collect(Collectors.toMap(User::getId,
                        user -> filmStorage.findIdLikedFilmsByUser(user.getId())));

        List<Long> idUsersBySimilarLikes = new ArrayList<>();
        long maxCount = 0;

        for (Long curUserId : allUsersWithTheirLikedFilm.keySet()) {
            if (Objects.equals(curUserId, userId)) continue;

            long count = allUsersWithTheirLikedFilm.get(curUserId).stream()
                    .filter(filmId -> allUsersWithTheirLikedFilm.get(userId).contains(filmId)).count();

            if (count > 0 && count == maxCount) {
                idUsersBySimilarLikes.add(curUserId);
            }
            if (count > maxCount) {
                maxCount = count;
                idUsersBySimilarLikes.clear();
                idUsersBySimilarLikes.add(curUserId);
            }
        }

        if (maxCount > 0) {
            idUsersBySimilarLikes.sort(Comparator.naturalOrder());
            return idUsersBySimilarLikes.stream()
                    .flatMap(idUser -> filmStorage.findIdLikedFilmsByUser(idUser).stream())
                    .filter(filmId -> !allUsersWithTheirLikedFilm.get(userId).contains(filmId))
                    .map(filmStorage::findById)
                    .collect(Collectors.toSet());
        } else {
            return new HashSet<>();
        }
    }
}
