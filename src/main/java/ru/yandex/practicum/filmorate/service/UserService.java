package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public User addUser(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        return userStorage.addUser(user);
    }

    public User updateUser(User newUser) {
        int userId = newUser.getId();
        if (!userStorage.isUserExists(userId)) {
            throw new NotFoundException(String.format("User with ID %d does not exist.", userId));
        }

        if (newUser.getName() == null || newUser.getName().isBlank()) {
            newUser.setName(newUser.getLogin());
        }
        return userStorage.updateUser(newUser);
    }

    public void addFriend(int userId, int friendId) {
        if (!userStorage.isUserExists(userId)) {
            throw new NotFoundException(String.format("User with ID %d does not exist.", userId));
        }
        if (!userStorage.isUserExists(friendId)) {
            throw new NotFoundException(String.format("User with ID %d does not exist.", friendId));
        }
        if (userStorage.areUsersFriends(userId, friendId)) {
            throw new IllegalArgumentException(String.format("Users with IDs %d and %d are already friends",
                    userId, friendId));
        }

        userStorage.addFriend(userId, friendId);
        log.info(String.format("Users %d and %d are friends now!", userId, friendId));
    }

    public void deleteFriend(int userId, int friendId) {
        if (!userStorage.isUserExists(userId)) {
            throw new NotFoundException(String.format("User with ID %d does not exist.", userId));
        }
        if (!userStorage.isUserExists(friendId)) {
            throw new NotFoundException(String.format("User with ID %d does not exist.", friendId));
        }
        if (!userStorage.areUsersFriends(userId, friendId)) {
           // throw new IllegalArgumentException(String.format("Users with IDs %d and %d are not friends already.",
             //       userId, friendId));
            log.info(String.format("Users with IDs %d and %d are not friends already. Do nothing.", userId, friendId));
            return;
        }

        userStorage.deleteFriend(userId, friendId);
        log.info(String.format("Users %d and %d are not friends now :(", userId, friendId));
    }

    public List<User> getFriendsOfUser(int userId) {
        if (!userStorage.isUserExists(userId)) {
            throw new NotFoundException(String.format("User with ID %d does not exist.", userId));
        }

        return userStorage.getFriendsOfUser(userId);
    }

    public List<User> getCommonFriends(int userId, int otherId) {
        if (!userStorage.isUserExists(userId) || !userStorage.isUserExists(otherId)) {
            throw new NotFoundException(String.format("User with ID %d or %id does not exist.", userId, otherId));
        }

        return userStorage.getCommonFriends(userId, otherId);
    }
}
