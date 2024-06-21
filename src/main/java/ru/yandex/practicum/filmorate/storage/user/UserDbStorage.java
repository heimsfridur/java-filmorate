package ru.yandex.practicum.filmorate.storage.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.mapper.UserRowMapper;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Slf4j
@Component
@Qualifier
@AllArgsConstructor
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<User> getAllUsers() {
        String sql = "SELECT users.*, FROM users";
        List<User> users = jdbcTemplate.query(sql, new UserRowMapper());

        return users;
    }

    @Override
    public User addUser(User user) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();

        String sql = "INSERT INTO users (user_email, user_login, user_name, user_birthday) VALUES (?, ?, ?, ?)";

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getLogin());
            ps.setString(3, user.getName());
            ps.setDate(4, Date.valueOf(user.getBirthday()));
            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();

        if (key != null) {
            user.setId(key.intValue());
        } else {
            throw new RuntimeException("Failed to add user");
        }

        log.info(String.format("User with email %s was added with ID %d.", user.getEmail(), user.getId()));
        return user;
    }

    @Override
    public User updateUser(User newUser) {
        String sql = "UPDATE users SET user_email = ?, user_login = ?, user_name = ?, " +
                "user_birthday = ? " +
                "WHERE user_id = ?";
        int id = newUser.getId();
        jdbcTemplate.update(sql, newUser.getEmail(), newUser.getLogin(), newUser.getName(), newUser.getBirthday(), id);
        log.info(String.format("User with id %d was updated", id));
        return newUser;
    }

    @Override
    public User getUserById(int id) {
        String sql = "SELECT * FROM users WHERE user_id = ? LIMIT 1";

        User user = jdbcTemplate.queryForObject(sql, new UserRowMapper(), id);

        return user;
    }

    @Override
    public boolean isUserExists(int userId) {
        String sql = "SELECT COUNT(*) FROM users WHERE user_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, userId);
        return count != null && count > 0;
    }

    @Override
    public boolean areUsersFriends(int userId, int friendId) {
        String sql = "SELECT COUNT(*) FROM friends " +
                "WHERE user_id = ? AND friend_id = ? AND friends_status = TRUE";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, userId, friendId);
        return count != null && count > 0;
    }

    @Override
    public void addFriend(int userId, int friendId) {
        String sql = "INSERT INTO friends (user_id, friend_id, friends_status) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, userId, friendId, true);
    }

    @Override
    public void deleteFriend(int userId, int friendId) {
        String sql = "DELETE FROM friends WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(sql, userId, friendId);
    }

    @Override
    public List<User> getFriendsOfUser(int userId) {
        String sql = "SELECT users.* FROM users WHERE user_id IN (" +
                "SELECT friend_id FROM friends WHERE friends_status = true AND user_id = ?)";
        return jdbcTemplate.query(sql, new UserRowMapper(), userId);
    }

    @Override
    public List<User> getCommonFriends(int userId, int otherId) {
        String sql = "SELECT users.* FROM users WHERE " +
                "user_id IN (SELECT friend_id FROM friends WHERE friends_status = true AND user_id = ?) AND " +
                "user_id IN (SELECT friend_id FROM friends WHERE friends_status = true AND user_id = ?)";

        return jdbcTemplate.query(sql, new UserRowMapper(), userId, otherId);
    }
}