package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;


import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Data
@Builder
public class Director {
    @Min(value = 1, message = "Id should not be less than 1")
    private Long id;
    @NotNull(message = "Name is mandatory")
    @NotBlank(message = "Name can not be blank")
    private String name;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Director director = (Director) o;
        return id == director.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("id", id);
        values.put("name", name);
        return values;
    }

    public Boolean containsValidId() {
        return id != null && id > 0;
    }
}
