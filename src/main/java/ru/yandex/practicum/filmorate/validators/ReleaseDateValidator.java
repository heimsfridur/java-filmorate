package ru.yandex.practicum.filmorate.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.yandex.practicum.filmorate.annotations.ValidReleaseDate;

import java.time.LocalDate;


public class ReleaseDateValidator implements ConstraintValidator<ValidReleaseDate, LocalDate> {
    private LocalDate minDate;

    @Override
    public void initialize(ValidReleaseDate constraintAnnotation) {
        this.minDate = LocalDate.parse(constraintAnnotation.value());
    }

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        return !value.isBefore(minDate);
    }
}