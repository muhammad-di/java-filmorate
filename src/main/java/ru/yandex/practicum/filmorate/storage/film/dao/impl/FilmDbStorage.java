package ru.yandex.practicum.filmorate.storage.film.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.dao.FilmStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
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
        String sqlQuery = "INSERT INTO film (NAME, RELEASE_DATE, DURATION, DESCRIPTION, MPA) " +
                "VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"film_id"});
            stmt.setString(1, film.getName());
            stmt.setDate(2, Date.valueOf(film.getReleaseDate()));
            stmt.setLong(3, film.getDuration());
            stmt.setString(4, film.getDescription());
            stmt.setInt(5, film.getMpa().getId());
            return stmt;
        }, keyHolder);
        film.setId(keyHolder.getKey().longValue());
        if (film.getLikes() != null) {
            setLikes(film);
        }
        if (film.getGenres() == null) {
            film.setGenres(List.of());
        } else {
            setGenre(film);
        }
        if (film.getMpa().getId() != null) {
            String ratingName = getRatingName(film);
            film.getMpa().setName(ratingName);
        }

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
    public Collection<Film> getMostPopularFilms(Integer count) {
        String sqlQuery = "SELECT t.film_id, " +
                "t.name, " +
                "t.description, " +
                "t.release_date, " +
                "t.duration, " +
                "t.RATING_ID," +
                "t.RATING_NAME " +
                "FROM " +
                "(SELECT f.film_id, " +
                "f.name, " +
                "f.description, " +
                "f.release_date, " +
                "f.duration, " +
                "f.mpa AS RATING_ID, " +
                "m.NAME AS RATING_NAME " +
                "FROM film AS f " +
                "JOIN MPA m ON f.MPA = m.RATING_ID) AS t " +
                "LEFT JOIN likes AS l ON t.film_id = l.film_id " +
                "GROUP BY t.film_id " +
                "ORDER BY COUNT(l.user_id) DESC " +
                "LIMIT ?;";

        return jdbcTemplate.query(sqlQuery, this::makeFilmList, count);
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
                .build();
    }

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
        String sqlQuery = "INSERT INTO likes (FILM_ID, USER_ID) " +
                "VALUES (?, ?)";

        film.getLikes().forEach(userId -> jdbcTemplate.update(sqlQuery, film.getId(), userId));
    }


    private void setGenre(Film film) {
        String sqlQuery = "INSERT INTO film_genre (FILM_ID, GENRE_ID) " +
                "VALUES (?, ?)";

        film.getGenres()
                .stream()
                .map(Genre::getId)
                .forEach(genreId -> jdbcTemplate.update(sqlQuery, film.getId(), genreId));
    }

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

    private String getRatingName(Film film) {
        String sqlQuery = "SELECT\n" +
                "m.NAME\n" +
                "FROM MPA m\n" +
                "WHERE\n" +
                "m.RATING_ID = ?";

        return jdbcTemplate.queryForObject(sqlQuery,
                (rs, rn) -> rs.getString("NAME"),
                film.getMpa().getId());
    }


    private void updateRating(Film film) {
        if (film.getMpa().getId() != null) {
            String ratingName = getRatingName(film);
            film.getMpa().setName(ratingName);
        }
    }

    // helpers methods for a UPDATE method------------------------------------------------------------------------------

    private void updateLikes(Film film) {
        if (film.getLikes() == null) {
            film.setLikes(Set.of());
            return;
        }
        Set<Long> likesToInsert = new HashSet<>(film.getLikes());
        Set<Long> likesToDelete = new HashSet<>(getLikes(film));
        Set<Long> commonLikes = likesToDelete.stream()
                .filter(likesToInsert::contains)
                .collect(Collectors.toSet());

        likesToInsert.removeAll(commonLikes);
        likesToDelete.removeAll(commonLikes);
        insertLikes(film, likesToInsert);
        deleteLikes(film, likesToDelete);
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
        insertGenres(film, genresToInsert);
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

    private void insertGenres(Film film, Collection<Genre> genres) {
        String sqlQuery = "INSERT INTO film_genre (FILM_ID, GENRE_ID) " +
                "VALUES (?, ?)";

        genres.forEach(genre -> jdbcTemplate.update(sqlQuery, film.getId(), genre.getId()));
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
}
