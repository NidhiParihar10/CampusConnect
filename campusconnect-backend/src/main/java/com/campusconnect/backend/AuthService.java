
package com.campusconnect.backend;

import org.springframework.dao.DuplicateKeyException;
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

        List<Map<String, Object>> students = jdbcTemplate.queryForList(sql, roll.trim());

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

    public void registerStudent(
            String name,
            String course,
            String roll,
            String password) {

        if (name == null || name.isBlank()
                || course == null || course.isBlank()
                || roll == null || roll.isBlank()
                || password == null || password.isBlank()) {
            throw new IllegalArgumentException(
                    "All fields are required");
        }

        name = name.trim();
        course = course.trim();
        roll = roll.trim();

        if (name.length() > 100
                || course.length() > 100
                || roll.length() > 50) {
            throw new IllegalArgumentException(
                    "One or more fields are too long");
        }

        if (password.length() < 8) {
            throw new IllegalArgumentException(
                    "Password must be at least 8 characters");
        }

        String salt = PasswordHasher.generateSalt();
        String hash = PasswordHasher.hashPassword(password, salt);

        try {
            jdbcTemplate.update(
                    """
                            INSERT INTO students
                                (roll, name, course, password_hash, password_salt)
                            VALUES (?, ?, ?, ?, ?)
                            """,
                    roll, name, course, hash, salt);

        } catch (DuplicateKeyException e) {
            throw new IllegalArgumentException(
                    "This roll number is already registered");
        }
    }
}
