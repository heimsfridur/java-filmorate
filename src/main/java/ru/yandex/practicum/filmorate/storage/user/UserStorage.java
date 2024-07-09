package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    List<User> getAll();

    User add(User user);

    User update(User newUser);

    User getUserById(int id);

    boolean isExists(int userId);

    void addFriend(int userId, int friendId);

    boolean areUsersFriends(int userId, int friendId);

    void deleteFriend(int userId, int friendId);

    List<User> getFriendsOfUser(int userId);

    List<User> getCommonFriends(int userId, int otherId);

    void deleteUser(int userId);
}
