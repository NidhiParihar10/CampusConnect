
package com.campusconnect.backend;

import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.ArrayList;
import java.util.Locale;

import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MatchingHelperController {

    private final JdbcTemplate jdbcTemplate;

    public MatchingHelperController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/api/requests/{id}/matching-helpers")
    public ResponseEntity<?> findMatchingHelpers(
            @PathVariable("id") int requestId,
            Authentication authentication) {

        List<Map<String, Object>> requests = jdbcTemplate.queryForList(
                """
                SELECT roll, topic, description
                FROM help_requests
                WHERE request_id = ?
                """,
                requestId
        );

        if (requests.isEmpty()) {
            return ResponseEntity.status(404).body(
                    Map.of("error", "Help request not found")
            );
        }

        Map<String, Object> helpRequest = requests.get(0);

        String ownerRoll = String.valueOf(helpRequest.get("roll"));
        String topic = String.valueOf(helpRequest.get("topic"));
        String description = helpRequest.get("description") == null
                ? ""
                : String.valueOf(helpRequest.get("description"));

        // Fetch skills of all students except the request owner.
        List<Map<String, Object>> studentSkills = jdbcTemplate.queryForList(
                """
                SELECT
                    s.roll,
                    s.name,
                    s.course,
                    ss.skill
                FROM students s
                JOIN student_skills ss
                    ON ss.roll = s.roll
                WHERE s.roll <> ?
                ORDER BY s.name ASC, ss.skill ASC
                """,
                ownerRoll
        );

        // Store one result per student.
        Map<String, Map<String, Object>> matchedStudents =
                new LinkedHashMap<>();

        for (Map<String, Object> row : studentSkills) {

            String studentRoll = String.valueOf(row.get("roll"));
            String skill = String.valueOf(row.get("skill")).trim();

            if (skill.isEmpty()) {
                continue;
            }

            boolean topicMatch = containsSkill(topic, skill);
            boolean descriptionMatch = containsSkill(description, skill);

            if (!topicMatch && !descriptionMatch) {
                continue;
            }

            Map<String, Object> student = matchedStudents.get(studentRoll);

            if (student == null) {
                student = new LinkedHashMap<>();
                student.put("roll", studentRoll);
                student.put("name", row.get("name"));
                student.put("course", row.get("course"));
                student.put("score", 0);
                student.put("matchedSkills", new ArrayList<String>());

                matchedStudents.put(studentRoll, student);
            }

            // Topic matches get higher weight.
            int points = 0;

            if (topicMatch) {
                points += 3;
            }

            if (descriptionMatch) {
                points += 1;
            }

            int currentScore = (Integer) student.get("score");
            student.put("score", currentScore + points);

            @SuppressWarnings("unchecked")
            List<String> skills =
                    (List<String>) student.get("matchedSkills");

            skills.add(skill);
        }

        List<Map<String, Object>> helpers =
                new ArrayList<>(matchedStudents.values());

        // Highest matching score first.
        helpers.sort((a, b) -> {
            int scoreA = (Integer) a.get("score");
            int scoreB = (Integer) b.get("score");

            int scoreComparison = Integer.compare(scoreB, scoreA);

            if (scoreComparison != 0) {
                return scoreComparison;
            }

            return String.valueOf(a.get("name"))
                    .compareToIgnoreCase(String.valueOf(b.get("name")));
        });

        return ResponseEntity.ok(
                Map.of(
                        "requestId", requestId,
                        "topic", topic,
                        "helpers", helpers
                )
        );
    }

    // Case-insensitive matching with basic word boundaries.
    // Example: "Java" matches "Java Basics"
    // but "SQL" will not match "NoSQL".
    private boolean containsSkill(String text, String skill) {

        if (text == null || skill == null || skill.isBlank()) {
            return false;
        }

        String normalizedText = text.toLowerCase(Locale.ROOT);
        String normalizedSkill = skill.trim().toLowerCase(Locale.ROOT);

        int index = normalizedText.indexOf(normalizedSkill);

        while (index >= 0) {

            int end = index + normalizedSkill.length();

            boolean leftBoundary =
                    index == 0 ||
                    !Character.isLetterOrDigit(
                            normalizedText.charAt(index - 1)
                    );

            boolean rightBoundary =
                    end == normalizedText.length() ||
                    !Character.isLetterOrDigit(
                            normalizedText.charAt(end)
                    );

            if (leftBoundary && rightBoundary) {
                return true;
            }

            index = normalizedText.indexOf(normalizedSkill, index + 1);
        }

        return false;
    }
}
