package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.BadInputException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Slf4j
@Service
public class InMemoryFilmService implements FilmService {
    private static final LocalDate FIRST_FILM_DATE = LocalDate.of(1895, 12, 28);
    private static final int MAX_NAME_LENGTH = 200;
    private final FilmStorage filmStorage;
    private final InMemoryUserService userService;

    @Autowired
    public InMemoryFilmService(FilmStorage filmStorage, InMemoryUserService userService) {
        this.filmStorage = filmStorage;
        this.userService = userService;
    }

    @Override
    public List<Film> getTopFilms(int size) {
        return filmStorage.getTopFilms(size);
    }

    @Override
    public Film findFilmById(Long filmId) {
        return filmStorage.findFilmById(filmId);
    }

    @Override
    public boolean putLike(Long id, Long userId) {
        checkId(id);
        userService.checkId(userId);
        return filmStorage.putLike(id, userId);
    }

    @Override
    public boolean deleteLike(Long filmId, Long userId) {
        checkId(filmId);
        userService.checkId(userId);
        return filmStorage.deleteLike(filmId, userId);
    }

    @Override
    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    @Override
    public Film create(Film film) {
        filmNullCheck(film);
        filmNameValidation(film);
        filmReleaseDateValidation(film);
        filmDurationValidation(film);
        filmDescriptionValidation(film);
        film.setId(getNextId());
        return filmStorage.create(film);
    }

    @Override
    public Film update(Film newFilm) {
        filmNullCheck(newFilm);
        checkId(newFilm.getId());
        if (filmStorage.getFilms().containsKey(newFilm.getId())) {
            Film oldFilm = filmStorage.getFilms().get(newFilm.getId());
            updateNewFilmFieldsFromOldFilm(newFilm, oldFilm);
            log.debug("Beginning validation of film fields");
            filmNameValidation(newFilm);
            filmReleaseDateValidation(newFilm);
            filmDurationValidation(newFilm);
            filmDescriptionValidation(newFilm);
            log.debug("Validations passed. Putting the new film into memory.");
            filmStorage.update(newFilm);
            log.debug("New film with id {} successfully put into memory.", newFilm.getId());
            return newFilm;
        }
        log.error("Error: Фильм с Id = {} не найден.", newFilm.getId());
        throw new NotFoundException("Фильм с Id = " + newFilm.getId() + " не найден.");
    }

    private void filmDurationValidation(Film film) {
        if (film.getDuration() == null) {
            log.error("Error: продолжительность фильма не может отсутствовать.");
            throw new BadInputException("Продолжительность фильма не может отсутствовать.");
        }
        if (film.getDuration() < 0) {
            log.error("Error: продолжительность фильма не может быть отрицательной.");
            throw new BadInputException("Продолжительность фильма не может быть отрицательной.");
        }
    }

    private void filmNullCheck(Film film) {
        if (film == null) {
            log.error("Тело запроса не может быть пустым.");
            throw new BadInputException("Тело запроса не может быть пустым.");
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
            throw new BadInputException("Описание фильма не может быть пустым.");
        }
    }

    private void filmReleaseDateValidation(Film film) {
        if (film.getReleaseDate() == null) {
            log.error("Error: Дата релиза не может быть пустой.");
            throw new BadInputException("Дата релиза не может быть пустой.");
        }
        if (film.getReleaseDate().isBefore(FIRST_FILM_DATE)) {
            log.error("Error: Дата релиза не может быть ранее {}.", FIRST_FILM_DATE);
            throw new BadInputException("Дата релиза не может быть ранее 28 декабря 1895 года.");
        }
    }

    private void filmNameValidation(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            log.error("Error: Название не может быть пустым.");
            throw new BadInputException("Название не может быть пустым.");
        }
        if (film.getName().length() > MAX_NAME_LENGTH) {
            log.error("Error: Название длиннее {} знаков.", MAX_NAME_LENGTH);
            throw new BadInputException("Название длиннее 200 знаков.");
        }
    }

    private long getNextId() {
        long currentMaxId = filmStorage.getFilms().keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    public void checkId(Long id) {
        if (!filmStorage.isExist(id)) {
            throw new NotFoundException("Объекта с ID " + id + " не существует");
        }
    }
}
