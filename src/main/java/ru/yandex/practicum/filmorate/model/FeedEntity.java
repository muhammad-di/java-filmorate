package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Data
@Builder
@AllArgsConstructor
public class FeedEntity {
    private final Long timestamp;
    private final EventType eventType;
    private final Operation operation;
    private final Long eventId;
    private final Long entityId;
    private Long userId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FeedEntity that = (FeedEntity) o;
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
