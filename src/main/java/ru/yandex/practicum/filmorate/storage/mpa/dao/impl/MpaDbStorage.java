package ru.yandex.practicum.filmorate.storage.mpa.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpa.dao.MpaStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;


@Slf4j
@Repository
@Primary
public class MpaDbStorage implements MpaStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<Mpa> findAll() {
        String sqlQuery = "SELECT\n" +
                "          m.rating_id AS rating_id,\n" +
                "          m.name AS rating_name\n" +
                "          FROM mpa m\n" +
                "          ORDER BY rating_id ASC";

        return jdbcTemplate.query(sqlQuery, this::mapRorToMpa);
    }

    @Override
    public Boolean containsMpa(Integer idOfMpa) {
        String sqlQuery = "SELECT EXISTS(SELECT 1 FROM mpa WHERE rating_id = ?) AS rating";

        return jdbcTemplate.queryForObject(sqlQuery, (rs, rn) -> rs.getBoolean("rating"), idOfMpa);
    }

    @Override
    public Mpa getMpaById(Integer id) {
        String sqlQuery = "SELECT\n" +
                "          m.rating_id AS rating_id,\n" +
                "          m.name AS rating_name\n" +
                "          FROM mpa m\n" +
                "          WHERE m.rating_id = ?";

        return jdbcTemplate.queryForObject(sqlQuery, this::mapRorToMpa, id);
    }

    private Mpa mapRorToMpa(ResultSet rs, Integer rn) throws SQLException {
        Integer ratingId = rs.getInt("rating_id");
        String ratingName = rs.getString("rating_name");
        return new Mpa(ratingId, ratingName);
    }
}
