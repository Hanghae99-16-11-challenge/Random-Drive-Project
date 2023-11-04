package com.example.randomdriveproject.history.repository;

import com.example.randomdriveproject.history.entity.Guide;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Repository
public class GuideJDBCRepository {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public GuideJDBCRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void batchInsert(List<Guide> guides) {
        String sql = "INSERT INTO guide (name, x, y, distance, duration, type, guidance, road_index, route_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Guide guide = guides.get(i);
                ps.setString(1, guide.getName());
                ps.setDouble(2, guide.getX());
                ps.setDouble(3, guide.getY());
                ps.setInt(4, guide.getDistance());
                ps.setInt(5, guide.getDuration());
                ps.setInt(6, guide.getType());
                ps.setString(7, guide.getGuidance());
                ps.setInt(8, guide.getRoadIndex());
                ps.setLong(9, guide.getRoute().getId());
            }

            @Override
            public int getBatchSize() {
                return guides.size();
            }
        });
    }
}

