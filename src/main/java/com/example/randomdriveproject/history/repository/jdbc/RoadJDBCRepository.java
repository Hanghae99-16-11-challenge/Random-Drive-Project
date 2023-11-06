package com.example.randomdriveproject.history.repository.jdbc;

import com.example.randomdriveproject.history.entity.Road;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Repository
public class RoadJDBCRepository {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public RoadJDBCRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void batchInsert(List<Road> roads) {
        String sql = "INSERT INTO road (vertexes, route_id) VALUES (?, ?)";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Road road = roads.get(i);
                ps.setString(1, road.getVertexes());
                ps.setLong(2, road.getRoute().getId());
            }

            @Override
            public int getBatchSize() {
                return roads.size();
            }
        });
    }
}
