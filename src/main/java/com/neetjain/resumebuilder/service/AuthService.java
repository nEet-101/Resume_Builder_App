package com.neetjain.resumebuilder.service;

import com.neetjain.resumebuilder.documents.User;
import com.neetjain.resumebuilder.dto.AuthResponse;
import com.neetjain.resumebuilder.dto.RegisterRequest;
import com.neetjain.resumebuilder.exceptions.ResourceExistsException;
import com.neetjain.resumebuilder.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    public final UserRepository userRepository;
    public AuthResponse register(RegisterRequest request) {
      log.info("Inside AuthSerice: register() {}",request);

      if(userRepository.existsByEmail(request.getEmail())) {
          throw new ResourceExistsException("User already exits with the email");
      }
      User newUser = toDocument(request);
      userRepository.save(newUser);

//      TODO : send verification email

        return toResponse(newUser);
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
                .password(request.getPassword())
                .profileImgUrl(request.getProfileImgUrl())
                .subscriptionPlan("Basic")
                .emailVerified(false)
                .verificationToken(UUID.randomUUID().toString())
                .verificationExpires(LocalDateTime.now().plusHours(24))
                .build();
    }

}
