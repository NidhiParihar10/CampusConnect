
package com.campusconnect.backend;

import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LeaderboardController {

    private final JdbcTemplate jdbcTemplate;

    public LeaderboardController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/api/leaderboard")
    public List<Map<String, Object>> getLeaderboard() {

        return jdbcTemplate.queryForList("""
                SELECT
                    s.roll,
                    s.name,
                    s.course,
                    COUNT(h.request_id) AS helps_completed
                FROM students s
                LEFT JOIN help_requests h
                    ON h.assigned_helper = s.roll
                    AND h.status = 'RESOLVED'
                GROUP BY s.roll, s.name, s.course
                ORDER BY helps_completed DESC, s.name ASC
                """);
    }
}
