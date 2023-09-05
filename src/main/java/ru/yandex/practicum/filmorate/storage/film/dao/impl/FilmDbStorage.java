package ru.yandex.practicum.filmorate.storage.film.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.storage.film.dao.FilmStorage;

import java.sql.*;
import java.sql.Date;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Repository
@Primary
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<Film> findAll() {
        String sqlQuery = "SELECT\n" +
                "          f.film_id,\n" +
                "          f.name,\n" +
                "          f.release_date,\n" +
                "          f.duration,\n" +
                "          f.description,\n" +
                "          f.mpa AS rating_id,\n" +
                "          m.name AS rating_name\n" +
                "          FROM film f\n" +
                "          INNER JOIN mpa m ON f.mpa = m.rating_id";

        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm);
    }

    @Override
    public Film create(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("film")
                .usingGeneratedKeyColumns("film_id");
        long filmId = simpleJdbcInsert.executeAndReturnKey(film.toMap()).longValue();

        film.setId(filmId);
        setLikes(film);
        setGenres(film);
        setRating(film);
        setDirector(film);

        return findById(film.getId());
    }

    @Override
    public Film update(Film film) {
        String sqlQuery = "UPDATE film SET \n" +
                "name = ?,\n" +
                "release_date = ?,\n" +
                "duration = ?,\n" +
                "description = ?, \n" +
                "mpa = ? \n" +
                "WHERE \n" +
                "film_id = ?";

        jdbcTemplate.update(sqlQuery,
                film.getName(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getDescription(),
                film.getMpa().getId(),
                film.getId()
        );
        updateLikes(film);
        updateGenre(film);
        updateDirector(film);
        return findById(film.getId());
    }

    @Override
    public void addLike(Long idOfFilm, Long idOfUser) {
        String sqlQuery = "INSERT INTO likes (film_id, user_id) " +
                "VALUES (?, ?)";

        jdbcTemplate.update(sqlQuery, idOfFilm, idOfUser);
        setFeedEvent(idOfUser, idOfFilm, Operation.ADD);
    }

    @Override
    public void deleteLike(Long idOfFilm, Long idOfUser) {
        String sqlQuery = "DELETE FROM likes " +
                "WHERE " +
                "film_id = ? " +
                "AND " +
                "user_id = ?";

        jdbcTemplate.update(sqlQuery, idOfFilm, idOfUser);
        setFeedEvent(idOfUser, idOfFilm, Operation.REMOVE);
    }

    @Override
    public Collection<Film> findMostPopularFilms(Integer count, Integer genreId, Integer year) {
        String sqlQuery = "SELECT " +
                "          t.film_id,\n" +
                "          t.name,\n" +
                "          t.description,\n" +
                "          t.release_date, \n" +
                "          t.duration, \n" +
                "          t.rating_id,\n" +
                "          t.rating_name \n" +
                "          FROM \n" +
                "          (" +
                "               SELECT" +
                "               f.film_id, \n" +
                "               f.name, \n" +
                "               f.description, \n" +
                "               f.release_date, \n" +
                "               f.duration, \n" +
                "               f.mpa AS rating_id, \n" +
                "               m.name AS rating_name \n" +
                "               FROM film AS f \n" +
                "               JOIN mpa m ON f.mpa = m.rating_id" +
                "          ) AS t \n" +
                "          LEFT JOIN likes AS l ON t.film_id = l.film_id \n" +
                "          LEFT JOIN film_genre AS fg ON t.film_id = fg.film_id \n";
        if (genreId == null && year == null) {
            sqlQuery = sqlQuery + "GROUP BY t.film_id \n" +
                    "              ORDER BY COUNT(l.user_id) DESC \n" +
                    "              LIMIT ?;";
            return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, count);
        } else if (genreId != null && year == null) {
            sqlQuery = sqlQuery + "WHERE fg.genre_id = ? \n" +
                    "              GROUP BY t.film_id \n" +
                    "              ORDER BY COUNT(l.user_id) DESC \n" +
                    "              LIMIT ?;";
            return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, genreId, count);
        } else if (genreId == null && year != null) {
            sqlQuery = sqlQuery + "WHERE EXTRACT(YEAR FROM CAST(t.release_date AS DATE)) = ?\n" +
                    "              GROUP BY t.film_id \n" +
                    "              ORDER BY COUNT(l.user_id) DESC \n" +
                    "              LIMIT ?;";
            return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, year, count);
        } else {
            sqlQuery = sqlQuery + "WHERE fg.genre_id = ? AND \n" +
                    "              EXTRACT(YEAR FROM CAST(t.release_date AS DATE)) = ?\n" +
                    "              GROUP BY t.film_id \n" +
                    "              ORDER BY COUNT(l.user_id) DESC \n" +
                    "              LIMIT ?;";
            return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, genreId, year, count);
        }
    }

    public List<Film> findCommonFilms(Integer userId, Integer friendId) {
        final String qs = "SELECT" +
                "          COUNT(l.user_id) AS counter," +
                "          req.film_id, name," +
                "          release_date, duration," +
                "          description, rating_id," +
                "          rating_name\n" +
                "          FROM " +
                "          (\n" +
                "               SELECT COUNT(l.user_id)," +
                "               f.film_id," +
                "               f.name," +
                "               f.release_date," +
                "               f.duration, description," +
                "               mpa.rating_id AS rating_id," +
                "               mpa.name AS rating_name\n" +
                "               FROM film f\n" +
                "               JOIN mpa ON mpa.rating_id = f.mpa\n" +
                "               JOIN likes l ON f.film_id = l.film_id\n" +
                "               WHERE l.user_id in (?, ?)\n" +
                "               GROUP BY f.film_id " +
                "               HAVING COUNT(l.user_id) > 1" +
                "          ) AS req\n" +
                "          JOIN likes l ON l.film_id = req.film_id\n" +
                "          GROUP BY req.film_id" +
                "          ORDER BY counter DESC;";

        try {
            return jdbcTemplate.query(qs, this::mapRowToFilm, userId, friendId);
        } catch (DataAccessException e) {
            throw new FilmNotFoundException("Фильм не найден");
        }
    }

    @Override
    public Boolean contains(Long idOfFilm) {
        String sqlQuery = "SELECT EXISTS(SELECT 1 FROM film WHERE film_id = ?) AS is_film";

        return jdbcTemplate.queryForObject(sqlQuery, (rs, rn) -> rs.getBoolean("is_film"), idOfFilm);
    }

    @Override
    public Film findById(Long id) {
        String sqlQuery = "SELECT\n" +
                "          f.film_id,\n" +
                "          f.name,\n" +
                "          f.release_date,\n" +
                "          f.duration,\n" +
                "          f.description,\n" +
                "          f.mpa AS rating_id,\n" +
                "          m.name AS rating_name\n" +
                "          FROM film AS f\n" +
                "          INNER JOIN mpa m ON f.mpa = m.rating_id\n" +
                "          WHERE f.film_id = ?";

        return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToFilm, id);
    }

    @Override
    public void deleteById(Long id) {
        String sqlQuery = "DELETE FROM film " +
                "          WHERE film_id = ?";

        jdbcTemplate.update(sqlQuery, id);
        log.info("Фильм с идентификатором {} удален.", id);
    }

    @Override
    public List<Film> findFilmsByIds(Set<Long> idsFilmsForRecommendations) {
        String inSql = String.join(",", Collections.nCopies(idsFilmsForRecommendations.size(), "?"));
        return jdbcTemplate.query(
                String.format("SELECT DISTINCT f.film_id,\n" +
                        "f.name,\n" +
                        "f.release_date,\n" +
                        "f.duration,\n" +
                        "f.description,\n" +
                        "f.mpa AS rating_id,\n" +
                        "m.name AS rating_name\n" +
                        "FROM film AS f\n" +
                        "INNER JOIN mpa m ON f.mpa = m.rating_id\n" +
                        "WHERE f.film_id IN (%s)\n" +
                        "ORDER BY f.film_id", inSql),
                idsFilmsForRecommendations.toArray(),
                this::mapRowToFilm);
    }

    @Override
    public List<Film> findFilmBySearchByTitleOrDirector(String title, boolean isDirectorCheck, boolean isTitleCheck) {
        List<String> params = new ArrayList<>();
        title = "%".concat(title).concat("%");
        String qs = "SELECT " +
                "    DISTINCT COUNT(l.user_id)  AS likes, " +
                "    f.film_id, " +
                "    f.name, " +
                "    f.release_date, " +
                "    f.duration, " +
                "    description, " +
                "    mpa.rating_id AS rating_id, " +
                "    mpa.name AS rating_name " +
                "    FROM film AS f " +
                "    JOIN mpa ON mpa.rating_id = f.mpa  " +
                "    LEFT JOIN likes l ON f.film_id = l.film_id";
        if (isDirectorCheck) {
            qs = qs.concat(" LEFT JOIN film_director AS fd ON f.film_id = fd.film_id " +
                    "             LEFT JOIN director AS d ON d.director_id = fd.director_id " +
                    "             WHERE LOWER(d.name) like LOWER(?)");
            params.add(title);
        }
        if (isTitleCheck) {
            params.add(title);
            if (!isDirectorCheck) {
                qs = qs.concat(" WHERE LOWER(f.name) like LOWER(?)");
            } else {
                qs = qs.concat(" OR LOWER(f.name) like LOWER(?)");
            }
        }
        qs = qs.concat(" GROUP BY f.film_id" +
                "             ORDER BY COUNT(l.user_id) DESC;");

        try {
            log.info("qs = " + qs);
            return jdbcTemplate.query(qs, this::mapRowToFilm, params.toArray());
        } catch (DataAccessException e) {
            throw new FilmNotFoundException("Фильм не найден");
        }
    }

    @Override
    public Collection<Film> findFilmsWithDirectorIdSortedByLikes(Long directorId) {
        String sqlQueryWhenLikesArePresent = "SELECT\n" +
                "                             f.film_id,\n" +
                "                             f.name,\n" +
                "                             f.release_date,\n" +
                "                             f.duration,\n" +
                "                             f.description,\n" +
                "                             f.mpa rating_id,\n" +
                "                             m.name rating_name,\n" +
                "                             COUNT(l.film_id)\n" +
                "                             FROM film f\n" +
                "                             JOIN likes l ON l.film_id = f.film_id\n" +
                "                             JOIN mpa m ON f.mpa = m.rating_id\n" +
                "                             GROUP BY l.film_id\n" +
                "                             HAVING f.film_id IN " +
                "                             (" +
                "                                  SELECT film_id" +
                "                                  FROM film_director " +
                "                                  WHERE director_id = ?" +
                "                             )\n" +
                "                             ORDER BY COUNT(l.film_id) DESC";

        String sqlQueryWhenLikesAbsent = "    SELECT\n" +
                "                             f.film_id,\n" +
                "                             f.name,\n" +
                "                             f.release_date,\n" +
                "                             f.duration,\n" +
                "                             f.description,\n" +
                "                             f.mpa rating_id,\n" +
                "                             m.name rating_name\n" +
                "                             FROM film f\n" +
                "                             JOIN mpa m ON f.mpa = m.rating_id\n" +
                "                             WHERE f.film_id IN " +
                "                             (" +
                "                                  SELECT film_id " +
                "                                  FROM film_director " +
                "                                  WHERE director_id = ?" +
                "                             )";

        Collection<Film> list = jdbcTemplate.query(sqlQueryWhenLikesArePresent, this::mapRowToFilm, directorId);
        if (CollectionUtils.isEmpty(list)) {
            list = jdbcTemplate.query(sqlQueryWhenLikesAbsent, this::mapRowToFilm, directorId);
        }

        return list;
    }

    @Override
    public Collection<Film> findFilmsWithDirectorIdSortedByYear(Long directorId) {
        String sqlQuery = "SELECT\n" +
                "          f.film_id,\n" +
                "          f.name,\n" +
                "          f.release_date,\n" +
                "          f.duration,\n" +
                "          f.description,\n" +
                "          f.mpa rating_id,\n" +
                "          m.name rating_name,\n" +
                "          EXTRACT(YEAR FROM CAST(release_date AS DATE)) AS the_year\n" +
                "          FROM film f\n" +
                "          JOIN mpa m ON f.mpa = m.rating_id\n" +
                "          WHERE f.film_id IN (SELECT film_id FROM film_director WHERE director_id = ?)\n" +
                "          ORDER BY the_year";

        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, directorId);
    }

    // helpers methods for a CREATE method------------------------------------------------------------------------------

    private Film mapRowToFilm(ResultSet rs, Integer rowNum) throws SQLException {
        long id = rs.getLong("film_id");
        String name = rs.getString("name");
        LocalDate releaseDate = rs.getDate("release_date").toLocalDate();
        long duration = rs.getLong("duration");
        String description = rs.getString("description");
        Integer mpaId = rs.getInt("rating_id");
        String ratingName = rs.getString("rating_name");
        Set<Long> likes = makeLikesSet(id);
        List<Genre> genre = makeGenreList(id);
        Collection<Director> directors = getDirectors(id);

        return Film.builder()
                .id(id)
                .name(name)
                .releaseDate(releaseDate)
                .duration(duration)
                .description(description)
                .mpa(Mpa.builder()
                        .id(mpaId)
                        .name(ratingName)
                        .build())
                .likes(likes)
                .genres(genre)
                .directors(directors)
                .build();
    }

    /////////////////// CREATE LIKES -----------------------------------------------------------------------------------

    private Set<Long> makeLikesSet(long id) {
        String sqlQuery = "SELECT\n" +
                "          l.user_id\n" +
                "          FROM film AS f\n" +
                "          INNER JOIN likes AS l ON l.film_id = f.film_id\n" +
                "          WHERE f.film_id = ?";

        return jdbcTemplate.query(sqlQuery, rs -> {
            Set<Long> setOfId = new HashSet<>();

            while (rs.next()) {
                Long userId = rs.getLong("user_id");
                setOfId.add(userId);
            }
            return setOfId;
        }, id);
    }

    private void setLikes(Film film) {
        if (CollectionUtils.isEmpty(film.getLikes())) {
            film.setLikes(Collections.emptySet());
            return;
        }
        String sqlQuery = "INSERT INTO likes (film_id, user_id) " +
                "          VALUES (?, ?)";

        film.getLikes().forEach(userId -> jdbcTemplate.update(sqlQuery, film.getId(), userId));
    }

    /////////////////// CREATE GENRE -----------------------------------------------------------------------------------

    private List<Genre> makeGenreList(long id) {
        String sqlQuery = "SELECT\n" +
                "          g.name AS genre_name,\n" +
                "          g.genre_id AS id\n" +
                "          FROM film AS f\n" +
                "          INNER JOIN film_genre AS fm ON f.film_id = fm.film_id\n" +
                "          INNER JOIN genre AS g ON g.genre_id = fm.genre_id\n" +
                "          WHERE f.film_id = ?";

        return jdbcTemplate.query(sqlQuery, rs -> {
            List<Genre> listOfGenre = new ArrayList<>();

            while (rs.next()) {
                Integer genreId = rs.getInt("id");
                String genreName = rs.getString("genre_name");
                Genre genre = new Genre(genreId, genreName);
                listOfGenre.add(genre);
            }
            return listOfGenre;
        }, id);
    }

    private String getName(String sqlQuery, Long id) {
        return jdbcTemplate.queryForObject(sqlQuery,
                (rs, rn) -> rs.getString("name"), id);
    }

    /////////////////// CREATE RATING ----------------------------------------------------------------------------------

    private void setRating(Film film) {
        if (!film.getMpa().containsValidId()) {
            film.setMpa(Mpa.builder().build());
            return;
        }
        String ratingName = getRatingName(film);
        film.getMpa().setName(ratingName);
    }

    private String getRatingName(Film film) {
        String sqlQuery = "SELECT\n" +
                "          m.name\n" +
                "          FROM mpa m\n" +
                "          WHERE m.rating_id = ?";

        return getName(sqlQuery, (long) film.getMpa().getId());
    }

    /////////////////// CREATE DIRECTOR --------------------------------------------------------------------------------

    private Collection<Director> setDirectorsNames(Film film) {
        return film.getDirectors()
                .stream()
                .peek(director -> {
                    String name = getDirectorName(director);
                    director.setName(name);
                }).collect(Collectors.toList());
    }

    private String getDirectorName(Director director) {
        String sqlQuery = "SELECT \n" +
                "          d.name\n" +
                "          FROM director d\n" +
                "          WHERE d.director_id = ?";

        return getName(sqlQuery, director.getId());
    }

    private void setDirector(Film film) {
        if (CollectionUtils.isEmpty(film.getDirectors())) {
            return;
        }
        Collection<Director> directors = setDirectorsNames(film);
        String sqlQuery = "INSERT INTO film_director (film_id, director_id)\n" +
                "          VALUES (?, ?)";

        film.setDirectors(directors);
        directors.forEach(director -> jdbcTemplate.update(sqlQuery, film.getId(), director.getId()));
    }

    // helpers methods for a UPDATE method------------------------------------------------------------------------------

    /////////////////// UPDATE LIKES -----------------------------------------------------------------------------------

    private void updateLikes(Film film) {
        if (CollectionUtils.isEmpty(film.getLikes())) {
            film.setLikes(Collections.emptySet());
            return;
        }
        Set<Long> likesToInsert = new HashSet<>(film.getLikes());
        Set<Long> likesToDelete = new HashSet<>(getLikes(film));
        Set<Long> commonLikes = likesToDelete.stream()
                .filter(likesToInsert::contains)
                .collect(Collectors.toSet());

        likesToInsert.removeAll(commonLikes);
        likesToDelete.removeAll(commonLikes);
        if (!CollectionUtils.isEmpty(likesToInsert)) {
            insertLikes(film, likesToInsert);
        }
        if (!CollectionUtils.isEmpty(likesToDelete)) {
            deleteLikes(film, likesToDelete);
        }
    }

    private Set<Long> getLikes(Film film) {
        String sqlQuery = "SELECT \n" +
                "          user_id\n" +
                "          FROM\n" +
                "          likes\n" +
                "          WHERE film_id = ?";

        return jdbcTemplate.query(sqlQuery, rs -> {
            Set<Long> setOfLikes = new HashSet<>();
            while (rs.next()) {
                Long userId = rs.getLong("user_id");
                setOfLikes.add(userId);
            }
            return setOfLikes;
        }, film.getId());
    }

    private void insertLikes(Film film, Set<Long> likes) {
        likes.forEach(userId -> addLike(film.getId(), userId));
    }

    private void deleteLikes(Film film, Set<Long> likes) {
        likes.forEach(userId -> deleteLike(film.getId(), userId));
    }

    /////////////////// UPDATE GENRE -----------------------------------------------------------------------------------

    private void updateGenre(Film film) {
        if (CollectionUtils.isEmpty(film.getGenres())) {
            film.setGenres(Collections.emptyList());
            deleteGenres(film);
        }
        Set<Genre> genresToInsert = new HashSet<>(film.getGenres());
        Set<Genre> genresToDelete = new HashSet<>(getGenres(film));
        Set<Genre> commonGenres = genresToDelete.stream()
                .filter(genresToInsert::contains)
                .collect(Collectors.toSet());

        genresToInsert.removeAll(commonGenres);
        genresToDelete.removeAll(commonGenres);
        deleteGenres(film, genresToDelete);
        setGenres(film, genresToInsert);
    }

    private List<Genre> getGenres(Film film) {
        String sqlQuery = "SELECT\n" +
                "          g.name AS genre_name,\n" +
                "          g.genre_id AS id\n" +
                "          FROM film f \n" +
                "          INNER JOIN film_genre fg ON fg.film_id = f.film_id\n" +
                "          INNER JOIN genre g ON fg.genre_id  = g.genre_id\n" +
                "          WHERE f.film_id  = ?";

        return jdbcTemplate.query(sqlQuery, (rs, rn) -> {
            Integer genreId = rs.getInt("id");
            String genreName = rs.getString("genre_name");
            return new Genre(genreId, genreName);
        }, film.getId());
    }

    private void setGenres(Film film, Collection<Genre> genres) {
        String sqlQuery = "INSERT INTO film_genre (film_id, genre_id) " +
                "          VALUES (?, ?)";

        genres.forEach(genre -> jdbcTemplate.update(sqlQuery, film.getId(), genre.getId()));
    }

    private void setGenres(Film film) {
        if (CollectionUtils.isEmpty(film.getGenres())) {
            film.setGenres(Collections.emptyList());
            return;
        }
        setGenres(film, film.getGenres());
    }

    private void deleteGenres(Film film) {
        String sqlQuery = "DELETE FROM film_genre " +
                "WHERE " +
                "FILM_ID = ? ";

        jdbcTemplate.update(sqlQuery, film.getId());
    }

    private void deleteGenres(Film film, Collection<Genre> genres) {
        String sqlQuery = "DELETE FROM film_genre " +
                "          WHERE " +
                "          film_id = ?\n" +
                "          AND\n" +
                "          genre_id = ? ";

        genres.forEach(genre -> jdbcTemplate.update(sqlQuery, film.getId(), genre.getId()));
    }

    /////////////////// UPDATE DIRECTOR --------------------------------------------------------------------------------


    private void updateDirector(Film film) {
        if (CollectionUtils.isEmpty(film.getDirectors())) {
            film.setDirectors(Collections.emptyList());
            deleteDirectors(film);
            return;
        }
        Set<Director> directorsToInsert = new HashSet<>(film.getDirectors());
        Set<Director> directorsToDelete = new HashSet<>(getDirectors(film.getId()));
        Set<Director> commonDirectors = directorsToDelete.stream()
                .filter(directorsToInsert::contains)
                .collect(Collectors.toSet());
        Set<Director> newDirectors = new HashSet<>(commonDirectors);


        directorsToInsert.removeAll(commonDirectors);
        directorsToDelete.removeAll(commonDirectors);
        if (!CollectionUtils.isEmpty(directorsToInsert)) {
            insertDirectors(film, directorsToInsert);
            newDirectors.addAll(directorsToInsert);
        }
        if (!CollectionUtils.isEmpty(directorsToDelete)) {
            deleteDirectors(film, directorsToDelete);
        }
        film.setDirectors(newDirectors);
    }

    private Collection<Director> getDirectors(Long filmId) {
        String sqlQuery = "SELECT\n" +
                "          d.director_id,\n" +
                "          d.name\n" +
                "          FROM director d\n" +
                "          INNER JOIN film_director fd ON d.director_id = fd.director_id\n" +
                "          INNER JOIN film f ON f.film_id = fd.film_id\n" +
                "          WHERE f.film_id = ?";

        return jdbcTemplate.query(sqlQuery, (rs, rn) -> {
            Long id = rs.getLong("director_id");
            String name = rs.getString("name");
            return Director.builder()
                    .id(id)
                    .name(name)
                    .build();
        }, filmId);
    }


    private void insertDirectors(Film film, Collection<Director> directors) {
        directors.forEach(director -> addDirector(film.getId(), director.getId()));
    }

    private void deleteDirectors(Film film, Collection<Director> directors) {
        directors.forEach(director -> deleteDirector(film.getId(), director.getId()));
    }

    private void deleteDirectors(Film film) {
        String sqlQuery = "DELETE FROM film_director " +
                "          WHERE " +
                "          film_id = ?";

        jdbcTemplate.update(sqlQuery, film.getId());
    }

    private void addDirector(Long idOfFilm, Long idOfUser) {
        String sqlQuery = "INSERT INTO film_director (film_id, director_id) " +
                "          VALUES (?, ?)";

        jdbcTemplate.update(sqlQuery, idOfFilm, idOfUser);
    }

    private void deleteDirector(Long idOfFilm, Long idOfUser) {
        String sqlQuery = "DELETE FROM film_director " +
                "          WHERE " +
                "          film_id = ? " +
                "          AND " +
                "          director_id = ?";

        jdbcTemplate.update(sqlQuery, idOfFilm, idOfUser);
    }

    /////////////////// GET_FEED_OF_USER --------------------------------------------------------------------------------

    private void setFeedEvent(Long id, Long entityId, Operation operation) {
        Event feed = Event.builder()
                .userId(id)
                .entityId(entityId)
                .eventType(EventType.LIKE)
                .operation(operation)
                .timestamp(Instant.now().toEpochMilli())
                .build();
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("feed")
                .usingGeneratedKeyColumns("event_id");
        simpleJdbcInsert.execute(feed.toMap());
    }
}
