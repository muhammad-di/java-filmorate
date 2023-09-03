package ru.yandex.practicum.filmorate.storage.film.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.dao.FilmStorage;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
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
                "f.FILM_ID,\n" +
                "f.NAME,\n" +
                "f.RELEASE_DATE,\n" +
                "f.DURATION,\n" +
                "f.DESCRIPTION,\n" +
                "f.MPA AS RATING_ID,\n" +
                "m.NAME AS RATING_NAME\n" +
                "FROM film f\n" +
                "INNER JOIN MPA m ON f.MPA = m.RATING_ID";

        return jdbcTemplate.query(sqlQuery, this::makeFilmList);
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

        return film;
    }

    @Override
    public Film update(Film film) {
        String sqlQuery = "UPDATE FILM SET \n" +
                "NAME = ?,\n" +
                "RELEASE_DATE = ?,\n" +
                "DURATION = ?,\n" +
                "DESCRIPTION = ?, \n" +
                "MPA = ? \n" +
                "WHERE \n" +
                "FILM_ID = ?";

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
        return getFilmById(film.getId());
    }

    @Override
    public void addLike(Long idOfFilm, Long idOfUser) {
        String sqlQuery = "INSERT INTO likes (FILM_ID, USER_ID) " +
                "VALUES (?, ?)";

        jdbcTemplate.update(sqlQuery, idOfFilm, idOfUser);
    }

    @Override
    public void deleteLike(Long idOfFilm, Long idOfUser) {
        String sqlQuery = "DELETE FROM likes " +
                "WHERE " +
                "FILM_ID = ? " +
                "AND " +
                "USER_ID = ?";

        jdbcTemplate.update(sqlQuery, idOfFilm, idOfUser);
    }

    @Override
    public Collection<Film> getMostPopularFilms(Integer count, Integer genreId, Integer year) {
        String sqlQuery = "SELECT t.film_id,\n" +
                "t.name,\n" +
                "t.description,\n" +
                "t.release_date, \n" +
                "t.duration, \n" +
                "t.RATING_ID,\n" +
                "t.RATING_NAME \n" +
                "FROM \n" +
                "(SELECT f.film_id, \n" +
                "f.name, \n" +
                "f.description, \n" +
                "f.release_date, \n" +
                "f.duration, \n" +
                "f.mpa AS RATING_ID, \n" +
                "m.NAME AS RATING_NAME \n" +
                "FROM film AS f \n" +
                "JOIN MPA m ON f.MPA = m.RATING_ID) AS t \n" +
                "LEFT JOIN likes AS l ON t.film_id = l.film_id \n" +
                "LEFT JOIN FILM_GENRE AS fg ON t.film_id = fg.FILM_ID \n";
        if (genreId == null && year == null) {
            sqlQuery = sqlQuery + "GROUP BY t.film_id \n" +
                    "ORDER BY COUNT(l.user_id) DESC \n" +
                    "LIMIT ?;";
            return jdbcTemplate.query(sqlQuery, this::makeFilmList, count);
        } else if (genreId != null && year == null) {
            sqlQuery = sqlQuery + "WHERE fg.GENRE_ID = ? \n" +
                    "GROUP BY t.film_id \n" +
                    "ORDER BY COUNT(l.user_id) DESC \n" +
                    "LIMIT ?;";
            return jdbcTemplate.query(sqlQuery, this::makeFilmList, genreId, count);
        } else if (genreId == null && year != null) {
            sqlQuery = sqlQuery + "WHERE EXTRACT(YEAR FROM CAST(t.release_date AS date)) = ?\n" +
                    "GROUP BY t.film_id \n" +
                    "ORDER BY COUNT(l.user_id) DESC \n" +
                    "LIMIT ?;";
            return jdbcTemplate.query(sqlQuery, this::makeFilmList, year, count);
        } else {
            sqlQuery = sqlQuery + "WHERE fg.GENRE_ID = ? AND \n" +
                    "EXTRACT(YEAR FROM CAST(t.release_date AS date)) = ?\n" +
                    "GROUP BY t.film_id \n" +
                    "ORDER BY COUNT(l.user_id) DESC \n" +
                    "LIMIT ?;";
            return jdbcTemplate.query(sqlQuery, this::makeFilmList, genreId, year, count);
        }
    }

    public List<Film> getCommonFilms(Integer userId, Integer friendId) {
        final String qs = "select count(l.ID) as counter, req.film_id, name, release_date, duration, description, RATING_ID, RATING_NAME\n" +
                "from (\n" +
                "select count(l.USER_ID), f.film_id, f.name, f.release_date, f.duration, description, mpa.RATING_ID as RATING_ID, mpa.NAME as RATING_NAME\n" +
                "         from FILM f\n" +
                "         join MPA on mpa.RATING_ID = f.MPA\n" +
                "         join LIKES l on f.FILM_ID = l.FILM_ID\n" +
                "         where l.USER_ID in (?, ?)\n" +
                "         group by f.FILM_ID having count(l.USER_ID) > 1) as req\n" +
                "join LIKES l on l.FILM_ID = req.FILM_ID\n" +
                "group by req.FILM_ID order by counter desc;";

        try {
            return jdbcTemplate.query(qs, this::makeFilmList, userId, friendId);
        } catch (DataAccessException e) {
            throw new FilmNotFoundException("Фильм не найден");
        }
    }

    @Override
    public boolean containsFilm(Long idOfFilm) {
        String sqlQuery = "SELECT \n" +
                "f.FILM_ID\n" +
                "FROM\n" +
                "FILM f\n" +
                "WHERE f.FILM_ID = ?";

        SqlRowSet sqlRows = jdbcTemplate.queryForRowSet(sqlQuery, idOfFilm);
        if (sqlRows.next()) {
            log.info("Найден фильм c id: {}", idOfFilm);
            return true;
        } else {
            log.info("Фильм с идентификатором {} не найден.", idOfFilm);
            return false;
        }
    }

    @Override
    public Film getFilmById(Long id) {
        String sqlQuery = "SELECT\n" +
                "f.FILM_ID,\n" +
                "f.NAME,\n" +
                "f.RELEASE_DATE,\n" +
                "f.DURATION,\n" +
                "f.DESCRIPTION,\n" +
                "f.MPA AS RATING_ID,\n" +
                "m.NAME AS RATING_NAME\n" +
                "FROM film AS f\n" +
                "INNER JOIN MPA m ON f.MPA = m.RATING_ID\n" +
                "WHERE f.FILM_ID = ?";

        return jdbcTemplate.queryForObject(sqlQuery, this::makeFilm, id);
    }

    @Override
    public Set<Long> getIdLikedFilmsByUser(Long id) {
        String sqlQuery = "SELECT film_id " +
                "FROM likes " +
                "WHERE user_id = ?";

        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sqlQuery, id);
        Set<Long> likes = new HashSet<>();
        while (sqlRowSet.next()) {
            likes.add(sqlRowSet.getLong("film_id"));
        }
        return likes;
    }

    @Override
    public void deleteFilmById(Long id) {
        String sqlQuery = "DELETE FROM film " +
                "WHERE " +
                "FILM_ID = ?";

        jdbcTemplate.update(sqlQuery, id);
        log.info("Фильм с идентификатором {} удален.", id);
    }

    @Override
    public List<Film> getFilmBySearchByTitleOrDirector(String title, boolean isDirectorCheck, boolean isTitleCheck) {
        List<String> params = new ArrayList<>();
        title = "%".concat(title).concat("%");
        String qs = "select distinct count(l.USER_ID)  as likes, f.film_id, f.name, f.release_date, f.duration, description, mpa.RATING_ID as RATING_ID, mpa.NAME as RATING_NAME from FILM as f join MPA on mpa.RATING_ID = f.MPA left join LIKES l on f.FILM_ID = l.FILM_ID";
        if (isDirectorCheck) {
            qs = qs.concat(" left join FILM_DIRECTOR as fd on f.FILM_ID = fd.FILM_ID left join DIRECTOR as d on d.DIRECTOR_ID = fd.DIRECTOR_ID where LOWER(d.NAME) like LOWER(?)");
            params.add(title);
        }
        if (isTitleCheck) {
            params.add(title);
            if (!isDirectorCheck) {
                qs = qs.concat(" where LOWER(f.NAME) like LOWER(?)");
            } else {
                qs = qs.concat(" or LOWER(f.NAME) like LOWER(?)");
            }
        }
        qs = qs.concat(" group by f.FILM_ID order by count(l.USER_ID) desc;");

        try {
            log.info("qs = " + qs);
            return jdbcTemplate.query(qs, this::makeFilmList, params.toArray());
        } catch (DataAccessException e) {
            throw new FilmNotFoundException("Фильм не найден");
        }
    }

    @Override
    public Collection<Film> getFilmsWithDirectorIdSortedByLikes(Long directorId) {
        String sqlQueryWhenLikesArePresent = "SELECT\n" +
                "f.film_id,\n" +
                "f.name,\n" +
                "f.release_date,\n" +
                "f.duration,\n" +
                "f.description,\n" +
                "f.mpa rating_id,\n" +
                "m.name rating_name,\n" +
                "COUNT(l.film_id)\n" +
                "FROM film f\n" +
                "JOIN likes l ON l.film_id = f.film_id\n" +
                "JOIN mpa m ON f.mpa = m.rating_id\n" +
                "GROUP BY  l.film_id\n" +
                "HAVING f.film_id IN (SELECT film_id FROM film_director WHERE director_id = ?)\n" +
                "ORDER BY  COUNT(l.film_id) DESC";
        String sqlQueryWhenLikesAbsent = "SELECT\n" +
                "f.film_id,\n" +
                "f.name,\n" +
                "f.release_date,\n" +
                "f.duration,\n" +
                "f.description,\n" +
                "f.mpa rating_id,\n" +
                "m.name rating_name\n" +
                "FROM film f\n" +
                "JOIN mpa m ON f.mpa = m.rating_id\n" +
                "WHERE f.film_id IN (SELECT film_id FROM film_director WHERE director_id = ?)";

        Collection<Film> list = jdbcTemplate.query(sqlQueryWhenLikesArePresent, this::makeFilmList, directorId);
        if (CollectionUtils.isEmpty(list)) {
            list = jdbcTemplate.query(sqlQueryWhenLikesAbsent, this::makeFilmList, directorId);
        }

        return list;
    }

    @Override
    public Collection<Film> getFilmsWithDirectorIdSortedByYear(Long directorId) {
        String sqlQuery = "SELECT\n" +
                "f.film_id,\n" +
                "f.name,\n" +
                "f.release_date,\n" +
                "f.duration,\n" +
                "f.description,\n" +
                "f.mpa rating_id,\n" +
                "m.name rating_name,\n" +
                "EXTRACT(\n" +
                "YEAR \n" +
                "    FROM\n" +
                "    CAST(release_date AS date)\n" +
                ") as the_year\n" +
                "FROM film f\n" +
                "JOIN mpa m ON f.mpa = m.rating_id\n" +
                "WHERE  f.film_id IN (SELECT film_id FROM film_director WHERE director_id = ?)\n" +
                "ORDER BY the_year";

        return jdbcTemplate.query(sqlQuery, this::makeFilmList, directorId);
    }

    // helpers methods for a CREATE method------------------------------------------------------------------------------

    private List<Film> makeFilmList(ResultSet rs) throws SQLException, DataAccessException {
        List<Film> filmList = new ArrayList<>();

        while (rs.next()) {
            filmList.add(makeFilm(rs));
        }
        return filmList;
    }

    private Film makeFilm(ResultSet rs, Integer... rowNum) throws SQLException {
        long id = rs.getLong("FILM_ID");
        String name = rs.getString("NAME");
        LocalDate releaseDate = rs.getDate("RELEASE_DATE").toLocalDate();
        long duration = rs.getLong("DURATION");
        String description = rs.getString("DESCRIPTION");
        Integer mpaId = rs.getInt("RATING_ID");
        String ratingName = rs.getString("RATING_NAME");
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
                "l.USER_ID\n" +
                "FROM film AS f\n" +
                "INNER JOIN likes AS l ON l.film_id = f.film_id\n" +
                "WHERE f.FILM_ID = ?";

        return jdbcTemplate.query(sqlQuery, rs -> {
            Set<Long> setOfId = new HashSet<>();

            while (rs.next()) {
                Long userId = rs.getLong("USER_ID");
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
        String sqlQuery = "INSERT INTO likes (FILM_ID, USER_ID) " +
                "VALUES (?, ?)";

        film.getLikes().forEach(userId -> jdbcTemplate.update(sqlQuery, film.getId(), userId));
    }

    /////////////////// CREATE GENRE -----------------------------------------------------------------------------------

    private List<Genre> makeGenreList(long id) {
        String sqlQuery = "SELECT\n" +
                "g.NAME AS GENRE_NAME,\n" +
                "g.GENRE_ID AS ID\n" +
                "FROM film AS f\n" +
                "INNER JOIN film_genre AS fm ON f.film_id = fm.film_id\n" +
                "INNER JOIN genre AS g ON g.genre_id = fm.genre_id\n" +
                "WHERE f.FILM_ID = ?";

        return jdbcTemplate.query(sqlQuery, rs -> {
            List<Genre> listOfGenre = new ArrayList<>();

            while (rs.next()) {
                Integer genreId = rs.getInt("ID");
                String genreName = rs.getString("GENRE_NAME");
                Genre genre = new Genre(genreId, genreName);
                listOfGenre.add(genre);
            }
            return listOfGenre;
        }, id);
    }

    private String getName(String sqlQuery, Long id) {
        return jdbcTemplate.queryForObject(sqlQuery,
                (rs, rn) -> rs.getString("NAME"), id);
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
                "m.NAME\n" +
                "FROM MPA m\n" +
                "WHERE\n" +
                "m.RATING_ID = ?";

        return getName(sqlQuery, (long) film.getMpa().getId());
    }

    /////////////////// CREATE DIRECTOR --------------------------------------------------------------------------------

    private Collection<Director> setDirectorsNames(Film film) {
        return film.getDirectors()
                .stream()
                .map(director -> {
                    String name = getDirectorName(director);
                    director.setName(name);
                    return director;
                }).collect(Collectors.toList());
    }

    private String getDirectorName(Director director) {
        String sqlQuery = "SELECT \n" +
                "d.name\n" +
                "FROM director d\n" +
                "WHERE d.director_id = ?";

        return getName(sqlQuery, director.getId());
    }

    private void setDirector(Film film) {
        if (CollectionUtils.isEmpty(film.getDirectors())) {
            return;
        }
        Collection<Director> directors = setDirectorsNames(film);
        String sqlQuery = "INSERT INTO film_director\n" +
                "(film_id, director_id)\n" +
                "VALUES (?, ?)";

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
                "USER_ID\n" +
                "FROM\n" +
                "LIKES\n" +
                "WHERE FILM_ID = ?";
        return jdbcTemplate.query(sqlQuery, rs -> {
            Set<Long> setOfLikes = new HashSet<>();
            while (rs.next()) {
                Long userId = rs.getLong("USER_ID");
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
                "g.NAME AS GENRE_NAME,\n" +
                "g.GENRE_ID AS ID\n" +
                "FROM FILM f \n" +
                "INNER JOIN FILM_GENRE fg ON fg.FILM_ID = f.FILM_ID\n" +
                "INNER JOIN GENRE g ON fg.GENRE_ID  = g.GENRE_ID\n" +
                "WHERE f.FILM_ID  = ?";

        return jdbcTemplate.query(sqlQuery, rs -> {
            List<Genre> listOfGenre = new ArrayList<>();
            while (rs.next()) {
                Integer genreId = rs.getInt("ID");
                String genreName = rs.getString("GENRE_NAME");
                Genre genre = new Genre(genreId, genreName);
                listOfGenre.add(genre);
            }
            return listOfGenre;
        }, film.getId());
    }

    private void setGenres(Film film, Collection<Genre> genres) {
        String sqlQuery = "INSERT INTO film_genre (FILM_ID, GENRE_ID) " +
                "VALUES (?, ?)";

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
                "WHERE " +
                "FILM_ID = ?\n" +
                "AND\n" +
                "GENRE_ID = ? ";

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
                "d.director_id,\n" +
                "d.name\n" +
                "FROM director d\n" +
                "INNER JOIN film_director fd ON d.director_id = fd.director_id\n" +
                "INNER JOIN film f ON f.film_id = fd.film_id\n" +
                "WHERE f.film_id = ?";

        return jdbcTemplate.query(sqlQuery, rs -> {
            Collection<Director> setOfDirectors = new HashSet<>();
            while (rs.next()) {
                Long id = rs.getLong("director_id");
                String name = rs.getString("name");
                Director director = Director.builder()
                        .id(id)
                        .name(name)
                        .build();
                setOfDirectors.add(director);
            }
            return setOfDirectors;
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
                "WHERE " +
                "film_id = ?";

        jdbcTemplate.update(sqlQuery, film.getId());
    }

    private void addDirector(Long idOfFilm, Long idOfUser) {
        String sqlQuery = "INSERT INTO film_director (film_id, director_id) " +
                "VALUES (?, ?)";

        jdbcTemplate.update(sqlQuery, idOfFilm, idOfUser);
    }

    private void deleteDirector(Long idOfFilm, Long idOfUser) {
        String sqlQuery = "DELETE FROM film_director " +
                "WHERE " +
                "film_id = ? " +
                "AND " +
                "director_id = ?";

        jdbcTemplate.update(sqlQuery, idOfFilm, idOfUser);
    }
}
