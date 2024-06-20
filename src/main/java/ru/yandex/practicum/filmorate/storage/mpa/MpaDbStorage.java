package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mapper.MpaRowMapper;

import java.util.List;

@Slf4j
@Component
@AllArgsConstructor
public class MpaDbStorage implements MpaStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Mpa> getAllMpas() {
        String sql = "SELECT * FROM MPA ORDER BY mpa_id";
        return jdbcTemplate.query(sql, new MpaRowMapper());
    }

    @Override
    public Mpa getMpaById(int id) {
        String sql = "SELECT * FROM MPA WHERE mpa_id = ?";
        return jdbcTemplate.queryForObject(sql, new MpaRowMapper(), id);
    }

    @Override
    public boolean isMpaExist(int mpaId) {
        String sql = "SELECT COUNT(*) FROM MPA WHERE mpa_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, mpaId);
        return count != null && count > 0;
    }
}
