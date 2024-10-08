package ru.yandex.practicum.filmorate.annotations;

public @interface MinFilmDate {
    String message() default "Минимальная дата выпуска фильма не может быть ранее {value}";
    Class<?>[] groups() default {};
    Class<?>[] payload() default {};

    String value() default "1895-12-28";
}