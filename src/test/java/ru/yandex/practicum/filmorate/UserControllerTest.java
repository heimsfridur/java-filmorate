package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.EventService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserStorage userStorage;

    @MockBean
    private UserService userService;

    @MockBean
    private EventService eventService;

    @Test
    void shouldAddUser() throws Exception {
        User user = User.builder()
                .email("test@test.ru")
                .login("coollogin")
                .name("name")
                .birthday(LocalDate.of(1990, 11, 10))
                .build();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        String requestBody = objectMapper.writeValueAsString(user);

        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isOk());
    }

    @Test
    void shouldUpdateUser() throws Exception {
        User user = User.builder()
                .email("test@test.ru")
                .login("coollogin")
                .name("name")
                .birthday(LocalDate.of(1990, 11, 10))
                .build();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        String requestBody = objectMapper.writeValueAsString(user);

        mockMvc.perform(post("/users")
                .contentType("application/json")
                .content(requestBody));

        User updatedUser = User.builder()
                .id(1)
                .email("test222222@test.ru")
                .login("coollogin")
                .name("name")
                .birthday(LocalDate.of(1990, 11, 10))
                .build();
        String requestBody2 = objectMapper.writeValueAsString(updatedUser);

        mockMvc.perform(put("/users")
                        .contentType("application/json")
                        .content(requestBody2))
                .andExpect(status().isOk());
    }

    @Test
    void testGetRecommendationsForUser() throws Exception {
        int userId = 1;
        Film film1 = Film.builder()
                .id(1)
                .name("Film 1")
                .description("Description 1")
                .build();
        Film film2 = Film.builder()
                .id(2)
                .name("Film 2")
                .description("Description 2")
                .build();
        List<Film> recommendations = List.of(film1, film2);
        when(userService.getRecommendations(userId)).thenReturn(recommendations);
        mockMvc.perform(get("/users/{id}/recommendations", userId)).andExpect(status().isOk());
    }
}
