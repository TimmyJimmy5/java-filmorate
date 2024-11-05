package ru.yandex.practicum.filmorate.dao.mappers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.ResultSet;
import java.sql.SQLException;

@Slf4j
@Component
public class ReviewRowMapper implements RowMapper<Review> {
    @Override
    public Review mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Review review = new Review();
        review.setReviewId(resultSet.getLong("id"));
        review.setContent(resultSet.getString("content"));
        review.setIsPositive(Boolean.parseBoolean(resultSet.getString("isPositive")));
        review.setUseful(resultSet.getInt("useful"));
        review.setUserId(resultSet.getLong("user_id"));
        review.setFilmId(resultSet.getLong("film_id"));
        return review;
    }
}
