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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/films")
public class FilmController {
    private final Logger log = LoggerFactory.getLogger(FilmController.class);
    private final Map<Long, Film> films = new HashMap<>();
    private final LocalDate FIRST_FILM_DATE = LocalDate.of(1895, 12, 28);
    private final static int MAX_NAME_LENGTH = 200;

    @GetMapping
    public Collection<Film> findAll() {
        return films.values();
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        filmNullCheck(film);
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
        filmNullCheck(newFilm);
        filmNullIdCheck(newFilm);
        if (films.containsKey(newFilm.getId())) {
            Film oldFilm = films.get(newFilm.getId());
            updateNewFilmFieldsFromOldFilm(newFilm, oldFilm);
            log.debug("Beginning validation of film fields");
            filmNameValidation(newFilm);
            filmReleaseDateValidation(newFilm);
            filmDurationValidation(newFilm);
            filmDescriptionValidation(newFilm);
            log.debug("Validations passed. Putting the new film into memory.");
            films.put(newFilm.getId(), newFilm);
            log.debug("New film with id {} successfully put into memory.", newFilm.getId());
            return newFilm;
        }
        log.error("Error: Фильм с Id = {} не найден.", newFilm.getId());
        throw new NotFoundException("Фильм с Id = " + newFilm.getId() + " не найден.");
    }

    private void filmDurationValidation(Film film) {
        if (film.getDuration() == null) {
            log.error("Error: продолжительность фильма не может отсутствовать.");
            throw new ConditionsNotMetException("Продолжительность фильма не может отсутствовать.");
        }
        if (film.getDuration() < 0) {
            log.error("Error: продолжительность фильма не может быть отрицательной.");
            throw new ConditionsNotMetException("Продолжительность фильма не может быть отрицательной.");
        }
    }

    private void filmNullIdCheck(Film film) {
        if (film.getId() == null) {
            log.error("Error: Id фильма должен быть указан.");
            throw new ConditionsNotMetException("Id фильма должен быть указан.");
        }
    }

    private void filmNullCheck(Film film) {
        if (film == null) {
            log.error("Тело запроса не может быть пустым.");
            throw new ConditionsNotMetException("Тело запроса не может быть пустым.");
        }
    }

    private void updateNewFilmFieldsFromOldFilm(Film newFilm, Film oldFilm) {
        if (newFilm.getName() == null || newFilm.getName().isEmpty()) {
            log.debug("Name is empty. Filling current name field with previous value: {}", oldFilm.getName());
            newFilm.setName(oldFilm.getName());
        }
        if (newFilm.getDescription() == null || newFilm.getDescription().isEmpty()) {
            log.debug("Description is empty. Filling current description field with previous value: {}", oldFilm.getDescription());
            newFilm.setDescription(oldFilm.getDescription());
        }
        if (newFilm.getDuration() == null) {
            log.debug("Duration is empty. Filling current duration field with previous value: {}", oldFilm.getDuration());
            newFilm.setDuration(oldFilm.getDuration());
        }
        if (newFilm.getReleaseDate() == null) {
            log.debug("ReleaseDate is empty. Filling current ReleaseDate field with previous value: {}", oldFilm.getReleaseDate());
            newFilm.setReleaseDate(oldFilm.getReleaseDate());
        }
    }

    private void filmDescriptionValidation(Film film) {
        if (film.getDescription() == null || film.getDescription().isEmpty()) {
            log.error("Error: Описание фильма не может быть пустым.");
            throw new ConditionsNotMetException("Описание фильма не может быть пустым.");
        }
    }

    private void filmReleaseDateValidation(Film film) {
        if (film.getReleaseDate() == null) {
            log.error("Error: Дата релиза не может быть пустой.");
            throw new ConditionsNotMetException("Дата релиза не может быть пустой.");
        }
        if (film.getReleaseDate().isBefore(FIRST_FILM_DATE)) {
            log.error("Error: Дата релиза не может быть ранее {}.", FIRST_FILM_DATE);
            throw new ConditionsNotMetException("Дата релиза не может быть ранее 28 декабря 1895 года.");
        }
    }

    private void filmNameValidation(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            log.error("Error: Название не может быть пустым.");
            throw new ConditionsNotMetException("Название не может быть пустым.");
        }
        if (film.getName().length() > MAX_NAME_LENGTH) {
            log.error("Error: Название длиннее {} знаков.", MAX_NAME_LENGTH);
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
