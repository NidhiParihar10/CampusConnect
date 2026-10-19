
package com.campusconnect.backend;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PostRequestDemoController {

    @PostMapping("/api/demo/request")
    public ResponseEntity<?> demoRequest(
            @RequestBody Map<String, String> data) {

        String topic = data.get("topic");
        String description = data.get("description");

        if (topic == null || topic.isBlank()
                || description == null || description.isBlank()) {

            return ResponseEntity.badRequest().body(
                    Map.of("error",
                            "Topic and description are required"));
        }

        return ResponseEntity.ok(
                Map.of(
                        "message", "Demo request received",
                        "topic", topic.trim(),
                        "description", description.trim()));
    }
}
