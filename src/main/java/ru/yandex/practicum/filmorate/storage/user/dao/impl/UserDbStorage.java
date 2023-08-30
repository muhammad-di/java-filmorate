package ru.yandex.practicum.filmorate.storage.user.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.dao.UserStorage;

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
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<User> findAll() {
        String sqlQuery = "SELECT \n" +
                "u.*\n" +
                "FROM USERS u";

        return jdbcTemplate.query(sqlQuery, this::makeUserList);
    }

    @Override
    public User create(User user) {
        String sqlQuery = "INSERT INTO users (LOGIN, BIRTHDAY, EMAIL, NAME) " +
                "VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"user_id"});
            stmt.setString(1, user.getLogin());
            stmt.setDate(2, Date.valueOf(user.getBirthday()));
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getName());
            return stmt;
        }, keyHolder);
        user.setId(keyHolder.getKey().longValue());
        if (user.getFriends() != null) {
            setFriends(user);
        }

        return getUserById(keyHolder.getKey().longValue());
    }

    @Override
    public User update(User user) {
        String sqlQuery = "UPDATE users " +
                "SET\n" +
                "LOGIN = ?,\n" +
                "BIRTHDAY = ?,\n" +
                "EMAIL = ?,\n" +
                "NAME = ?\n" +
                "WHERE USER_ID = ?";

        jdbcTemplate.update(sqlQuery,
                user.getLogin(),
                user.getBirthday(),
                user.getEmail(),
                user.getName(),
                user.getId()
        );
        if (user.getFriends() != null) {
            updateFriends(user);
        }

        return getUserById(user.getId());
    }

    @Override
    public Collection<User> getAllFriends(Long id) {
        String sqlQuery = "SELECT \n" +
                "f.FRIEND_ID,\n" +
                "u.USER_ID,\n" +
                "u.LOGIN,\n" +
                "u.BIRTHDAY,\n" +
                "u.EMAIL,\n" +
                "u.NAME\n" +
                "FROM FRIENDS f\n" +
                "INNER JOIN USERS u ON f.friend_id = u.user_id\n" +
                "WHERE " +
                "f.USER_ID = ?";

        return jdbcTemplate.query(sqlQuery, this::makeUserList, id);
    }

    @Override
    public void addFriend(Long id, Long idOfFriend) {
        String sqlQuery = "INSERT INTO friends (USER_ID, FRIEND_ID) " +
                "VALUES (?, ?)";

        jdbcTemplate.update(sqlQuery, id, idOfFriend);
    }

    @Override
    public void deleteFriend(Long id, Long idOfFriend) {
        String sqlQuery = "DELETE FROM friends\n" +
                "WHERE\n" +
                "USER_ID = ?\n" +
                "AND\n" +
                "FRIEND_ID = ?";

        jdbcTemplate.update(sqlQuery, id, idOfFriend);
    }

    @Override
    public Collection<User> getCommonFriends(Long id, Long idOfFriend) {
        Set<User> friends = new HashSet<>(getAllFriends(id));
        Set<User> friendsOfFriend = new HashSet<>(getAllFriends(idOfFriend));

        return friends
                .stream()
                .filter(friendsOfFriend::contains)
                .collect(Collectors.toList());
    }

    @Override
    public boolean containsUser(Long idOfUser) {
        String sqlQuery = "SELECT \n" +
                "u.USER_ID\n" +
                "FROM users u\n" +
                "WHERE u.USER_ID = ?";

        SqlRowSet userRows = jdbcTemplate.queryForRowSet(sqlQuery, idOfUser);
        if (userRows.next()) {
            log.info("Найден пользователь c id: {}", idOfUser);
            return true;
        } else {
            log.info("Пользователь с идентификатором {} не найден.", idOfUser);
            return false;
        }
    }

    @Override
    public User getUserById(Long id) {
        String sqlQuery = "SELECT\n" +
                "u.USER_ID,\n" +
                "u.LOGIN,\n" +
                "u.BIRTHDAY,\n" +
                "u.EMAIL,\n" +
                "u.NAME\n" +
                "FROM users u\n" +
                "WHERE u.USER_ID = ?";

        return jdbcTemplate.queryForObject(sqlQuery, this::makeUser, id);
    }

    public void deleteUserById(Long id) {
        String sqlQuery = "DELETE FROM users " +
                "WHERE " +
                "USER_ID = ?";

        jdbcTemplate.update(sqlQuery, id);
        log.info("Пользователь с идентификатором {} удален.", id);
    }

    // helpers methods for a CREATE method------------------------------------------------------------------------------

    private List<User> makeUserList(ResultSet rs) throws SQLException, DataAccessException {
        List<User> userList = new ArrayList<>();

        while (rs.next()) {
            userList.add(makeUser(rs));
        }
        return userList;
    }

    private User makeUser(ResultSet rs, Integer... rowNum) throws SQLException {
        Long id = rs.getLong("USER_ID");
        String login = rs.getString("LOGIN");
        LocalDate birthday = rs.getDate("BIRTHDAY").toLocalDate();
        String email = rs.getString("EMAIL");
        String name = rs.getString("NAME");
        Map<Long, Boolean> friends = makeFriendsMap(id);

        return User.builder()
                .id(id)
                .login(login)
                .birthday(birthday)
                .friends(friends)
                .email(email)
                .name(name)
                .build();
    }

    private Map<Long, Boolean> makeFriendsMap(Long id) {
        String sqlQuery = "SELECT\n" +
                "f.FRIEND_ID,\n" +
                "f.STATUS\n" +
                "FROM friends f\n" +
                "WHERE f.USER_ID = ?";

        return jdbcTemplate.query(sqlQuery, rs -> {
            Map<Long, Boolean> mapOfFriends = new HashMap<>();

            while (rs.next()) {
                Long friendId = rs.getLong("FRIEND_ID");
                Boolean status = rs.getBoolean("STATUS");
                mapOfFriends.put(friendId, status);
            }
            return mapOfFriends;
        }, id);
    }

    private void setFriends(User user) {
        String sqlQuery = "INSERT INTO users (USER_ID, FRIEND_ID, STATUS) " +
                "VALUES (?, ?, ?)";

        user.getFriends()
                .forEach((key, value) -> jdbcTemplate.update(sqlQuery, user.getId(), key, value));
    }

    // helpers methods for a UPDATE method------------------------------------------------------------------------------

    private void updateFriends(User user) {
        Map<Long, Boolean> newFriends = new HashMap<>(user.getFriends());
        Map<Long, Boolean> oldFriends = new HashMap<>(getUsers(user));

        Map<Long, Boolean> friendsToInsert = newFriends.entrySet()
                .stream()
                .filter(entry -> !oldFriends.containsKey(entry.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        Map<Long, Boolean> friendsToDelete = oldFriends.entrySet()
                .stream()
                .filter(entry -> !newFriends.containsKey(entry.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        Map<Long, Boolean> friendsToUpdate = newFriends.entrySet()
                .stream()
                .filter(entry -> oldFriends.containsKey(entry.getKey())
                        && entry.getValue() != oldFriends.get(entry.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        insertFriends(user, friendsToInsert);
        deleteFriends(user, friendsToDelete);
        updateFriendsStatus(user, friendsToUpdate);
    }

    private Map<Long, Boolean> getUsers(User user) {
        String sqlQuery = "SELECT \n" +
                "f.FRIEND_ID,\n" +
                "f.STATUS,\n" +
                "FROM\n" +
                "FRIENDS f\n" +
                "WHERE f.USER_ID = ?";

        return jdbcTemplate.query(sqlQuery, rs -> {
            Map<Long, Boolean> mapOfFriends = new HashMap<>();
            while (rs.next()) {
                Long friendId = rs.getLong("FRIEND_ID");
                Boolean status = rs.getBoolean("STATUS");
                mapOfFriends.put(friendId, status);
            }
            return mapOfFriends;
        }, user.getId());
    }

    private void insertFriends(User user, Map<Long, Boolean> friends) {
        friends.forEach((key, value) -> addFriend(user.getId(), key));
    }

    private void deleteFriends(User user, Map<Long, Boolean> friends) {
        friends.forEach((key, value) -> deleteFriend(user.getId(), key));
    }

    private void updateFriendsStatus(User user, Map<Long, Boolean> friends) {
        String sqlQuery = "UPDATE \n" +
                "friends \n" +
                "SET STATUS = ? \n" +
                "WHERE " +
                "USER_ID = ? " +
                "AND " +
                "FRIEND_ID = ?";

        friends.forEach((key, value) -> jdbcTemplate.update(sqlQuery, value, user.getId(), key));
    }
}
