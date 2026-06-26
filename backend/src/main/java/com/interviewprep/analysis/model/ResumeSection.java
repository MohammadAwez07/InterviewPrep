package com.interviewprep.analysis.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    property = "type"
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = ResumeSection.SummarySection.class, name = "SUMMARY"),
    @JsonSubTypes.Type(value = ResumeSection.ExperienceSection.class, name = "EXPERIENCE"),
    @JsonSubTypes.Type(value = ResumeSection.SkillsSection.class, name = "SKILLS"),
    @JsonSubTypes.Type(value = ResumeSection.EducationSection.class, name = "EDUCATION"),
    @JsonSubTypes.Type(value = ResumeSection.CertificationsSection.class, name = "CERTIFICATIONS"),
    @JsonSubTypes.Type(value = ResumeSection.ProjectsSection.class, name = "PROJECTS")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class ResumeSection {

    private String type;
    private String title;

    @EqualsAndHashCode(callSuper = true)
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SummarySection extends ResumeSection {
        private SummaryContent content;

        public SummarySection(String type, String title, SummaryContent content) {
            super(type, title);
            this.content = content;
        }
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExperienceSection extends ResumeSection {
        private ExperienceContent content;

        public ExperienceSection(String type, String title, ExperienceContent content) {
            super(type, title);
            this.content = content;
        }
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SkillsSection extends ResumeSection {
        private SkillsContent content;

        public SkillsSection(String type, String title, SkillsContent content) {
            super(type, title);
            this.content = content;
        }
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EducationSection extends ResumeSection {
        private EducationContent content;

        public EducationSection(String type, String title, EducationContent content) {
            super(type, title);
            this.content = content;
        }
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CertificationsSection extends ResumeSection {
        private CertificationsContent content;

        public CertificationsSection(String type, String title, CertificationsContent content) {
            super(type, title);
            this.content = content;
        }
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProjectsSection extends ResumeSection {
        private ProjectsContent content;

        public ProjectsSection(String type, String title, ProjectsContent content) {
            super(type, title);
            this.content = content;
        }
    }

    // Content classes
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SummaryContent {
        private String text;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExperienceContent {
        private String company;
        private String role;
        private String dates;
        private java.util.List<String> bullets;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SkillsContent {
        private java.util.List<SkillCategory> categories;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SkillCategory {
        private String name;
        private java.util.List<String> items;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EducationContent {
        private String institution;
        private String degree;
        private String dates;
        private String details;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CertificationsContent {
        private java.util.List<Certification> certifications;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Certification {
        private String name;
        private String issuer;
        private String date;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProjectsContent {
        private java.util.List<Project> projects;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Project {
        private String name;
        private String description;
        private java.util.List<String> technologies;
    }
}
