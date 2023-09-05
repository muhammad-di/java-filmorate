package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
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
@AllArgsConstructor
public class Event {
    @NotNull(message = "timestamp is mandatory")
    private final Long timestamp;
    @NotNull(message = "eventType is mandatory")
    @NotBlank(message = "operation can not be blank")
    private final EventType eventType;
    @NotNull(message = "operation is mandatory")
    @NotBlank(message = "operation can not be blank")
    private final Operation operation;
    @Min(value = 1, message = "eventId should not be less than 1")
    private final Long eventId;
    @Min(value = 1, message = "entityId should not be less than 1")
    private final Long entityId;
    @Min(value = 1, message = "userId should not be less than 1")
    private Long userId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event that = (Event) o;
        return Objects.equals(eventId, that.eventId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventId);
    }

    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("user_id", userId);
        values.put("entity_id", entityId);
        values.put("event_type", eventType);
        values.put("operation", operation);
        values.put("event_timestamp", timestamp);

        return values;
    }
}
