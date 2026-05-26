package com.scrap.auth;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.scrap.auth.dto.ForgotPasswordRequest;
import com.scrap.auth.dto.LoginRequest;
import com.scrap.auth.dto.LoginResponse;
import com.scrap.auth.dto.SignupRequest;
import com.scrap.auth.dto.UserProfileDTO;
import com.scrap.auth.entity.User;
import com.scrap.auth.entity.UserProfile;
import com.scrap.auth.repository.UserProfileRepository;
import com.scrap.auth.repository.UserRepository;
import com.scrap.common.security.JwtTokenUtil;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/user")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // ================= SIGNUP =================
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody SignupRequest signupRequest) {

        try {
            Optional<User> existingUser =
                    userRepository.findByEmail(signupRequest.getEmail());

            if (existingUser.isPresent()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Email already exists");
            }

            User user = userService.registerUser(
                    signupRequest.getEmail(),
                    signupRequest.getPassword()
            );

            UserProfile userProfile =
                    userProfileService.registerUserProfile(signupRequest, user);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("User registered successfully");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    // ================= LOGIN (FIXED) =================
   // ================= LOGIN =================
@PostMapping("/login")
public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest) {

    User user = userRepository.getByEmail(loginRequest.getEmail());

    if (user == null) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Invalid email or password");
    }

    String storedPassword = user.getPassword();

    boolean passwordMatched = false;

    try {

        // ✅ NEW USERS (BCrypt)
        if (storedPassword.startsWith("$2a$")
                || storedPassword.startsWith("$2b$")
                || storedPassword.startsWith("$2y$")) {

            passwordMatched =
                    passwordEncoder.matches(
                            loginRequest.getPassword(),
                            storedPassword
                    );
        }

        // ✅ OLD USERS (PLAIN TEXT)
        else {

            passwordMatched =
                    loginRequest.getPassword()
                            .equals(storedPassword);
        }

    } catch (Exception e) {

        passwordMatched = false;
    }

    if (!passwordMatched) {

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Invalid email or password");
    }

    // ✅ AUTO CONVERT OLD PASSWORD TO BCrypt
    if (!(storedPassword.startsWith("$2a$")
            || storedPassword.startsWith("$2b$")
            || storedPassword.startsWith("$2y$"))) {

        user.setPassword(
                passwordEncoder.encode(loginRequest.getPassword())
        );

        userRepository.save(user);
    }

    String token = jwtTokenUtil.generateToken(
            user.getUserId(),
            user.getEmail()
    );

    UserProfile userProfile =
            userProfileRepository.getByUser(user);

    LoginResponse loginResponse =
            new LoginResponse(token, userProfile);

    return ResponseEntity.ok(loginResponse);
}

    // ================= FORGOT PASSWORD =================
    @PutMapping("/forgotpassword")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request) {

        User user = userRepository.getByEmail(request.getEmail());

        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid email");
        }

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Passwords not matching");
        }

        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userService.updateUser(user);

        return ResponseEntity.ok("Password changed successfully!");
    }

    // ================= LOGOUT =================
    @GetMapping("/logout")
    public ResponseEntity<?> logoutUser(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok("Logout successful");
    }

    // ================= VERIFY =================
    @GetMapping("/verify/{userProfileId}")
    public ResponseEntity<?> verifyUser(@PathVariable Long userProfileId) {

        UserProfile userProfile =
                userProfileService.getUserProfile(userProfileId);

        if (userProfile != null) {
            return ResponseEntity.ok(userProfile);
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Unauthorized");
    }

    // ================= PROFILE =================
    @GetMapping("/profile/{userProfileId}")
    public ResponseEntity<?> getProfile(@PathVariable Long userProfileId) {

        UserProfile userProfile =
                userProfileService.getUserProfile(userProfileId);

        if (userProfile != null) {
            return ResponseEntity.ok(userProfile);
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Invalid request");
    }

    // ================= UPDATE PROFILE =================
    @PutMapping("/updateprofile/{userProfileId}")
    public ResponseEntity<?> updateProfile(
            @PathVariable Long userProfileId,
            @RequestBody UserProfileDTO dto) {

        UserProfile userProfile =
                userProfileService.getUserProfile(userProfileId);

        if (userProfile == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid user profile");
        }

        User user = userRepository.getById(dto.getUserId());

        userProfile.setCompanyName(dto.getCompanyName());
        userProfile.setCompanyAddress(dto.getCompanyAddress());
        userProfile.setEmailId(dto.getEmail());
        userProfile.setMobile(dto.getMobile());
        userProfile.setName(dto.getName());
        userProfile.setUpdatedOn(LocalDateTime.now());
        userProfile.setUser(user);

        userProfileRepository.save(userProfile);

        return ResponseEntity.ok(userProfile);
    }

    // ================= CURRENT USER =================
    @GetMapping("/user/profile")
    public ResponseEntity<?> getUserProfile(
            @RequestHeader("Authorization") String token) {

        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Unauthorized");
        }

        token = token.substring(7);

        if (!jwtTokenUtil.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid token");
        }

        String email = jwtTokenUtil.getUsernameFromToken(token);

        Optional<User> user = userService.findByEmail(email);

        return user.<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() ->
                        ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                .body("User not found"));
    }
}