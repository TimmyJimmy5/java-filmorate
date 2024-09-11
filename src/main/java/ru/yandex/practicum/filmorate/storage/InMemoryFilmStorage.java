package ru.yandex.practicum.filmorate.storage;

import lombok.Data;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Data
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films;

    public InMemoryFilmStorage() {
        this.films = new HashMap<>();
    }

    @Override
    public Collection<Film> findAll() {
        return films.values();
    }

    @Override
    public Film findFilmById(Long filmId) {
        if (!isExist(filmId)) {
            throw new NotFoundException("Фильм по данному ID не найден");
        }
        return films.get(filmId);
    }

    @Override
    public List<Film> getTopFilms(int size) {
        return films.values().stream()
                .sorted(Comparator.comparing(film -> -film.getLikes().size()))
                .limit(size)
                .collect(Collectors.toList());
    }

    @Override
    public Film create(Film film) {
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film update(Film newFilm) {
        return films.put(newFilm.getId(), newFilm);
    }

    @Override
    public boolean putLike(Long filmId, Long userId) {
        return films.get(filmId).getLikes().add(userId);
    }

    @Override
    public boolean deleteLike(Long filmId, Long userId) {
        return films.get(filmId).getLikes().remove(userId);
    }

    @Override
    public boolean isExist(Long filmId) {
        return films.containsKey(filmId);
    }
}
