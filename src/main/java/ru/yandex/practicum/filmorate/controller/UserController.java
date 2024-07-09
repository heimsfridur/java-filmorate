package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.EventService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final EventService eventService;

    @GetMapping
    public Collection<User> getAll() {
        log.info("Received a request to get all users");
        return userService.getAll();
    }

    @PostMapping
    public User add(@RequestBody @Valid User user) {
        log.info("Received a request to add user");
        return userService.add(user);
    }

    @PutMapping
    public User update(@RequestBody @Valid User newUser) {
        log.info("Received a request to update user");
        return userService.update(newUser);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable Integer id, @PathVariable Integer friendId) {
        log.info("Received a request to add friend.");
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable Integer id, @PathVariable Integer friendId) {
        log.info("Received a request to delete friend");
        userService.deleteFriend(id, friendId);
    }

    @GetMapping("{id}/friends")
    public List<User> getFriendsOfUser(@PathVariable Integer id) {
        log.info(String.format("Received a request to get friends of user with id %d", id));
        return userService.getFriendsOfUser(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable Integer id, @PathVariable Integer otherId) {
        log.info(String.format("Received a request to get common friends for users %d and %d", id, otherId));
        return userService.getCommonFriends(id, otherId);
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Integer id) {
        log.info(String.format("Received a request to get get User with id %d", id));
        return userService.getUserById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Integer id) {
        log.info(String.format("Received a request to delete user with id = %d", id));
        userService.deleteUser(id);
    }

    @GetMapping("/{id}/feed")
    public List<Event> getFeed(@PathVariable int id) {
        return eventService.getFeed(id);
    }

    @GetMapping("/{id}/recommendations")
    @ResponseStatus(HttpStatus.OK)
    public Collection<Film> getRecommendations(@PathVariable Integer id) {
        log.info(String.format("Received a request to get recommendations for user with id %d", id));
        return userService.getRecommendations(id);
    }
}
