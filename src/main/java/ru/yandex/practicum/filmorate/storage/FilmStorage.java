package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface FilmStorage {
    Collection<Film> findAll();

    Film create(Film film);

    Film update(Film newFilm);

    Map<Long, Film> getFilms();

    boolean putLike(Long filmId, Long userId);

    boolean deleteLike(Long filmId, Long userId);

    Film findFilmById(Long filmId);

    List<Film> getTopFilms(int size);

    boolean isExist(Long filmId);
}
