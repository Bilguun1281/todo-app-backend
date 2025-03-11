package com.example.Todo.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.example.Todo.model.MyUser;
import com.example.Todo.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@RestController
@RequestMapping("/api/users")


public class UserController {

    private final AuthenticationManager authenticationManager;
    private final SecurityContextRepository securityContextRepository;


    public UserController(AuthenticationManager authenticationManager ) {
		this.authenticationManager = authenticationManager;
        this.securityContextRepository = new HttpSessionSecurityContextRepository();
	}

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder; // For password hashing


    // Register a new user
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Validated @RequestBody MyUser user) {
        Optional<MyUser> existingUser = userService.findByUsername(user.getUsername());
        if (existingUser.isPresent()) {
            return ResponseEntity.status(409).body("Username already exists!"); // 409 Conflict
        }

        // Hash the password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        MyUser createdUser = userService.createUser(user);
        return ResponseEntity.status(201).body(createdUser);
    }

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody MyUser user , HttpServletRequest request, HttpServletResponse response) {
        try {
            Authentication authenticationRequest =
                UsernamePasswordAuthenticationToken.unauthenticated(user.getUsername(), user.getPassword());
            Authentication authenticationResponse = this.authenticationManager.authenticate(authenticationRequest);
            
            // Set the authenticated user in SecurityContext
            SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
            securityContext.setAuthentication(authenticationResponse);
            SecurityContextHolder.setContext(securityContext);
    
            // Store SecurityContext in session
            securityContextRepository.saveContext(securityContext, request, response);

      
        // ✅ Fetch user from database to get userId
        Optional<MyUser> authenticatedUser = userService.findByUsername(user.getUsername());
        if (authenticatedUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found after authentication");
        }

        // Store userId in session
        request.getSession().setAttribute("userId", authenticatedUser.get().getId());

        // ✅ Return userId in response
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("message", "ok");
        responseBody.put("userId", authenticatedUser.get().getId());
        responseBody.put("username", authenticatedUser.get().getUsername());


        return ResponseEntity.ok(responseBody);
        } catch (AuthenticationException ex) {
            // Return 401 if authentication fails
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }
    }

    // Get user by username (Login simulation for now)
    @GetMapping("/login/{username}")
    public ResponseEntity<?> loginUser(@PathVariable String username) {
        Optional<MyUser> user = userService.findByUsername(username);
        
        if (user.isEmpty()) {
            return ResponseEntity.status(404).body("User not found!"); // 404 Not Found
        }

        return ResponseEntity.ok(user.get()); // 200 OK
    }

    @GetMapping("/all")
    public List<MyUser> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public Optional<MyUser> getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }
}
