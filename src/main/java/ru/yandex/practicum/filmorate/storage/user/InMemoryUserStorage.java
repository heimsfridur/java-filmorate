package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private HashMap<Integer, User> users = new HashMap<>();
    private int userId = 1;

    @Override
    public Collection<User> getAllUsers() {
        return users.values();
    }

    @Override
    public User addUser(User user) {
        user.setId(userId++);
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        user.setFriends(new HashSet<>());
        user.setLikedFilms(new HashSet<>());
        users.put(user.getId(), user);
        log.info(String.format("User with id %d was added.", userId - 1));
        return user;
    }

    @Override
    public User updateUser(User newUser) {
        int userId = newUser.getId();
        User oldUser = getUserById(userId);

        if (users.containsKey(userId)) {
            if (newUser.getName() == null) {
                newUser.setName(newUser.getLogin());
            }
            newUser.setLikedFilms(oldUser.getLikedFilms());
            newUser.setFriends(oldUser.getFriends());

            users.put(userId, newUser);
            log.info(String.format("User with id %d was updated", userId));
            return newUser;
        }

        log.warn(String.format("Can not update user. There is no user with id %d", userId));
        throw new NotFoundException(String.format("User with id %d is not found", newUser.getId()));
    }

    @Override
    public User getUserById(int id) {
        if (users.containsKey(id)) {
            return users.get(id);
        } else {
            log.warn(String.format("Can't find user with id %d", id));
            throw new NotFoundException(String.format("Can't find user with id %d", id));
        }
    }
}
