package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists());
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
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test222222@test.ru"));
    }
}
