package ru.yandex.practicum.filmorate.storageTests;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import(UserDbStorage.class)
public class UserDbStorageTest {
    private final UserStorage userStorage;

    @Test
    @Sql(scripts = {"/test-data.sql"})
    @DirtiesContext
    public void shouldGetUserByIdTest() {
        User user = userStorage.getUserById(1);

        assertThat(user)
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", 1);
    }

    @Test
    @Sql(scripts = {"/test-data.sql"})
    @DirtiesContext
    public void shouldGetAllUsersTest() {
        List<User> users = userStorage.getAllUsers();

        assertEquals(3, users.size(), "The size of users list is incorrect.");
        assertThat(users.get(0).getEmail()).isEqualTo("user1@example.com");
        assertThat(users.get(1).getEmail()).isEqualTo("user2@example.com");
    }

    @Test
    @DirtiesContext
    public void shouldAddUserTest() {
        User user = User.builder()
                .email("test@test.ru")
                .login("coollogin")
                .name("name")
                .birthday(LocalDate.of(1990, 11, 10))
                .build();

        userStorage.addUser(user);
        List<User> users = userStorage.getAllUsers();

        assertEquals(1, users.size(), "The user was not added.");
        assertThat(users.get(0)).hasFieldOrPropertyWithValue("id", 1);
    }

    @Test
    @Sql(scripts = {"/test-data.sql"})
    @DirtiesContext
    public void shouldUpdateUserTest() {
        User newUser = User.builder()
                .id(1)
                .email("new_email")
                .login("new_login")
                .name("new_user_name")
                .birthday(LocalDate.of(2023, 10, 11))
                .build();

        userStorage.updateUser(newUser);

        User updtedUser = userStorage.getUserById(1);
        assertThat(updtedUser).hasFieldOrPropertyWithValue("id", 1);
        assertThat(updtedUser).hasFieldOrPropertyWithValue("name", "new_user_name");
        assertThat(updtedUser).hasFieldOrPropertyWithValue("email", "new_email");
    }

    @Test
    @Sql(scripts = {"/test-data.sql"})
    @DirtiesContext
    public void shouldAddAndDeleteFriendsTest() {
        userStorage.addFriend(1, 2);
        List<User> friends = userStorage.getFriendsOfUser(1);

        assertEquals(1, friends.size(), "Friend was not added.");

        userStorage.deleteFriend(1, 2);
        assertEquals(0, userStorage.getFriendsOfUser(1).size(), "Friend was not deleted.");
    }

    @Test
    @Sql(scripts = {"/test-data.sql"})
    @DirtiesContext
    public void shouldGetCommonFriendsTest() {
        userStorage.addFriend(1, 2);
        userStorage.addFriend(3, 2);
        List<User> commonFriends = userStorage.getCommonFriends(1, 3);

        assertEquals(1, commonFriends.size(), "Amount of commin friends isincorrect.");
        assertEquals(2, commonFriends.get(0).getId(), "Wrong common friend.");
    }
}

