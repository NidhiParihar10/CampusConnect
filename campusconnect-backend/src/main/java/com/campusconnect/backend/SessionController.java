
package com.campusconnect.backend;

import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SessionController {

    @GetMapping("/api/auth/me")
    public Map<String, String> currentStudent(
            Authentication authentication) {

        return Map.of(
                "message", "Session active",
                "roll", authentication.getName());
    }
}
