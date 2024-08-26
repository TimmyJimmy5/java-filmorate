package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
public class FilmController {
    private final Map<Long, Film> films = new HashMap<>();
    private final static LocalDate FIRST_FILM_DATE = LocalDate.of(1895, 12, 28);

    @GetMapping
    public Collection<Film> findAll() {
        return films.values();
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        filmNameValidation(film);
        filmReleaseDateValidation(film);
        filmDurationValidation(film);
        filmDescriptionValidation(film);
        film.setId(getNextId());
        films.put(film.getId(), film);
        return film;
    }

    @PutMapping
    public Film update(@RequestBody Film newFilm) {
        if (newFilm.getId() == null) {
            throw new ConditionsNotMetException("Id фильма должен быть указан.");
        }
        if (films.containsKey(newFilm.getId())) {
            Film oldFilm = films.get(newFilm.getId());
            if (newFilm.getName() == null || newFilm.getName().isEmpty()) {
                newFilm.setName(oldFilm.getName());
            }
            if (newFilm.getDescription() == null || newFilm.getDescription().isEmpty()) {
                newFilm.setDescription(oldFilm.getDescription());
            }
            if (newFilm.getDuration() == null) {
                newFilm.setDuration(oldFilm.getDuration());
            }
            if (newFilm.getReleaseDate() == null) {
                newFilm.setReleaseDate(oldFilm.getReleaseDate());
            }
            filmNameValidation(newFilm);
            filmReleaseDateValidation(newFilm);
            filmDurationValidation(newFilm);
            filmDescriptionValidation(newFilm);
            films.put(newFilm.getId(), newFilm);
            return newFilm;
        }
        throw new NotFoundException("Фильм с Id = " + newFilm.getId() + " не найден.");
    }

    private void filmDurationValidation(Film film) {
        if (film.getDuration().isNegative()) {
            throw new ConditionsNotMetException("Продолжительность фильма не может быть отрицательной.");
        }
    }

    private void filmDescriptionValidation(Film film) {
        if (film.getDescription() == null || film.getDescription().isEmpty()) {
            throw new ConditionsNotMetException("Описание фильма не может быть пустым.");
        }
    }

    private void filmReleaseDateValidation(Film film) {
        if (film.getReleaseDate().isBefore(FIRST_FILM_DATE)) {
            throw new ConditionsNotMetException("Дата релиза не может быть ранее 28 декабря 1895 года.");
        }
    }

    private void filmNameValidation(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ConditionsNotMetException("Название не может быть пустым.");
        }
        if (film.getName().length() > 200) {
            throw new ConditionsNotMetException("Название длиннее 200 знаков.");
        }
    }

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
