package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Data
@Builder
public class Review {
    @Min(value = 1, message = "reviewId should not be less than 1")
    private Long reviewId;
    @NotNull(message = "operation is mandatory")
    @NotBlank(message = "operation can not be blank")
    @Size(min = 1, max = 50)
    private String content;
    private Boolean isPositive;
    @Min(value = 0, message = "userId should not be less than 0")
    private Long userId;
    @Min(value = 1, message = "filmId should not be less than 1")
    private Long filmId;
    private Long useful;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Review review = (Review) o;
        return Objects.equals(reviewId, review.reviewId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reviewId);
    }

    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("content", content);
        values.put("is_positive", isPositive);
        values.put("user_id", userId);
        values.put("film_id", filmId);
        values.put("useful", useful);
        return values;
    }
}
