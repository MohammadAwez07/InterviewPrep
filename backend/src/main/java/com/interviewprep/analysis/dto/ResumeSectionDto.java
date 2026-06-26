package com.interviewprep.analysis.dto;

import com.interviewprep.analysis.model.ResumeSection;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.util.List;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    property = "type"
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = ResumeSectionDto.SummaryDto.class, name = "SUMMARY"),
    @JsonSubTypes.Type(value = ResumeSectionDto.ExperienceDto.class, name = "EXPERIENCE"),
    @JsonSubTypes.Type(value = ResumeSectionDto.SkillsDto.class, name = "SKILLS"),
    @JsonSubTypes.Type(value = ResumeSectionDto.EducationDto.class, name = "EDUCATION"),
    @JsonSubTypes.Type(value = ResumeSectionDto.CertificationsDto.class, name = "CERTIFICATIONS"),
    @JsonSubTypes.Type(value = ResumeSectionDto.ProjectsDto.class, name = "PROJECTS")
})
public abstract class ResumeSectionDto {

    private String type;
    private String title;

    public ResumeSectionDto() {}

    public ResumeSectionDto(String type, String title) {
        this.type = type;
        this.title = title;
    }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public static class SummaryDto extends ResumeSectionDto {
        private SummaryContent content;
        public SummaryDto() { super("SUMMARY", "Summary"); }
        public SummaryContent getContent() { return content; }
        public void setContent(SummaryContent content) { this.content = content; }
    }

    public static class ExperienceDto extends ResumeSectionDto {
        private ExperienceContent content;
        public ExperienceDto() { super("EXPERIENCE", "Experience"); }
        public ExperienceContent getContent() { return content; }
        public void setContent(ExperienceContent content) { this.content = content; }
    }

    public static class SkillsDto extends ResumeSectionDto {
        private SkillsContent content;
        public SkillsDto() { super("SKILLS", "Skills"); }
        public SkillsContent getContent() { return content; }
        public void setContent(SkillsContent content) { this.content = content; }
    }

    public static class EducationDto extends ResumeSectionDto {
        private EducationContent content;
        public EducationDto() { super("EDUCATION", "Education"); }
        public EducationContent getContent() { return content; }
        public void setContent(EducationContent content) { this.content = content; }
    }

    public static class CertificationsDto extends ResumeSectionDto {
        private CertificationsContent content;
        public CertificationsDto() { super("CERTIFICATIONS", "Certifications"); }
        public CertificationsContent getContent() { return content; }
        public void setContent(CertificationsContent content) { this.content = content; }
    }

    public static class ProjectsDto extends ResumeSectionDto {
        private ProjectsContent content;
        public ProjectsDto() { super("PROJECTS", "Projects"); }
        public ProjectsContent getContent() { return content; }
        public void setContent(ProjectsContent content) { this.content = content; }
    }

    // Content classes
    public static class SummaryContent {
        private String text;
        public String getText() { return text; }
        public void setText(String text) { this.text = text; }
    }

    public static class ExperienceContent {
        private String company;
        private String role;
        private String dates;
        private List<String> bullets;
        public String getCompany() { return company; }
        public void setCompany(String company) { this.company = company; }
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
        public String getDates() { return dates; }
        public void setDates(String dates) { this.dates = dates; }
        public List<String> getBullets() { return bullets; }
        public void setBullets(List<String> bullets) { this.bullets = bullets; }
    }

    public static class SkillsContent {
        private List<SkillCategory> categories;
        public List<SkillCategory> getCategories() { return categories; }
        public void setCategories(List<SkillCategory> categories) { this.categories = categories; }
    }

    public static class SkillCategory {
        private String name;
        private List<String> items;
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public List<String> getItems() { return items; }
        public void setItems(List<String> items) { this.items = items; }
    }

    public static class EducationContent {
        private String institution;
        private String degree;
        private String dates;
        private String details;
        public String getInstitution() { return institution; }
        public void setInstitution(String institution) { this.institution = institution; }
        public String getDegree() { return degree; }
        public void setDegree(String degree) { this.degree = degree; }
        public String getDates() { return dates; }
        public void setDates(String dates) { this.dates = dates; }
        public String getDetails() { return details; }
        public void setDetails(String details) { this.details = details; }
    }

    public static class CertificationsContent {
        private List<Certification> certifications;
        public List<Certification> getCertifications() { return certifications; }
        public void setCertifications(List<Certification> certifications) { this.certifications = certifications; }
    }

    public static class Certification {
        private String name;
        private String issuer;
        private String date;
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getIssuer() { return issuer; }
        public void setIssuer(String issuer) { this.issuer = issuer; }
        public String getDate() { return date; }
        public void setDate(String date) { this.date = date; }
    }

    public static class ProjectsContent {
        private List<Project> projects;
        public List<Project> getProjects() { return projects; }
        public void setProjects(List<Project> projects) { this.projects = projects; }
    }

    public static class Project {
        private String name;
        private String description;
        private List<String> technologies;
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public List<String> getTechnologies() { return technologies; }
        public void setTechnologies(List<String> technologies) { this.technologies = technologies; }
    }
}
