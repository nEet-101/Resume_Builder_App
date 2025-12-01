package com.neetjain.resumebuilder.controller;

import com.neetjain.resumebuilder.dto.AuthResponse;
import com.neetjain.resumebuilder.dto.LoginRequest;
import com.neetjain.resumebuilder.dto.RegisterRequest;
import com.neetjain.resumebuilder.service.AuthService;
import com.neetjain.resumebuilder.service.FileUploadService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

import static com.neetjain.resumebuilder.utils.AppConstants.*;

@RestController
@RequiredArgsConstructor
@Slf4j

@RequestMapping(AUTH_CONTROLLER)

public class AuthController {

    private final AuthService authService;
    private final FileUploadService fileUploadService;

    @PostMapping(REGISTER)
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        log.info("Inside AuthController - register(): {}",request);
        AuthResponse response = authService.register(request);
        log.info("Response from service : {}",response);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping(VERIFY_EMAIL)
    public ResponseEntity<?> verifyEmail(@RequestParam String token) {
        log.info("Inside AuthController - verifyEmail(): {}",token);
        authService.verifyEmail(token);
        return ResponseEntity.status(HttpStatus.OK).body(Map.of("message","Email verified successfully"));
    }

    @PostMapping(UPLOAD_PROFILE)
    public ResponseEntity<?> uploadImage(@RequestPart("image") MultipartFile file) throws IOException {
        log.info("Inside AuthController - uploadImage()");
        Map<String, String> response = fileUploadService.uploadSingleImage(file);
        return ResponseEntity.ok(response);
    }

    @PostMapping(LOGIN)
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping(RESEND_VERIFICATION)
    public ResponseEntity<?> resendVerification(@RequestBody Map<String, String> body) {
        //Step 1: Get the email from the request.
        String email = body.get("email");


        //Step 2: Add the validations
        if(Objects.isNull(email)) {
            return ResponseEntity.badRequest().body(Map.of("message", "Email is required"));
        }

        //Step 3: Call the service method to resend verification link.
        authService.resendVerification(email);

        //Step 4: Return response.
        return ResponseEntity.ok(Map.of("success",true,"message","Verification email sent"));
    }

    //Get user profile
    @GetMapping(PROFILE)
    public ResponseEntity<?> getProfile(Authentication authentication){
        //Step 1: Get Principle Object
         Object principalObject =  authentication.getPrincipal();

        //Step 2: Call the service method
        AuthResponse currentProfile =  authService.getProfile(principalObject);

        //Step 3: return the response
        return ResponseEntity.ok(currentProfile);
    }

}
