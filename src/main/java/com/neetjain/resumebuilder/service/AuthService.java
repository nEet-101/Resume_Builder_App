package com.neetjain.resumebuilder.service;

import com.neetjain.resumebuilder.documents.User;
import com.neetjain.resumebuilder.dto.AuthResponse;
import com.neetjain.resumebuilder.dto.LoginRequest;
import com.neetjain.resumebuilder.dto.RegisterRequest;
import com.neetjain.resumebuilder.exceptions.ResourceExistsException;
import com.neetjain.resumebuilder.repository.UserRepository;
import com.neetjain.resumebuilder.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    public final UserRepository userRepository;
    public final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Value("${app.base.url:http://localhost:8082}")
    private String appBaseUrl;


    public AuthResponse register(RegisterRequest request) {
      log.info("Inside AuthService: register() {}",request);

      if(userRepository.existsByEmail(request.getEmail())) {
          throw new ResourceExistsException("User already exits with the email");
      }
      User newUser = toDocument(request);
      userRepository.save(newUser);

//      Send verification email
        sendVerificationEmail(newUser);

        return toResponse(newUser);
    }

    private void sendVerificationEmail(User newUser) {
        log.info("Inside AuthService - sendVerificationEmail(): {}", newUser);
        try {
            String link = appBaseUrl+"/api/auth/verify-email?token="+newUser.getVerificationToken();
            String html = "<div style='font-family:sand-sarif'>" +
                    "<h2>Verify your email</h2>" +
                    "<p>Hi " +newUser.getName() + " , please confirm your email to activate your account </p>" +
                    "<p><a href='" + link
                    + "' style='display:inline-block;padding:10px 16px;background-color:#6366f1;color:#fff;border-radius:6px;text-decoration:none'>Verify Email</a></p>"
                    +
                    "<p>Or copy this link: " + link + "</p>" +
                    "<p>This link expires in 24 hours.</p>" +
                    "</div>";
            emailService.sendHtmlEmail(newUser.getEmail(),"Verify your email",html);
        } catch (Exception e) {
            log.error("Exception occured at sendVerificationEmail(): {}", e.getMessage());
            throw new RuntimeException("Failed to send varification email: "+e.getMessage());
        }
    }

    private AuthResponse toResponse(User newUser) {
        return AuthResponse.builder()
                .id(newUser.getId())
                .name(newUser.getName())
                .email(newUser.getEmail())
                .profileImgUrl(newUser.getProfileImgUrl())
                .emailVerified(newUser.isEmailVerified())
                .createdAt(newUser.getCreatedAt())
                .updatedAt(newUser.getUpdatedAt())
                .subscriptionPlan(newUser.getSubscriptionPlan())
                .build();
    }

    private User toDocument(RegisterRequest request){
        return User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword())) //password will be encoded
                .profileImgUrl(request.getProfileImgUrl())
                .subscriptionPlan("Basic")
                .emailVerified(false)
                .verificationToken(UUID.randomUUID().toString())
                .verificationExpires(LocalDateTime.now().plusHours(24))
                .build();
    }

    //Validating token
    public void verifyEmail(String token){
        log.info("Inside AuthService: verifyEmail() : {}", token);
        User user = userRepository.findByVerificationToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid or expire token"));

        if(user.getVerificationExpires() != null && user.getVerificationExpires().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Verification token has expired. Please request new one.");
        }

        user.setEmailVerified(true);
        user.setVerificationToken(null);
        user.setVerificationExpires(null);
        userRepository.save(user);
    }

    public AuthResponse login(LoginRequest request) {
        User existingUser =  userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("Invalid email or password"));

        //Check if existing password and entered password not matches
        if (!passwordEncoder.matches(request.getPassword(), existingUser.getPassword())) {
            throw new UsernameNotFoundException("Invalid email or password");
        }

        //Check if email is not verified
        if(!existingUser.isEmailVerified()) {
            throw new RuntimeException("Please verify your email before logging in");
        }


        String token = jwtUtil.generateToken(existingUser.getId());

        AuthResponse response = toResponse(existingUser);
        response.setToken(token);
        return response;

    }
}
