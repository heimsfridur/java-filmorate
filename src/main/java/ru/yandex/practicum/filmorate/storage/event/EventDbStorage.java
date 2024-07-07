package ru.yandex.practicum.filmorate.storage.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.storage.mapper.EventMapper;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class EventDbStorage implements EventStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Event> getFeed(int userId) {
        final String sql = "SELECT * FROM feed where user_id = ?";

        return jdbcTemplate.query(sql, new EventMapper(), userId);
    }

    @Override
    public void createEvent(Event event) {
        final String sql = "INSERT INTO " +
                "feed (user_id, timestamp, event_type, operation, entity_id) " +
                "VALUES (?,?,?,?,?)";

        jdbcTemplate.update(sql,
                event.getUserId(),
                event.getTimestamp(),
                event.getEventType().name(),
                event.getOperation().name(),
                event.getEntityId()
                );
    }
}
