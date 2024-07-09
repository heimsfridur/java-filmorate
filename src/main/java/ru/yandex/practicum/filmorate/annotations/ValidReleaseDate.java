package ru.yandex.practicum.filmorate.annotations;

import jakarta.validation.Constraint;
import ru.yandex.practicum.filmorate.validators.ReleaseDateValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = ReleaseDateValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidReleaseDate {
    String message() default "Release date must not be before 28.12.1895";

    Class<?>[] groups() default {};

    Class<?>[] payload() default {};

    String value() default "1895-12-28";
}
