package com.neetjain.resumebuilder.repository;

import com.neetjain.resumebuilder.documents.Resume;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ResumeRepository extends MongoRepository<Resume, String> {
    List<Resume> findByUserIdOrderByUpdatedAtDesc(String userId);

    Optional<Resume> findByUserIdAndId(String userId, String id);
}
