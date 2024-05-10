package ru.yandex.practicum.filmorate;

import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import jakarta.validation.Validator;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class UserTest {
    @Autowired
    private Validator validator;

    @Test
    public void shouldBeValidUser() {
        User user = User.builder()
                .email("test@test.ru")
                .login("coollogin")
                .name("name")
                .birthday(LocalDate.of(1990, 11, 10))
                .build();

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertEquals(0, violations.size());
    }

    @Test
    public void shouldBeNotValidWhenEmailIsIncorrect() {
        User user = User.builder()
                .email("testtest.ru")
                .login("coollogin")
                .name("name")
                .birthday(LocalDate.of(1990, 11, 10))
                .build();

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size());
    }

    @Test
    public void shouldBeNotValidWhenLoginIsIncorrect() {
        User user = User.builder()
                .email("test@test.ru")
                .login("cool login")
                .name("name")
                .birthday(LocalDate.of(1990, 11, 10))
                .build();
        User user2 = User.builder()
                .email("test@test.ru")
                .name("name")
                .birthday(LocalDate.of(1990, 11, 10))
                .build();

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size());

        Set<ConstraintViolation<User>> violations2 = validator.validate(user2);
        assertEquals(1, violations2.size());
    }

    @Test
    public void shouldBeNotValidWhenBirthdayInFuture() {
        User user = User.builder()
                .email("test@test.ru")
                .login("coollogin")
                .name("name")
                .birthday(LocalDate.of(2990, 11, 10))
                .build();

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size());
    }


}
