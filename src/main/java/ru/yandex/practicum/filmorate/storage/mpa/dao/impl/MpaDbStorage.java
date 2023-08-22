package ru.yandex.practicum.filmorate.storage.mpa.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
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
    public List<Mpa> findAll() {
        String sqlQuery = "SELECT\n" +
                "m.RATING_ID AS RATING_ID,\n" +
                "m.NAME AS RATING_NAME\n" +
                "FROM MPA m\n" +
                "ORDER BY RATING_ID ASC";


        return jdbcTemplate.query(sqlQuery, this::makeMpaList);
    }


    @Override
    public boolean containsMpa(Integer idOfMpa) {
        String sqlQuery = "SELECT EXISTS(SELECT 1 FROM mpa WHERE rating_id = ?) AS RATING";

        return jdbcTemplate.queryForObject(sqlQuery, (rs, rn) -> rs.getBoolean("RATING"), idOfMpa);
    }

    @Override
    public Mpa getMpaById(Integer id) {
        String sqlQuery = "SELECT\n" +
                "m.RATING_ID AS RATING_ID,\n" +
                "m.NAME AS RATING_NAME\n" +
                "FROM MPA m\n" +
                "WHERE m.RATING_ID = ?";

        return jdbcTemplate.queryForObject(sqlQuery, this::makeMpa, id);
    }

    private List<Mpa> makeMpaList(ResultSet rs) throws SQLException {
        List<Mpa> mpaList = new ArrayList<>();

        while (rs.next()) {
            Mpa mpa = makeMpa(rs);
            mpaList.add(mpa);
        }

        return mpaList;
    }

    private Mpa makeMpa(ResultSet rs, Integer... rn) throws SQLException {
        Integer ratingId = rs.getInt("RATING_ID");
        String ratingName = rs.getString("RATING_NAME");
        return new Mpa(ratingId, ratingName);
    }
}
