package ru.yandex.practicum.filmorate.storage.director.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.director.dao.DirectorStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

@Slf4j
@Repository
@RequiredArgsConstructor
public class DirectorDbStorage implements DirectorStorage {
    @Autowired
    private final JdbcTemplate jdbcTemplate;

    public Collection<Director> findAll() {
        String sqlQuery = "SELECT\n" +
                "          d.director_id,\n" +
                "          d.name\n" +
                "          FROM director d\n" +
                "          ORDER BY director_id";

        return jdbcTemplate.query(sqlQuery, this::mapRowToDirector);
    }

    public Director findById(Long id) {
        String sqlQuery = "SELECT\n" +
                "          d.director_id,\n" +
                "          d.name\n" +
                "          FROM director d\n" +
                "          WHERE director_id = ?";

        try {
            return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToDirector, id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public Boolean contains(Long id) {
        String sqlQuery = "SELECT EXISTS(SELECT 1 FROM director WHERE director_id = ?) AS is_director";

        return jdbcTemplate.queryForObject(sqlQuery,
                (rs, rn) -> rs.getBoolean("is_director"),
                id);
    }

    public Boolean contains(Director director) {
        String sqlQuery = "SELECT EXISTS(SELECT 1 FROM director WHERE director_id = ? OR name = ?) AS is_director";

        return jdbcTemplate.queryForObject(sqlQuery,
                (rs, rn) -> rs.getBoolean("is_director"),
                director.getId(), director.getName());
    }

    public Director create(Director director) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("director")
                .usingGeneratedKeyColumns("director_id");
        Long id = simpleJdbcInsert.executeAndReturnKey(director.toMap()).longValue();

        return findById(id);
    }

    public Director update(Director director) {
        String sqlQuery = "UPDATE director\n" +
                "          SET name = ?\n" +
                "          WHERE director_id = ?";

        jdbcTemplate.update(sqlQuery, director.getName(), director.getId());
        return findById(director.getId());
    }

    @Override
    public void deleteById(Long id) {
        String sqlQuery = "DELETE FROM director " +
                "          WHERE " +
                "          director_id = ?";

        jdbcTemplate.update(sqlQuery, id);
    }

    // helpers methods for a GetAll method------------------------------------------------------------------------------

    private Director mapRowToDirector(ResultSet rs, Integer rn) throws SQLException {
        Long id = rs.getLong("director_id");
        String name = rs.getString("name");

        return Director.builder()
                .id(id)
                .name(name)
                .build();
    }
}


