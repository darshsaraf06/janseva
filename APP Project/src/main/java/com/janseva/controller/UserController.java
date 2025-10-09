package com.janseva.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.janseva.model.User;
import com.janseva.repository.UserRepository;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    // Register a new user
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody Map<String, String> userData) {
        try {
            String username = userData.get("username");
            String email = userData.get("email");
            String role = userData.getOrDefault("role", "citizen");

            // Check if username already exists
            if (userRepository.findByUsername(username).isPresent()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Username already exists"));
            }

            // Check if email already exists
            if (userRepository.findByEmail(email).isPresent()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Email already exists"));
            }

            // Create new user
            User newUser = new User();
            newUser.setUsername(username);
            newUser.setEmail(email);
            newUser.setRole(role);
            newUser.setPoints(0); // Start with 0 points

            User savedUser = userRepository.save(newUser);

            // Return user data (without sensitive info in future)
            Map<String, Object> response = new HashMap<>();
            response.put("id", savedUser.getId());
            response.put("username", savedUser.getUsername());
            response.put("email", savedUser.getEmail());
            response.put("role", savedUser.getRole());
            response.put("points", savedUser.getPoints());
            response.put("message", "User registered successfully");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Registration failed: " + e.getMessage()));
        }
    }

    // Login user (simple authentication - in production, use proper authentication)
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody Map<String, String> loginData) {
        try {
            String username = loginData.get("username");
            String email = loginData.get("email");

            Optional<User> user;
            if (username != null && !username.isEmpty()) {
                user = userRepository.findByUsername(username);
            } else if (email != null && !email.isEmpty()) {
                user = userRepository.findByEmail(email);
            } else {
                return ResponseEntity.badRequest().body(Map.of("error", "Username or email required"));
            }

            if (user.isPresent()) {
                User foundUser = user.get();
                
                // Return user data
                Map<String, Object> response = new HashMap<>();
                response.put("id", foundUser.getId());
                response.put("username", foundUser.getUsername());
                response.put("email", foundUser.getEmail());
                response.put("role", foundUser.getRole());
                response.put("points", foundUser.getPoints());
                response.put("message", "Login successful");

                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(Map.of("error", "User not found"));
            }

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Login failed: " + e.getMessage()));
        }
    }

    // Get user profile
    @GetMapping("/profile/{userId}")
    public ResponseEntity<?> getUserProfile(@PathVariable Long userId) {
        try {
            Optional<User> user = userRepository.findById(userId);
            if (user.isPresent()) {
                User foundUser = user.get();
                
                Map<String, Object> response = new HashMap<>();
                response.put("id", foundUser.getId());
                response.put("username", foundUser.getUsername());
                response.put("email", foundUser.getEmail());
                response.put("role", foundUser.getRole());
                response.put("points", foundUser.getPoints());

                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to get user profile: " + e.getMessage()));
        }
    }

    // Update user points (for gamification)
    @PutMapping("/points/{userId}")
    public ResponseEntity<?> updateUserPoints(@PathVariable Long userId, @RequestBody Map<String, Integer> pointsData) {
        try {
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                int pointsToAdd = pointsData.getOrDefault("points", 0);
                user.setPoints(user.getPoints() + pointsToAdd);
                
                User updatedUser = userRepository.save(user);
                
                Map<String, Object> response = new HashMap<>();
                response.put("id", updatedUser.getId());
                response.put("username", updatedUser.getUsername());
                response.put("points", updatedUser.getPoints());
                response.put("message", "Points updated successfully");

                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to update points: " + e.getMessage()));
        }
    }
}
