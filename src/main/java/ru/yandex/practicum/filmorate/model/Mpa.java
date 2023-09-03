package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Min;
import java.util.Objects;

@Data
@Builder
@AllArgsConstructor
public class Mpa {
    @Min(1)
    private Integer id;
    private String name;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Mpa mpa = (Mpa) o;
        return id.equals(mpa.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public Boolean containsValidId() {
        return id != null && id > 0;
    }
}