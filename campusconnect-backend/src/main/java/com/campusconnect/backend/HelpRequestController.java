
package com.campusconnect.backend;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelpRequestController {

        private final JdbcTemplate jdbcTemplate;

        public HelpRequestController(JdbcTemplate jdbcTemplate) {
                this.jdbcTemplate = jdbcTemplate;
        }

        // View all help requests
        @GetMapping("/api/requests")
        public List<Map<String, Object>> getRequests() {

                String sql = """
                                SELECT
                                    request_id,
                                    roll,
                                    topic,
                                    description,
                                    status,
                                    assigned_helper
                                FROM help_requests
                                ORDER BY request_id DESC
                                """;

                return jdbcTemplate.queryForList(sql);
        }

        // Create a new help request
        @PostMapping("/api/requests")
        public ResponseEntity<?> createRequest(
                        @RequestBody Map<String, String> data,
                        Authentication authentication) {

                String roll = authentication.getName();

                String topic = data.get("topic");
                String description = data.get("description");

                if (topic == null || topic.isBlank()
                                || description == null || description.isBlank()) {

                        return ResponseEntity.badRequest().body(
                                        Map.of("error",
                                                        "Topic and description are required"));
                }

                String sql = """
                                INSERT INTO help_requests
                                (roll, topic, description, status)
                                VALUES (?, ?, ?, ?)
                                """;

                jdbcTemplate.update(
                                sql,
                                roll,
                                topic.trim(),
                                description.trim(),
                                "OPEN");

                return ResponseEntity.status(201).body(
                                Map.of(
                                                "message", "Help request created successfully",
                                                "roll", roll,
                                                "topic", topic.trim(),
                                                "status", "OPEN"));
        }
        
    // Accept another student's OPEN help request
    @PostMapping("/api/requests/{id}/accept")
    public ResponseEntity<?> acceptRequest(
            @PathVariable("id") int requestId,
            Authentication authentication) {

        String helperRoll = authentication.getName();

        // Find the request
        List<Map<String, Object>> requests =
                jdbcTemplate.queryForList(
                        """
                        SELECT roll, status
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

        String ownerRoll =
                String.valueOf(requests.get(0).get("roll"));

        // Student cannot accept own request
        if (helperRoll.equals(ownerRoll)) {
            return ResponseEntity.badRequest().body(
                    Map.of("error", "You cannot accept your own request")
            );
        }

        // Accept only if request is still OPEN
        int updatedRows = jdbcTemplate.update(
                """
                UPDATE help_requests
                SET status = 'ASSIGNED',
                    assigned_helper = ?
                WHERE request_id = ?
                  AND status = 'OPEN'
                  AND roll <> ?
                """,
                helperRoll,
                requestId,
                helperRoll
        );

        if (updatedRows == 0) {
            return ResponseEntity.status(409).body(
                    Map.of("error", "Request is no longer available")
            );
        }

        return ResponseEntity.ok(
                Map.of(
                        "message", "Help request accepted successfully",
                        "requestId", requestId,
                        "helperRoll", helperRoll,
                        "status", "ASSIGNED"
                )
        );
    }

    // Start working on an assigned help request
    @PostMapping("/api/requests/{id}/start")
    public ResponseEntity<?> startRequest(
            @PathVariable("id") int requestId,
            Authentication authentication) {

        // Logged-in student's roll number
        String helperRoll = authentication.getName();

        // Only the assigned helper can start the request
        int updatedRows = jdbcTemplate.update(
                """
                UPDATE help_requests
                SET status = 'IN_PROGRESS'
                WHERE request_id = ?
                  AND status = 'ASSIGNED'
                  AND assigned_helper = ?
                """,
                requestId,
                helperRoll
        );

        if (updatedRows == 0) {
            return ResponseEntity.status(409).body(
                    Map.of(
                            "error",
                            "Request is not ASSIGNED to you or cannot be started"
                    )
            );
        }

        return ResponseEntity.ok(
                Map.of(
                        "message", "Help request started successfully",
                        "requestId", requestId,
                        "helperRoll", helperRoll,
                        "status", "IN_PROGRESS"
                )
        );
    }

    
    // Resolve a help request after completing the work
    @PostMapping("/api/requests/{id}/resolve")
    public ResponseEntity<?> resolveRequest(
            @PathVariable("id") int requestId,
            Authentication authentication) {

        String helperRoll = authentication.getName();

        int updatedRows = jdbcTemplate.update(
                """
                UPDATE help_requests
                SET status = 'RESOLVED'
                WHERE request_id = ?
                  AND status = 'IN_PROGRESS'
                  AND assigned_helper = ?
                """,
                requestId,
                helperRoll
        );

        if (updatedRows == 0) {
            return ResponseEntity.status(409).body(
                    Map.of(
                            "error",
                            "Request is not IN_PROGRESS or not assigned to you"
                    )
            );
        }

        return ResponseEntity.ok(
                Map.of(
                        "message", "Help request resolved successfully",
                        "requestId", requestId,
                        "status", "RESOLVED"
                )
        );
    }

}
