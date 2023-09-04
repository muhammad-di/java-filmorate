package ru.yandex.practicum.filmorate.storage.genre.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.dao.GenreStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;


@Slf4j
@Repository
@Primary
public class GenreDbStorage implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<Genre> findAll() {
        String sqlQuery = "SELECT\n" +
                "          g.genre_id,\n" +
                "          g.name\n" +
                "          FROM genre g\n" +
                "          ORDER BY genre_id ASC";

        return jdbcTemplate.query(sqlQuery, this::mapRowToGenre);
    }

    @Override
    public Boolean containsGenre(Integer idOfGenre) {
        String sqlQuery = "SELECT EXISTS(SELECT 1 FROM genre WHERE genre_id = ?) AS is_genre";

        return jdbcTemplate.queryForObject(sqlQuery, (rs, rn) -> rs.getBoolean("is_genre"), idOfGenre);
    }

    @Override
    public Genre getGenreById(Integer id) {
        String sqlQuery = "SELECT\n" +
                "          g.genre_id,\n" +
                "          g.name\n" +
                "          FROM genre g\n" +
                "          WHERE g.genre_id = ?";

        return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToGenre, id);
    }

    private Genre mapRowToGenre(ResultSet rs, Integer rn) throws SQLException {
        Integer genreId = rs.getInt("GENRE_ID");
        String genreName = rs.getString("NAME");
        return new Genre(genreId, genreName);
    }

}
