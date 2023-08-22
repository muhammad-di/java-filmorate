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
import java.util.ArrayList;
import java.util.List;


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
    public List<Genre> findAll() {
        String sqlQuery = "SELECT\n" +
                "g.GENRE_ID,\n" +
                "g.NAME\n" +
                "FROM GENRE g\n" +
                "ORDER BY GENRE_ID ASC";

        return jdbcTemplate.query(sqlQuery, this::makeGenreList);
    }


    @Override
    public boolean containsGenre(Integer idOfGenre) {
        String sqlQuery = "SELECT EXISTS(SELECT 1 FROM genre WHERE genre_id = ?) AS GENRE";

        return jdbcTemplate.queryForObject(sqlQuery, (rs, rn) -> rs.getBoolean("GENRE"), idOfGenre);
    }

    @Override
    public Genre getGenreById(Integer id) {
        String sqlQuery = "SELECT\n" +
                "g.GENRE_ID,\n" +
                "g.NAME\n" +
                "FROM GENRE g\n" +
                "WHERE g.GENRE_ID = ?";

        return jdbcTemplate.queryForObject(sqlQuery, this::makeGenre, id);
    }

    private List<Genre> makeGenreList(ResultSet rs) throws SQLException {
        List<Genre> genreList = new ArrayList<>();

        while (rs.next()) {
            Genre genre = makeGenre(rs);
            genreList.add(genre);
        }

        return genreList;
    }

    private Genre makeGenre(ResultSet rs, Integer... rn) throws SQLException {
        Integer genreId = rs.getInt("GENRE_ID");
        String genreName = rs.getString("NAME");
        return new Genre(genreId, genreName);
    }

}
