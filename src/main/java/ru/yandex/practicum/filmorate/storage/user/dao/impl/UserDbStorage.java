package ru.yandex.practicum.filmorate.storage.user.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.dao.UserStorage;

import java.sql.*;
import java.time.Instant;
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
                "          u.*\n" +
                "          FROM users u";

        return jdbcTemplate.query(sqlQuery, this::mapRowToUser);
    }

    @Override
    public User create(User user) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("user_id");
        long userId = simpleJdbcInsert.executeAndReturnKey(user.toMap()).longValue();

        user.setId(userId);
        if (!CollectionUtils.isEmpty(user.getFriends())) {
            setFriends(user);
        }

        return findById(userId);
    }

    @Override
    public User update(User user) {
        String sqlQuery = "UPDATE users " +
                "          SET\n" +
                "          login = ?,\n" +
                "          birthday = ?,\n" +
                "          email = ?,\n" +
                "          name = ?\n" +
                "          WHERE user_id = ?";

        jdbcTemplate.update(sqlQuery,
                user.getLogin(),
                user.getBirthday(),
                user.getEmail(),
                user.getName(),
                user.getId()
        );
        if (!CollectionUtils.isEmpty(user.getFriends())) {
            updateFriends(user);
        }

        return findById(user.getId());
    }

    @Override
    public Collection<User> findAllFriends(Long id) {
        String sqlQuery = "SELECT \n" +
                "          f.friend_id,\n" +
                "          u.user_id,\n" +
                "          u.login,\n" +
                "          u.birthday,\n" +
                "          u.email,\n" +
                "          u.name\n" +
                "          FROM friends f\n" +
                "          INNER JOIN users u ON f.friend_id = u.user_id\n" +
                "          WHERE " +
                "          f.user_id = ?";

        return jdbcTemplate.query(sqlQuery, this::mapRowToUser, id);
    }

    @Override
    public void addFriend(Long id, Long idOfFriend) {
        String sqlQuery = "INSERT INTO friends (user_id, friend_id) " +
                "          VALUES (?, ?)";

        jdbcTemplate.update(sqlQuery, id, idOfFriend);
        createEvent(id, idOfFriend, Operation.ADD);
    }

    @Override
    public void deleteFriend(Long id, Long idOfFriend) {
        String sqlQuery = "DELETE FROM friends\n" +
                "          WHERE\n" +
                "          user_id = ?\n" +
                "          AND\n" +
                "          friend_id = ?";

        jdbcTemplate.update(sqlQuery, id, idOfFriend);
        createEvent(id, idOfFriend, Operation.REMOVE);

    }

    @Override
    public Collection<User> findCommonFriends(Long id, Long idOfFriend) {
        String sqlQuery = " SELECT\n" +
                "           u.*\n" +
                "           FROM users u\n" +
                "           WHERE u.user_id IN (" +
                "                               SELECT\n" +
                "                               f.friend_id AS friend_id \n" +
                "                               FROM friends f\n" +
                "                               WHERE \n" +
                "                               friend_id IN (" +
                "                                               SELECT\n" +
                "                                               f.friend_id AS friend_id \n" +
                "                                               FROM friends f\n" +
                "                                               WHERE f.user_id = ?\n" +
                "                                               )\n" +
                "                               AND\n" +
                "                               f.user_id = ?\n" +
                "                               )";
        return jdbcTemplate.query(sqlQuery, this::mapRowToUser, id, idOfFriend);
    }

    @Override
    public Boolean contains(Long idOfUser) {
        String sqlQuery = "SELECT EXISTS(SELECT 1 FROM users WHERE user_id = ?) AS is_user";

        return jdbcTemplate.queryForObject(sqlQuery, (rs, rn) -> rs.getBoolean("is_user"), idOfUser);
    }

    @Override
    public User findById(Long id) {
        String sqlQuery = "SELECT\n" +
                "          u.user_id,\n" +
                "          u.login,\n" +
                "          u.birthday,\n" +
                "          u.email,\n" +
                "          u.name\n" +
                "          FROM users u\n" +
                "          WHERE u.user_id = ?";

        return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToUser, id);
    }

    @Override
    public Map<Long, List<Long>> findAllUsersWithTheirLikedFilms() {

        return jdbcTemplate.query("SELECT * FROM likes", (ResultSet rs) -> {
            Map<Long, List<Long>> allUsersWithTheirLikedFilms = new HashMap<>();
            while (rs.next()) {
                long idFilm = rs.getLong("film_id");
                long idUser = rs.getLong("user_id");

                if (allUsersWithTheirLikedFilms.containsKey(idUser)) {
                    allUsersWithTheirLikedFilms.get(idUser).add(idFilm);
                } else {
                    allUsersWithTheirLikedFilms.put(idUser, new ArrayList<>() {{
                        add(idFilm);
                    }
                    });
                }
            }
            return allUsersWithTheirLikedFilms;
        });
    }

    public void deleteById(Long id) {
        String sqlQuery = "DELETE FROM users " +
                "WHERE " +
                "user_id = ?";

        jdbcTemplate.update(sqlQuery, id);
        log.info("Пользователь с идентификатором {} удален.", id);
    }

    public Collection<Event> findEventByUserId(Long id) {
        String sqlQuery = "SELECT \n" +
                "          f.event_id,\n" +
                "          f.user_id,\n" +
                "          f.entity_id,\n" +
                "          f.event_type,\n" +
                "          f.operation,\n" +
                "          f.event_timestamp \n" +
                "          FROM feed f\n" +
                "          WHERE f.user_id = ?";

        return jdbcTemplate.query(sqlQuery, this::mapRowToEvent, id);
    }

    // helpers methods for a CREATE method------------------------------------------------------------------------------

    private User mapRowToUser(ResultSet rs, Integer rowNum) throws SQLException {
        long id = rs.getLong("user_id");
        String login = rs.getString("login");
        LocalDate birthday = rs.getDate("birthday").toLocalDate();
        String email = rs.getString("email");
        String name = rs.getString("name");
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
                "          f.friend_id,\n" +
                "          f.status\n" +
                "          FROM friends f\n" +
                "          WHERE f.user_id = ?";

        return jdbcTemplate.query(sqlQuery, rs -> {
            Map<Long, Boolean> mapOfFriends = new HashMap<>();

            while (rs.next()) {
                Long friendId = rs.getLong("friend_id");
                Boolean status = rs.getBoolean("status");
                mapOfFriends.put(friendId, status);
            }
            return mapOfFriends;
        }, id);
    }

    private void setFriends(User user) {
        String sqlQuery = "INSERT INTO friends (user_id, friend_id, status) " +
                "          VALUES (?, ?, ?)";

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
                "          f.friend_id,\n" +
                "          f.status,\n" +
                "          FROM\n" +
                "          friends f\n" +
                "          WHERE f.user_id = ?";

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
                "          friends \n" +
                "          SET status = ? \n" +
                "          WHERE " +
                "          user_id = ? " +
                "          AND " +
                "          friend_id = ?";

        friends.forEach((key, value) -> jdbcTemplate.update(sqlQuery, value, user.getId(), key));
    }

    // helpers methods for a FIND_EVENT_BY_USER_ID method--------------------------------------------------------------------

    private Event mapRowToEvent(ResultSet rs, Integer rowNum) throws SQLException {
        Long eventId = rs.getLong("event_id");
        Long userId = rs.getLong("user_id");
        Long entityId = rs.getLong("entity_id");
        Long timestamp = rs.getLong("event_timestamp");
        EventType eventType = EventType.valueOf(rs.getString("event_type"));
        Operation operation = Operation.valueOf(rs.getString("operation"));

        return Event.builder()
                .eventId(eventId)
                .userId(userId)
                .entityId(entityId)
                .eventType(eventType)
                .operation(operation)
                .timestamp(timestamp)
                .build();
    }

    private void createEvent(Long id, Long entityId, Operation operation) {
        Event feed = Event.builder()
                .userId(id)
                .entityId(entityId)
                .eventType(EventType.FRIEND)
                .operation(operation)
                .timestamp(Instant.now().toEpochMilli())
                .build();
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("feed")
                .usingGeneratedKeyColumns("event_id");
        simpleJdbcInsert.execute(feed.toMap());
    }
}
