package com.neetjain.resumebuilder.repository;

import com.neetjain.resumebuilder.documents.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(@Email(message = "Email should be valid") @NotBlank(message = "Email is required") String email);

    Optional<User> findByVerificationToken(String verificationToken);
}