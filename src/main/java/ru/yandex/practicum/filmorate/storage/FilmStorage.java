package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface FilmStorage {
    Collection<Film> findAll();

    Film create(Film film);

    Film update(Film newFilm);

    Map<Long, Film> getFilms();

    boolean putLike(Long filmId, Long userId);

    boolean deleteLike(Long filmId, Long userId);

    Optional<Film> findFilmById(Long id);

    List<Film> getTopFilms(int size);
}
