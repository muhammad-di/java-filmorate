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
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
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
        if (film.getLikes() == null) {
            film.setLikes(Set.of());
        } else {
            updateLikes(film);
        }
        if (film.getGenres() == null) {
            film.setGenres(List.of());
        } else {
            List<Genre> genres = new ArrayList<>(updateGenre(film));
            film.setGenres(genres);
        }
        if (film.getMpa().getId() != null) {
            String ratingName = getRatingName(film);
            film.getMpa().setName(ratingName);
        }

        return film;
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
                .directors(List.of())   //Заглушка
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

    private Integer findGenreId(Genre genre) {
        String sqlQuery = "SELECT\n" +
                "GENRE_ID\n" +
                "FROM\n" +
                "GENRE\n" +
                "WHERE NAME = ?";

        return jdbcTemplate.queryForObject(sqlQuery,
                (rs, rowNum) -> rs.getInt("GENRE_ID"),
                genre.toString());
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

    private String getRatingName(Integer id) {
        String sqlQuery = "SELECT\n" +
                "m.NAME\n" +
                "FROM MPA m\n" +
                "WHERE\n" +
                "m.RATING_ID = ?";

        return jdbcTemplate.queryForObject(sqlQuery,
                (rs, rn) -> rs.getString("NAME"),
                id);
    }

    private Integer findRatingId(Film film) {
        String sqlQuery = "SELECT \n" +
                "RATING_ID\n" +
                "FROM\n" +
                "RATING r\n" +
                "WHERE NAME = ?";

        return jdbcTemplate.queryForObject(sqlQuery,
                (rs, rowNum) -> rs.getInt("RATING_ID"),
                film.getMpa().toString());
    }

    // helpers methods for a UPDATE method------------------------------------------------------------------------------

    private void updateLikes(Film film) {
        Set<Long> newLikes = new HashSet<>(film.getLikes());
        Set<Long> oldLikes = new HashSet<>(getLikes(film));
        Set<Long> likesToInsert = new HashSet<>(newLikes);
        Set<Long> likesToDelete = new HashSet<>(oldLikes);

        likesToInsert.removeAll(oldLikes);
        likesToDelete.removeAll(newLikes);
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

    private Collection<Genre> updateGenre(Film film) {
//        Set<Genre> newGenres = new HashSet<>(film.getGenres());
//        Set<Genre> oldGenres = new HashSet<>(getGenres(film));
//        Set<Genre> genresToInsert = new HashSet<>(newGenres);
//        Set<Genre> genresToDelete = new HashSet<>(oldGenres);
//
//        genresToInsert.removeAll(oldGenres);
//        genresToDelete.removeAll(newGenres);
//        Set<Long> genresIdToInsert = genresToInsert.stream().map(this::getGenresId).collect(Collectors.toSet());
//        Set<Long> genresIdToDelete = genresToDelete.stream().map(this::getGenresId).collect(Collectors.toSet());
//        insertGenresId(film, genresIdToInsert);
//        deleteGenresId(film, genresIdToDelete);
        List<Genre> genresList = film.getGenres().stream()
                .distinct()
                .sorted(Comparator.comparing(Genre::getId))
                .collect(Collectors.toList());
        film.setGenres(genresList);
        deleteGenresId(film);
        insertGenresId(film);
        return getGenres(film);
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

    private Long getGenresId(Genre genre) {
        String sqlQuery = "SELECT \n" +
                "GENRE_ID\n" +
                "FROM\n" +
                "GENRE g\n" +
                "WHERE\n" +
                "g.NAME = ?";

        return jdbcTemplate.queryForObject(sqlQuery, (rs, rn) -> rs.getLong("GENRE_ID"), genre.toString());
    }

    private void insertGenresId(Film film) {
        String sqlQuery = "INSERT INTO film_genre (FILM_ID, GENRE_ID) " +
                "VALUES (?, ?)";

        film.getGenres().forEach(genre -> jdbcTemplate.update(sqlQuery, film.getId(), genre.getId()));
    }

    private void deleteGenresId(Film film) {
        String sqlQuery = "DELETE FROM film_genre " +
                "WHERE " +
                "FILM_ID = ? ";

        jdbcTemplate.update(sqlQuery, film.getId());
    }

    private void updateRating(Film film) {
        String sqlQuery = "UPDATE \n" +
                "film_rating \n" +
                "SET RATING_ID = ? \n" +
                "WHERE FILM_ID = ?";

        jdbcTemplate.update(sqlQuery,
                findRatingId(film),
                film.getId());
    }
}
