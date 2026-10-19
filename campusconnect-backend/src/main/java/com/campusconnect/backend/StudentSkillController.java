
package com.campusconnect.backend;

import java.util.List;
import java.util.Map;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StudentSkillController {

    private final JdbcTemplate jdbcTemplate;

    public StudentSkillController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // Get skills of the logged-in student
    @GetMapping("/api/skills/me")
    public List<String> getMySkills(Authentication authentication) {

        String roll = authentication.getName();

        return jdbcTemplate.queryForList(
                """
                        SELECT skill
                        FROM student_skills
                        WHERE roll = ?
                        ORDER BY skill ASC
                        """,
                String.class,
                roll);
    }

    // Add a new skill for the logged-in student
    @PostMapping("/api/skills/me")
    public ResponseEntity<?> addMySkill(
            @RequestBody Map<String, String> data,
            Authentication authentication) {

        String roll = authentication.getName();
        String skill = data.get("skill");

        // Validate skill
        if (skill == null || skill.isBlank()) {
            return ResponseEntity.badRequest().body(
                    Map.of("error", "Skill cannot be empty"));
        }

        skill = skill.trim();

        if (skill.length() > 50) {
            return ResponseEntity.badRequest().body(
                    Map.of("error", "Skill must be 50 characters or less"));
        }

        try {
            jdbcTemplate.update(
                    """
                            INSERT INTO student_skills (roll, skill)
                            VALUES (?, ?)
                            """,
                    roll,
                    skill);

            return ResponseEntity.status(201).body(
                    Map.of(
                            "message", "Skill added successfully",
                            "skill", skill));

        } catch (DuplicateKeyException exception) {
            return ResponseEntity.status(409).body(
                    Map.of("error", "You already have this skill"));
        }
    }
}
