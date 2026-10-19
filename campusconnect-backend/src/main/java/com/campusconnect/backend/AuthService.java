
package com.campusconnect.backend;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class AuthService {

    private final JdbcTemplate jdbcTemplate;

    public AuthService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean verifyLogin(String roll, String password) {

        if (roll == null || roll.isBlank()
                || password == null || password.isEmpty()) {
            return false;
        }

        String sql = """
                SELECT password_hash, password_salt
                FROM students
                WHERE roll = ?
                """;

        List<Map<String, Object>> students = jdbcTemplate.queryForList(sql, roll);

        if (students.isEmpty()) {
            return false;
        }

        Map<String, Object> student = students.get(0);

        String storedHash = (String) student.get("password_hash");

        String storedSalt = (String) student.get("password_salt");

        if (storedHash == null || storedSalt == null) {
            return false;
        }

        return PasswordHasher.verifyPassword(
                password,
                storedSalt,
                storedHash);
    }
}
