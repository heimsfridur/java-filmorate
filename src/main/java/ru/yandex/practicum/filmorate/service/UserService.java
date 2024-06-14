package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public User addFriend(int userId, int friendId) {
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);
        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
        log.info(String.format("Users %d and %d are friends now!", userId, friendId));
        return user;
    }

    public User deleteFriend(int userId, int friendId) {
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);
        if (user.getFriends().contains(friendId)) {
            user.getFriends().remove(friendId);
            friend.getFriends().remove(userId);
        }
        return user;
    }

    public List<User> getFriendsOfUser(int userId) {
        return userStorage.getUserById(userId)
                .getFriends()
                .stream()
                .map(id -> userStorage.getUserById(id))
                .toList();
    }

    public List<User> getCommonFriends(int userId, int otherId) {
        Set<Integer> userFriends = userStorage.getUserById(userId).getFriends();
        Set<Integer> otherUserFriends = userStorage.getUserById(otherId).getFriends();
        return userFriends.stream()
                .filter(id -> otherUserFriends.contains(id))
                .map(id -> userStorage.getUserById(id))
                .toList();
    }
}
