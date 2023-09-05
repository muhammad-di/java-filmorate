package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserDoesNotExistException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.dao.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.dao.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendationsService {
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;

    public List<Film> findRecommendedFilms(Long userId) throws UserDoesNotExistException {
        if (!userStorage.contains(userId)) {
            throw new UserDoesNotExistException("User " +
                    "with such id {" + userId + "} does not exist", 404);
        }

        Map<Long, List<Long>> allUsersWithTheirLikedFilm = userStorage.findAllUsersWithTheirLikedFilms();
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

            Set<Long> idsFilmsForRecommendations = idUsersBySimilarLikes.stream()
                    .flatMap(idUser -> allUsersWithTheirLikedFilm.get(idUser).stream())
                    .filter(filmId -> !allUsersWithTheirLikedFilm.get(userId).contains(filmId))
                    .collect(Collectors.toSet());
            return filmStorage.findFilmsByIds(idsFilmsForRecommendations);
        } else {
            return new ArrayList<>();
        }
    }
}



