package ru.yandex.practicum.filmorate.storage.user.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.FeedEntity;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.dao.UserStorage;

import java.sql.*;
import java.sql.Date;
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
                "u.*\n" +
                "FROM USERS u";

        return jdbcTemplate.query(sqlQuery, this::makeUserList);
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

        return getUserById(userId);
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
        if (!CollectionUtils.isEmpty(user.getFriends())) {
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
        String sqlQuery = "INSERT INTO friends (user_id, friend_id) " +
                "VALUES (?, ?)";

        jdbcTemplate.update(sqlQuery, id, idOfFriend);
        setFeedEvent(id, idOfFriend, Operation.ADD);
    }

    @Override
    public void deleteFriend(Long id, Long idOfFriend) {
        String sqlQuery = "DELETE FROM friends\n" +
                "WHERE\n" +
                "USER_ID = ?\n" +
                "AND\n" +
                "FRIEND_ID = ?";

        jdbcTemplate.update(sqlQuery, id, idOfFriend);
        setFeedEvent(id, idOfFriend, Operation.REMOVE);

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
    public Boolean containsUser(Long idOfUser) {
        String sqlQuery = "SELECT EXISTS(SELECT 1 FROM users WHERE user_id = ?) AS is_user";

        return jdbcTemplate.queryForObject(sqlQuery, (rs, rn) -> rs.getBoolean("is_user"), idOfUser);
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

    public Collection<FeedEntity> getFeedOfUser(Long id) {
        String sqlQuery = "SELECT \n" +
                "f.event_id,\n" +
                "f.user_id,\n" +
                "f.entity_id,\n" +
                "f.event_type,\n" +
                "f.operation,\n" +
                "f.EVENT_TIMESTAMP \n" +
                "FROM feed f\n" +
                "WHERE f.user_id = ?";

        return jdbcTemplate.query(sqlQuery, this::mapRowToFeed, id);
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

    // helpers methods for a GET_FEED_OF_USER method--------------------------------------------------------------------

    private FeedEntity mapRowToFeed(ResultSet rs, Integer rowNum) throws SQLException {
        Long eventId = rs.getLong("event_id");
        Long userId = rs.getLong("user_id");
        Long entityId = rs.getLong("entity_id");
        Long timestamp = rs.getLong("event_timestamp");
        EventType eventType = EventType.valueOf(rs.getString("event_type"));
        Operation operation = Operation.valueOf(rs.getString("operation"));

        return FeedEntity.builder()
                .eventId(eventId)
                .userId(userId)
                .entityId(entityId)
                .eventType(eventType)
                .operation(operation)
                .timestamp(timestamp)
                .build();
    }

    private void setFeedEvent(Long id, Long entityId, Operation operation) {
        FeedEntity feed = FeedEntity.builder()
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
