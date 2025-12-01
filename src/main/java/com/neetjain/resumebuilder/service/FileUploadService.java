package com.neetjain.resumebuilder.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.neetjain.resumebuilder.config.CloudinaryConfig;
import com.neetjain.resumebuilder.documents.Resume;
import com.neetjain.resumebuilder.dto.AuthResponse;
import com.neetjain.resumebuilder.repository.ResumeRepository;
import jakarta.mail.Multipart;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static java.nio.file.Paths.get;

@Service
@RequiredArgsConstructor
@Slf4j

public class FileUploadService {

    //Injecting cloudinary
    private final Cloudinary cloudinary;

    private final AuthService authService;

    private final ResumeRepository resumeRepository;

    public Map<String,String> uploadSingleImage(MultipartFile file) throws IOException {
        Map<String, Object> imageUploadResult = cloudinary.uploader().upload(file.getBytes(),ObjectUtils.asMap("resource_type","image"));
        log.info("Inside FileUploadService - uploadSingleImage() {}",imageUploadResult.get("secure_url").toString());
        return Map.of("imageUrl",imageUploadResult.get("secure_url").toString());

    }

    public Map<String, String> uploadResumeImages(String resumeId,
                                                  Object principal,
                                                  MultipartFile thumbnail,
                                                  MultipartFile profileImage) throws IOException {
        //Step 1: get the current profile from principal obj
        AuthResponse response = authService.getProfile(principal);

        //Step 2: get the existing Resume
        Resume existingResume = resumeRepository.findByUserIdAndId(response.getId() ,resumeId)
                .orElseThrow(() -> new RuntimeException("Resume not found"));

        //Step 3: upload the resume images and set the Resume
        Map<String, String> returnValue = new HashMap<>();
        Map<String, String> uploadResult;

        if(Objects.nonNull(thumbnail)) {
            uploadResult = uploadSingleImage(thumbnail);
            existingResume.setThumbnailLink(uploadResult.get("imageUrl"));
            returnValue.put("thumbnailLink",uploadResult.get("imageUrl"));
        }

        if(Objects.nonNull(profileImage)) {
            uploadResult = uploadSingleImage(profileImage);
            if(Objects.isNull(existingResume.getProfileInfo())) {
                existingResume.setProfileInfo(new Resume.ProfileInfo());
            }
            existingResume.getProfileInfo().setProfilePreviewUrl(uploadResult.get("imageUrl"));
            returnValue.put("profilePreviewUrl",uploadResult.get("imageUrl"));

        }
        //Step 4: save the details in DB
        resumeRepository.save(existingResume);
        returnValue.put("message", "Images uploaded successfully");

        //Step 5: return the result
        return returnValue;
    }
}
