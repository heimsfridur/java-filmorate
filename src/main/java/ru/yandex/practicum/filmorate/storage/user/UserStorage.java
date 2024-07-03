package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;
import java.util.List;

public interface UserStorage {
    public List<User> getAll();

    public User add(User user);

    public User update(User newUser);

    public User getUserById(int id);

    public boolean isExists(int userId);

    public void addFriend(int userId, int friendId);

    public boolean areUsersFriends(int userId, int friendId);

    public void deleteFriend(int userId, int friendId);

    public List<User> getFriendsOfUser(int userId);

    public List<User> getCommonFriends(int userId, int otherId);

    public void deleteUser(int userId);
}
