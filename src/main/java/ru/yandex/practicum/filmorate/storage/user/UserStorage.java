package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;

public interface UserStorage {
    public Collection<User> getAllUsers();

    public User addUser(User user);

    public User updateUser(User newUser);

    public User getUserById(int id);

    public boolean isUserExists(int userId);

    public void addFriend(int userId, int friendId);

    public boolean areUsersFriends(int userId, int friendId);

    public void deleteFriend(int userId, int friendId);

    public List<User> getFriendsOfUser(int userId);

    public List<User> getCommonFriends(int userId, int otherId);
}
