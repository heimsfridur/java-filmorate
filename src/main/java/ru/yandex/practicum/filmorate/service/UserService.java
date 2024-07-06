package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.EventOperation;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;
    private final EventService eventService;
    private final FilmStorage filmStorage;

    public Collection<User> getAll() {
        return userStorage.getAll();
    }

    public User add(User user) {
        setCorrectName(user);
        return userStorage.add(user);
    }

    public User update(User newUser) {
        int userId = newUser.getId();
        if (!userStorage.isExists(userId)) {
            throw new NotFoundException(String.format("User with ID %d does not exist.", userId));
        }
        setCorrectName(newUser);

        return userStorage.update(newUser);
    }

    private void setCorrectName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }

    public void addFriend(int userId, int friendId) {
        if (!userStorage.isExists(userId)) {
            throw new NotFoundException(String.format("User with ID %d does not exist.", userId));
        }
        if (!userStorage.isExists(friendId)) {
            throw new NotFoundException(String.format("User with ID %d does not exist.", friendId));
        }
        if (userStorage.areUsersFriends(userId, friendId)) {
            throw new IllegalArgumentException(String.format("Users with IDs %d and %d are already friends",
                    userId, friendId));
        }

        userStorage.addFriend(userId, friendId);
        log.info(String.format("Users %d and %d are friends now!", userId, friendId));
        eventService.createEvent(userId, EventType.FRIEND, EventOperation.ADD, friendId);
    }

    public void deleteFriend(int userId, int friendId) {
        if (!userStorage.isExists(userId)) {
            throw new NotFoundException(String.format("User with ID %d does not exist.", userId));
        }
        if (!userStorage.isExists(friendId)) {
            throw new NotFoundException(String.format("User with ID %d does not exist.", friendId));
        }
        if (!userStorage.areUsersFriends(userId, friendId)) {
            log.info(String.format("Users with IDs %d and %d are not friends already. Do nothing.", userId, friendId));
            return;
        }

        userStorage.deleteFriend(userId, friendId);
        log.info(String.format("Users %d and %d are not friends now :(", userId, friendId));
        eventService.createEvent(userId, EventType.FRIEND, EventOperation.REMOVE, friendId);
    }

    public List<User> getFriendsOfUser(int userId) {
        if (!userStorage.isExists(userId)) {
            throw new NotFoundException(String.format("User with ID %d does not exist.", userId));
        }

        return userStorage.getFriendsOfUser(userId);
    }

    public List<User> getCommonFriends(int userId, int otherId) {
        if (!userStorage.isExists(userId) || !userStorage.isExists(otherId)) {
            throw new NotFoundException(String.format("User with ID %d or %d does not exist.", userId, otherId));
        }

        return userStorage.getCommonFriends(userId, otherId);
    }

    public User getUserById(int userId) {
        if (!userStorage.isExists(userId)) {
            throw new NotFoundException(String.format("User with ID %d does not exist.", userId));
        }
        return userStorage.getUserById(userId);
    }

    public void deleteUser(int userId) {
        if (!userStorage.isExists(userId)) {
            throw new NotFoundException(String.format("User with ID %d does not exist.", userId));
        }
        userStorage.deleteUser(userId);
    }

    public Collection<Film> getRecommendations(int userId) {
        checkUserId(userId);
        return filmStorage.getRecommendations(userId);
    }

    private void checkUserId(int userId) {
        if (!userStorage.isExists(userId)) {
            throw new NotFoundException(String.format("User with ID %d does not exist.", userId));
        }
    }
}