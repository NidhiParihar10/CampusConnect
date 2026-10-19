
package com.campusconnect.backend;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

        private final AuthService authService;

        private final HttpSessionSecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();

        public AuthController(AuthService authService) {
                this.authService = authService;
        }

        @PostMapping("/api/auth/login")
        public ResponseEntity<?> login(
                        @RequestBody Map<String, String> data,
                        HttpServletRequest request,
                        HttpServletResponse response) {

                // Get login details
                String roll = data.get("roll");
                String password = data.get("password");

                // Validate input
                if (roll == null || roll.isBlank()
                                || password == null || password.isBlank()) {

                        return ResponseEntity.badRequest().body(
                                        Map.of(
                                                        "error",
                                                        "Roll and password are required"));
                }

                roll = roll.trim();

                // Verify password using SQLite database
                if (!authService.verifyLogin(roll, password)) {
                        return ResponseEntity.status(401).body(
                                        Map.of("error", "Invalid credentials"));
                }

                // Create or renew session after successful login
                HttpSession session = request.getSession(true);

                request.changeSessionId();

                // Create Spring Security authentication
                Authentication authentication = UsernamePasswordAuthenticationToken.authenticated(
                                roll,
                                null,
                                List.of());

                // Create security context
                SecurityContext context = SecurityContextHolder.createEmptyContext();

                context.setAuthentication(authentication);

                SecurityContextHolder.setContext(context);

                securityContextRepository.saveContext(
                                context,
                                request,
                                response);

                // Store logged-in student's roll number
                session.setAttribute("studentRoll", roll);

                // Successful login response
                return ResponseEntity.ok(
                                Map.of(
                                                "message", "Login successful",
                                                "roll", roll));
        }
        
@PostMapping("/api/auth/logout")
public ResponseEntity<?> logout(
        HttpServletRequest request,
        HttpServletResponse response) {

    // Clear Spring Security authentication
    SecurityContextHolder.clearContext();

    // Invalidate student session
    HttpSession session = request.getSession(false);

    if (session != null) {
        session.invalidate();
    }

    // Remove session cookie
    org.springframework.http.ResponseCookie cookie =
            org.springframework.http.ResponseCookie
                    .from("JSESSIONID", "")
                    .path("/")
                    .httpOnly(true)
                    .maxAge(0)
                    .build();

    response.addHeader(
            "Set-Cookie",
            cookie.toString()
    );

    return ResponseEntity.ok(
            Map.of("message", "Logout successful")
    );
}

}
