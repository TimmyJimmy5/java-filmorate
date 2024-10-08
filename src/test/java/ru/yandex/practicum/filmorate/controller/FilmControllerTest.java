package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.BadInputException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.InMemoryFilmService;
import ru.yandex.practicum.filmorate.service.InMemoryUserService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.io.IOException;
import java.time.LocalDate;

public class FilmControllerTest {
    UserStorage userStorage = new InMemoryUserStorage();
    FilmStorage filmStorage = new InMemoryFilmStorage();
    UserService userService = new InMemoryUserService(userStorage);
    FilmService filmService = new InMemoryFilmService(filmStorage, (InMemoryUserService) userService);
    FilmController filmController = new FilmController(filmService);

    @Test
    public void assertErrorUponEmptyFilmCreate() throws IOException {
        BadInputException thrown = Assertions.assertThrows(BadInputException.class, () -> filmController.create(null));
        Assertions.assertEquals("Тело запроса не может быть пустым.", thrown.getMessage());
    }

    @Test
    public void assertErrorUponEmptyFilmUpdate() throws IOException {
        BadInputException thrown = Assertions.assertThrows(BadInputException.class, () -> filmController.create(null));
        Assertions.assertEquals("Тело запроса не может быть пустым.", thrown.getMessage());
    }

    @Test
    public void assertCreateSuccess() throws IOException {
        Film film = new Film();
        film.setName("Test");
        film.setDescription("Tested");
        film.setDuration(100);
        film.setReleaseDate(LocalDate.of(1996, 3, 1));
        filmController.create(film);
        Assertions.assertEquals(1, filmController.findAll().size());
    }

    @Test
    public void assertCreateErrorUponAbsentName() throws IOException {
        Film film = new Film();
        film.setDescription("Tested");
        film.setDuration(100);
        film.setReleaseDate(LocalDate.of(1996, 3, 1));
        BadInputException thrown = Assertions.assertThrows(BadInputException.class, () -> filmController.create(film));
        Assertions.assertEquals("Название не может быть пустым.", thrown.getMessage());
    }

    @Test
    public void assertCreateErrorUponAbsentDescription() throws IOException {
        Film film = new Film();
        film.setName("Test");
        film.setDuration(100);
        film.setReleaseDate(LocalDate.of(1996, 3, 1));
        BadInputException thrown = Assertions.assertThrows(BadInputException.class, () -> filmController.create(film));
        Assertions.assertEquals("Описание фильма не может быть пустым.", thrown.getMessage());
    }

    @Test
    public void assertCreateErrorUponAbsentDuration() throws IOException {
        Film film = new Film();
        film.setName("Test");
        film.setDescription("Tested");
        film.setReleaseDate(LocalDate.of(1996, 3, 1));
        BadInputException thrown = Assertions.assertThrows(BadInputException.class, () -> filmController.create(film));
        Assertions.assertEquals("Продолжительность фильма не может отсутствовать.", thrown.getMessage());
    }

    @Test
    public void assertCreateErrorUponNegativeDuration() throws IOException {
        Film film = new Film();
        film.setName("Test");
        film.setDescription("Tested");
        film.setDuration(-100);
        film.setReleaseDate(LocalDate.of(1996, 3, 1));
        BadInputException thrown = Assertions.assertThrows(BadInputException.class, () -> filmController.create(film));
        Assertions.assertEquals("Продолжительность фильма не может быть отрицательной.", thrown.getMessage());
    }

    @Test
    public void assertCreateErrorUponAbsentReleaseDate() throws IOException {
        Film film = new Film();
        film.setName("Test");
        film.setDescription("Tested");
        film.setDuration(100);
        BadInputException thrown = Assertions.assertThrows(BadInputException.class, () -> filmController.create(film));
        Assertions.assertEquals("Дата релиза не может быть пустой.", thrown.getMessage());
    }

    @Test
    public void assertCreateErrorUponReleaseDateBefore1895() throws IOException {
        Film film = new Film();
        film.setName("Test");
        film.setDescription("Tested");
        film.setDuration(100);
        film.setReleaseDate(LocalDate.of(1796, 3, 1));
        BadInputException thrown = Assertions.assertThrows(BadInputException.class, () -> filmController.create(film));
        Assertions.assertEquals("Дата релиза не может быть ранее 28 декабря 1895 года.", thrown.getMessage());
    }
}