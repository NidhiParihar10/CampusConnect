
package com.campusconnect.backend;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StudentController {

    private final JdbcTemplate jdbcTemplate;

    public StudentController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // Existing API - view all students
    @GetMapping("/api/students")
    public List<Map<String, Object>> getStudents() {

        String sql = """
                SELECT roll, name, course
                FROM students
                ORDER BY roll
                """;

        return jdbcTemplate.queryForList(sql);
    }

    // New API - logged-in student's profile
    @GetMapping("/api/students/me")
    public ResponseEntity<?> getMyProfile(
            Authentication authentication) {

        String roll = authentication.getName();

        String sql = """
                SELECT roll, name, course
                FROM students
                WHERE roll = ?
                """;

        List<Map<String, Object>> students =
                jdbcTemplate.queryForList(sql, roll);

        if (students.isEmpty()) {
            return ResponseEntity.status(404).body(
                    Map.of("error", "Student not found")
            );
        }

        return ResponseEntity.ok(students.get(0));
    }
}
