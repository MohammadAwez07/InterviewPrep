package com.interviewprep.analysis.dto;

import com.interviewprep.analysis.model.JobAnalysis;
import com.interviewprep.analysis.model.ResumeSection;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public record GapAnalysisDto(
        UUID id,
        Integer readinessScore,
        List<SkillReadiness> skillBreakdown,
        List<String> strongAreas,
        List<String> gapAreas,
        List<String> recommendations,
        List<String> suggestedTopics,
        List<ResumeSectionDto> tailoredResumeSections,
        List<String> resumeChanges,
        Boolean cached,
        String jobTitle,
        String company
) {
    public static GapAnalysisDto from(JobAnalysis analysis) {
        List<ResumeSectionDto> resumeSections = analysis.getTailoredResumeSections() != null
                ? analysis.getTailoredResumeSections().stream()
                        .map(GapAnalysisDto::convertToDto)
                        .filter(dto -> dto != null)
                        .collect(Collectors.toList())
                : List.of();

        return new GapAnalysisDto(
                analysis.getId(),
                analysis.getReadinessScore(),
                analysis.getExtractedSkills() != null
                    ? analysis.getExtractedSkills().stream()
                        .map(SkillReadiness::from)
                        .collect(Collectors.toList())
                    : List.of(),
                analysis.getStrongAreas(),
                analysis.getGapAreas(),
                analysis.getRecommendations(),
                analysis.getSuggestedTopics(),
                resumeSections,
                analysis.getResumeChanges(),
                analysis.getCached(),
                analysis.getJobTitle(),
                analysis.getCompany()
        );
    }

    private static ResumeSectionDto convertToDto(ResumeSection section) {
        if (section == null) return null;

        return switch (section.getType()) {
            case "SUMMARY" -> {
                ResumeSectionDto.SummaryDto dto = new ResumeSectionDto.SummaryDto();
                if (section instanceof ResumeSection.SummarySection s && s.getContent() != null) {
                    ResumeSectionDto.SummaryContent content = new ResumeSectionDto.SummaryContent();
                    content.setText(s.getContent().getText());
                    dto.setContent(content);
                }
                yield dto;
            }
            case "EXPERIENCE" -> {
                ResumeSectionDto.ExperienceDto dto = new ResumeSectionDto.ExperienceDto();
                if (section instanceof ResumeSection.ExperienceSection s && s.getContent() != null) {
                    ResumeSectionDto.ExperienceContent content = new ResumeSectionDto.ExperienceContent();
                    content.setCompany(s.getContent().getCompany());
                    content.setRole(s.getContent().getRole());
                    content.setDates(s.getContent().getDates());
                    content.setBullets(s.getContent().getBullets());
                    dto.setContent(content);
                }
                yield dto;
            }
            case "SKILLS" -> {
                ResumeSectionDto.SkillsDto dto = new ResumeSectionDto.SkillsDto();
                if (section instanceof ResumeSection.SkillsSection s && s.getContent() != null) {
                    ResumeSectionDto.SkillsContent content = new ResumeSectionDto.SkillsContent();
                    if (s.getContent().getCategories() != null) {
                        content.setCategories(s.getContent().getCategories().stream().map(cat -> {
                            ResumeSectionDto.SkillCategory sc = new ResumeSectionDto.SkillCategory();
                            sc.setName(cat.getName());
                            sc.setItems(cat.getItems());
                            return sc;
                        }).collect(Collectors.toList()));
                    }
                    dto.setContent(content);
                }
                yield dto;
            }
            case "EDUCATION" -> {
                ResumeSectionDto.EducationDto dto = new ResumeSectionDto.EducationDto();
                if (section instanceof ResumeSection.EducationSection s && s.getContent() != null) {
                    ResumeSectionDto.EducationContent content = new ResumeSectionDto.EducationContent();
                    content.setInstitution(s.getContent().getInstitution());
                    content.setDegree(s.getContent().getDegree());
                    content.setDates(s.getContent().getDates());
                    content.setDetails(s.getContent().getDetails());
                    dto.setContent(content);
                }
                yield dto;
            }
            case "CERTIFICATIONS" -> {
                ResumeSectionDto.CertificationsDto dto = new ResumeSectionDto.CertificationsDto();
                if (section instanceof ResumeSection.CertificationsSection s && s.getContent() != null) {
                    ResumeSectionDto.CertificationsContent content = new ResumeSectionDto.CertificationsContent();
                    if (s.getContent().getCertifications() != null) {
                        content.setCertifications(s.getContent().getCertifications().stream().map(c -> {
                            ResumeSectionDto.Certification cert = new ResumeSectionDto.Certification();
                            cert.setName(c.getName());
                            cert.setIssuer(c.getIssuer());
                            cert.setDate(c.getDate());
                            return cert;
                        }).collect(Collectors.toList()));
                    }
                    dto.setContent(content);
                }
                yield dto;
            }
            case "PROJECTS" -> {
                ResumeSectionDto.ProjectsDto dto = new ResumeSectionDto.ProjectsDto();
                if (section instanceof ResumeSection.ProjectsSection s && s.getContent() != null) {
                    ResumeSectionDto.ProjectsContent content = new ResumeSectionDto.ProjectsContent();
                    if (s.getContent().getProjects() != null) {
                        content.setProjects(s.getContent().getProjects().stream().map(p -> {
                            ResumeSectionDto.Project proj = new ResumeSectionDto.Project();
                            proj.setName(p.getName());
                            proj.setDescription(p.getDescription());
                            proj.setTechnologies(p.getTechnologies());
                            return proj;
                        }).collect(Collectors.toList()));
                    }
                    dto.setContent(content);
                }
                yield dto;
            }
            default -> null;
        };
    }
}
