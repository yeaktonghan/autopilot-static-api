package com.kshrd.autopilot.service.implementation;

import com.kshrd.autopilot.entities.Project;
import com.kshrd.autopilot.entities.ProjectDetails;
import com.kshrd.autopilot.entities.dto.ProjectDto;
import com.kshrd.autopilot.entities.request.CreateTeamRequest;
import com.kshrd.autopilot.entities.user.User;
import com.kshrd.autopilot.exception.AutoPilotException;
import com.kshrd.autopilot.repository.ProjectDetailRepository;
import com.kshrd.autopilot.repository.ProjectRepository;
import com.kshrd.autopilot.repository.UserRepository;
import com.kshrd.autopilot.service.ProjectService;
import com.kshrd.autopilot.util.CurrentUserUtil;
import com.kshrd.autopilot.util.ProjectCodeGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service

public class ProjectServiceImpl implements ProjectService {
    @Value("${error.url}")
    private String urlError;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ProjectDetailRepository projectDetailRepository;

    public ProjectServiceImpl(ProjectRepository projectRepository, UserRepository userRepository, ProjectDetailRepository projectDetailRepository) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.projectDetailRepository = projectDetailRepository;
    }

    @Override
    public ProjectDto createProject(CreateTeamRequest request) {
        String code_team = ProjectCodeGenerator.generateUniqueCode();
        String email = CurrentUserUtil.getEmail();
        User user = userRepository.findUsersByEmail(email);
        Project project = new Project();
        project.setName(request.getName());
        project.setProject_code(code_team);
        project.setCreated_at(LocalDateTime.now());
        projectRepository.save(project);
        ProjectDetails projectDetails = new ProjectDetails();
        projectDetails.setProject(project);
        projectDetails.setUser(user);
        projectDetails.setIs_owner(true);
        projectDetailRepository.save(projectDetails);
        return project.toProjectDto();
    }

    @Override
    public List<ProjectDto> getProjectByUser() {
        String email = CurrentUserUtil.getEmail();
        User user = userRepository.findUsersByEmail(email);
        List<ProjectDetails> projectDetails = projectDetailRepository.findAllByUser(user);
        List<ProjectDto> projects = new ArrayList<>();
        for (ProjectDetails pro : projectDetails) {
            Project project = projectRepository.findById(pro.getProject().getId()).get();
            ProjectDto projectDto = project.toProjectDto();
            projectDto.setMember(projectDetailRepository.countAllByProject(project));
            projects.add(projectDto);
        }
        return projects;
    }

    @Override
    public ProjectDto editProject(CreateTeamRequest request, Integer id) {
        Project project = projectRepository.findAllById(id);
        if (project == null) {
            throw new AutoPilotException("Not found", HttpStatus.NOT_FOUND
                    , urlError, "Project ID: " + id + " not found"
            );
        }
        project.setName(request.getName());
        projectRepository.save(project);
        return project.toProjectDto();
    }

    @Override
    public ProjectDto joinProject(String project_code) {
        List<Project> projects = projectRepository.findAll();
        Project project = new Project();
        for (Project pro : projects) {
            if (pro.getProject_code().equals(project_code)) {
                project = pro;
            } else {
                project = null;
            }
        }
        String email = CurrentUserUtil.getEmail();
        User user = userRepository.findUsersByEmail(email);
        ProjectDetails projectDetails = projectDetailRepository.findByProject(project);
        if (project == null) {
            throw new AutoPilotException("Not found", HttpStatus.NOT_FOUND, urlError, "Project not found");
        } else if (user.getId().equals(projectDetails.getUser().getId())) {
            throw new AutoPilotException("Can not join", HttpStatus.BAD_REQUEST, urlError, "You already in project");
        } else {
            ProjectDetails newMember = new ProjectDetails();
            newMember.setUser(user);
            newMember.setProject(project);
            projectDetailRepository.save(newMember);
        }
        return project.toProjectDto();
    }

    @Override
    public void removeProject(Integer id) {
        String email = CurrentUserUtil.getEmail();
        User user = userRepository.findUsersByEmail(email);
        Project project = projectRepository.findAllById(id);
        ProjectDetails projectDetails = projectDetailRepository.findByUserAndProject(user, project);
        if (projectDetails == null) {
            throw new AutoPilotException("Not found", HttpStatus.NOT_FOUND, urlError, "Project not found");
        } else if (!projectDetails.getIs_owner()) {
            throw new AutoPilotException("Not owner", HttpStatus.BAD_REQUEST, urlError, "You are not project owner");
        } else {
            projectDetailRepository.deleteById(projectDetails.getId());
            projectRepository.deleteById(id);
        }
    }

}
