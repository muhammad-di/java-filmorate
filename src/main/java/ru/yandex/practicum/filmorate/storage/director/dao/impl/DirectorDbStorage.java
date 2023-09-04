package ru.yandex.practicum.filmorate.storage.director.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

    public Collection<Director> getAllDirectors() {
        String sqlQuery = "SELECT\n" +
                "          d.director_id,\n" +
                "          d.name\n" +
                "          FROM director d\n" +
                "          ORDER BY director_id";

        return jdbcTemplate.query(sqlQuery, this::mapRowToDirector);
    }

    public Director getDirectorById(Long id) {
        String sqlQuery = "SELECT\n" +
                "          d.director_id,\n" +
                "          d.name\n" +
                "          FROM director d\n" +
                "          WHERE director_id = ?";

        return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToDirector, id);
    }

    public Boolean containsDirector(Long id) {
        String sqlQuery = "SELECT EXISTS(SELECT 1 FROM director WHERE director_id = ?) AS is_director";

        return jdbcTemplate.queryForObject(sqlQuery,
                (rs, rn) -> rs.getBoolean("is_director"),
                id);
    }

    public Boolean containsDirector(String name) {
        String sqlQuery = "SELECT EXISTS(SELECT 1 FROM director WHERE name = ?) AS is_director";

        return jdbcTemplate.queryForObject(sqlQuery,
                (rs, rn) -> rs.getBoolean("is_director"),
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
                "          SET name = ?\n" +
                "          WHERE director_id = ?";

        jdbcTemplate.update(sqlQuery, director.getName(), director.getId());
        return getDirectorById(director.getId());
    }

    @Override
    public void deleteDirectorById(Long id) {
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


