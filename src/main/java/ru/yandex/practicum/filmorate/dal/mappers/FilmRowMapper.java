package ru.yandex.practicum.filmorate.dal.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dal.repository.GenreRepository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;

@Component
public class FilmRowMapper implements RowMapper<Film> {
    private final GenreRepository genreRepository;

    public FilmRowMapper(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    @Override
    public Film mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(resultSet.getLong("id"));
        film.setName(resultSet.getString("name"));
        film.setDescription(resultSet.getString("description"));
        film.setReleaseDate(resultSet.getDate("release_date").toLocalDate());
        film.setDuration(resultSet.getInt("duration"));

        Rating rating = new Rating();
        rating.setId(resultSet.getLong("rating_id"));
        film.setRating(rating);

        Set<Genre> genres = genreRepository.getForFilm(film.getId());
        film.setGenres(genres);
        return film;
    }
}
