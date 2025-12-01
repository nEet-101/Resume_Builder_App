package com.neetjain.resumebuilder.documents;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "resumes")
public class Resume {

    @Id
    @JsonProperty("_id")
    private String id;

    private String userId;

    private String title;

    private String thumbnailLink;

    private Template template;

    private ProfileInfo profileInfo;

    private ContactInfo contactInfo;

    private List<WorkExperience> workExperience;

    private List<Education> education;

    private List<Skill> skills;

    private List<Project> projects;

    private List<Certification> certification;

    private List<Language> languages;

    private List<String> interests;

    @CreatedDate
    private LocalDateTime created;

    @LastModifiedDate
    private LocalDateTime updatedAt;


    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @Builder
    public static class Template{
        private String theme;
        private List<String> colorPalette;

    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @Builder
    public static class ProfileInfo {
        private String profilePreviewUrl;
        private String fullName;
        private String designation;
        private String summary;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @Builder
    public static class ContactInfo{
        private String email;
        private String phone;
        private String location;
        private String linkedinIn;
        private String github;
        private String website;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @Builder
    public static class WorkExperience{
        private String company;
        private String role;
        private String startDate;
        private String endDate;
        private String description;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @Builder
    public static class Education{
        private String degree;
        private String institution;
        private String startDate;
        private String endDate;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @Builder
    public static class Skill{
        private String name;
        private Integer progress;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @Builder
    public static class Project {
        private String title;
        private String description;
        private String github;
        private String liveDemo;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @Builder
    public static class Certification{
        private String title;
        private String issuer;
        private String year;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @Builder
    public static class Language{
        private String name;
        private Integer progress;
    }


}
