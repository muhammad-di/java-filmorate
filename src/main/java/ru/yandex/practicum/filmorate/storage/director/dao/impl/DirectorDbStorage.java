package ru.yandex.practicum.filmorate.storage.director.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.director.dao.DirectorStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

@Slf4j
@Repository
@RequiredArgsConstructor
public class DirectorDbStorage implements DirectorStorage {
    @Autowired
    private final JdbcTemplate jdbcTemplate;

    public Collection<Director> getAllDirectors() {
        String sqlQuery = "SELECT\n" +
                "d.director_id,\n" +
                "d.name\n" +
                "FROM director d\n" +
                "ORDER BY director_id";

        return jdbcTemplate.query(sqlQuery, this::makeListOfDirectors);
    }

    public Director getDirectorById(Long id) {
        String sqlQuery = "SELECT\n" +
                "d.director_id,\n" +
                "d.name\n" +
                "FROM director d\n" +
                "WHERE director_id = ?";

        return jdbcTemplate.queryForObject(sqlQuery, this::makeDirector, id);
    }

    public Boolean containsDirector(Long id) {
        String sqlQuery = "SELECT EXISTS(SELECT 1 FROM director WHERE director_id = ?) AS director";

        return jdbcTemplate.queryForObject(sqlQuery,
                (rs, rn) -> rs.getBoolean("director"),
                id);
    }

    public Boolean containsDirector(String name) {
        String sqlQuery = "SELECT EXISTS(SELECT 1 FROM director WHERE name = ?) AS director";

        return jdbcTemplate.queryForObject(sqlQuery,
                (rs, rn) -> rs.getBoolean("director"),
                name);
    }

    public Director createDirector(Director director) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("director")
                .usingGeneratedKeyColumns("director_id");
        long id = simpleJdbcInsert.executeAndReturnKey(director.toMap()).longValue();
        director.setId(id);

        return director;
    }

    public Director updateDirector(Director director) {
        String sqlQuery = "UPDATE director\n" +
                "SET name = ?\n" +
                "WHERE director_id = ?";

        jdbcTemplate.update(sqlQuery, director.getName(), director.getId());
        return getDirectorById(director.getId());
    }

    // helpers methods for a GetAll method------------------------------------------------------------------------------

    private Collection<Director> makeListOfDirectors(ResultSet rs) throws SQLException {
        Collection<Director> directors = new ArrayList<>();

        while (rs.next()) {
            Director director = makeDirector(rs);
            directors.add(director);
        }
        return directors;
    }

    private Director makeDirector(ResultSet rs, Integer... rn) throws SQLException {
        Long id = rs.getLong("director_id");
        String name = rs.getString("name");

        return Director.builder()
                .id(id)
                .name(name)
                .build();
    }
}


