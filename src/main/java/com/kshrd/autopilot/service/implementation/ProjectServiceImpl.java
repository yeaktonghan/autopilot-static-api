package com.kshrd.autopilot.service.implementation;

import com.kshrd.autopilot.entities.Project;
import com.kshrd.autopilot.entities.ProjectDetails;
import com.kshrd.autopilot.entities.dto.DeploymentAppDto;
import com.kshrd.autopilot.entities.dto.DeploymentDBDto;
import com.kshrd.autopilot.entities.dto.ProjectDto;
import com.kshrd.autopilot.entities.dto.UserDto;
import com.kshrd.autopilot.entities.request.CreateTeamRequest;
import com.kshrd.autopilot.entities.user.User;
import com.kshrd.autopilot.exception.AutoPilotException;
import com.kshrd.autopilot.repository.ProjectDetailRepository;
import com.kshrd.autopilot.repository.ProjectRepository;
import com.kshrd.autopilot.repository.UserRepository;
import com.kshrd.autopilot.service.DeploymentAppService;
import com.kshrd.autopilot.service.DeploymentDBService;
import com.kshrd.autopilot.service.ProjectService;
import com.kshrd.autopilot.util.CurrentUserUtil;
import com.kshrd.autopilot.util.ProjectCodeGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service

public class ProjectServiceImpl implements ProjectService {
    @Value("${error.url}")
    private String urlError;

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final DeploymentAppService deploymentAppService;
    private final DeploymentDBService deploymentDBService;
    private final ProjectDetailRepository projectDetailRepository;

    public ProjectServiceImpl(ProjectRepository projectRepository, UserRepository userRepository, DeploymentAppService deploymentAppService, DeploymentDBService deploymentDBService, ProjectDetailRepository projectDetailRepository) {

        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.deploymentAppService = deploymentAppService;
        this.deploymentDBService = deploymentDBService;
        this.projectDetailRepository = projectDetailRepository;
    }

    public static String generateRandomColor() {
        Random random = new Random();
        String[] colorNames = {
                "00c6ff", "c000ff", "ff0086", "ffd600", "00ff2e", "00ffa6", "009fff"
        };
        int index = random.nextInt(colorNames.length);
        return colorNames[index];
    }

    @Override
    public ProjectDto createProject(CreateTeamRequest request) {
        String code_team = ProjectCodeGenerator.generateUniqueCode();
        String color = generateRandomColor();
        String email = CurrentUserUtil.getEmail();
        User user = userRepository.findUsersByEmail(email);
        Project project = new Project();
        List<ProjectDetails> projectDetails=projectDetailRepository.findAllByUser(user);
        for (ProjectDetails pjs:projectDetails) {
            Project pj = projectRepository.findById(pjs.getProject().getId()).get();
            if (pj.getName().equals(request.getName())) {
                throw new AutoPilotException("Already Exist", HttpStatus.BAD_REQUEST, urlError, "Your project already exist!");
            }
        }
        project.setName(request.getName());
        project.setProjectCode(code_team);
        project.setCreated_at(LocalDateTime.now());
        project.setColor(color);
        projectRepository.save(project);
        ProjectDetails projectDetail = new ProjectDetails();
        projectDetail.setProject(project);
        projectDetail.setUser(user);
        projectDetail.setIs_owner(true);
        projectDetailRepository.save(projectDetail);
        List<UserDto> userDtos = new ArrayList<>();
        UserDto userDto = userRepository.findById(projectDetail.getUser().getId()).get().toUserDto();
        userDtos.add(userDto);
        return project.toProjectDto(userDtos, true);
    }

    @Override
    public List<ProjectDto> getProjectByUser() {
        String email = CurrentUserUtil.getEmail();
        User user = userRepository.findUsersByEmail(email);

        List<ProjectDetails> projectDetails = projectDetailRepository.findAllByUser(user);

        List<ProjectDto> projectDtoList = new ArrayList<>();

        for (ProjectDetails projectDetail : projectDetails) {
            ProjectDto projectDto = projectDetail.getProject().toProjectDto();
            projectDto.setIsOwner(projectDetail.getIs_owner());

            List<UserDto> userDtos = new ArrayList<>();
            List<ProjectDetails> projectDetailsList = projectDetailRepository.findAllByProject(projectDetail.getProject());
            for (ProjectDetails member : projectDetailsList) {
                userDtos.add(member.getUser().toUserDto());
            }

            projectDto.setMembers(userDtos);

            projectDtoList.add(projectDto);
        }

        return projectDtoList;
    }

    @Override
    public ProjectDto getProjectById(Long id) {
        Optional<Project> project = projectRepository.findById(id);
        String email = CurrentUserUtil.getEmail();
        User user = userRepository.findUsersByEmail(email);
        if (!project.isPresent()) {
            throw new AutoPilotException("Project not found", HttpStatus.NOT_FOUND, urlError, "You project id  is not found!");
        }
        List<ProjectDetails> projectDetails = projectDetailRepository.findAllByProject(project.get());
        List<UserDto> userDtos = new ArrayList<>();
        for (ProjectDetails member : projectDetails) {
            userDtos.add(member.getUser().toUserDto());
        }
        ProjectDetails isOwner = projectDetailRepository.findByUserAndProject(user, project.get());
        return project.get().toProjectDto(userDtos, isOwner.getIs_owner());
    }


    @Override
    public ProjectDto editProject(CreateTeamRequest request, Long id) {
        Project project = projectRepository.findById(id).get();
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
        Project project = projectRepository.findByProjectCode(project_code);
        //System.out.println("find by project code"+project);
        ProjectDetails newMember=new ProjectDetails();
        String email = CurrentUserUtil.getEmail();
        User user = userRepository.findUsersByEmail(email);
        ProjectDetails projectDetails = projectDetailRepository.findByUserAndProject(user,project);
        if (project == null) {
            throw new AutoPilotException("Not found", HttpStatus.NOT_FOUND, urlError, "Project not found");
        } else if (projectDetails!=null) {
            throw new AutoPilotException("Can not join", HttpStatus.BAD_REQUEST, urlError, "You already in project");
        }
            newMember.setUser(user);
            newMember.setProject(project);
        projectDetailRepository.save(newMember);
        return project.toProjectDto();
    }

    @Override
    public ProjectDto changeImage(Long id, String url) {
        Optional<Project> project = projectRepository.findById(id);
        if (!project.isPresent()) {
            throw new AutoPilotException("Not Found!", HttpStatus.NOT_FOUND, urlError, "Your project not found");
        }
        project.get().setProjectPf(url);
        projectRepository.save(project.get());
        List<UserDto> userDtos = new ArrayList<>();
        List<ProjectDetails> projectDetailsList = projectDetailRepository.findAllByProject(project.get());
        for (ProjectDetails member : projectDetailsList) {
            userDtos.add(member.getUser().toUserDto());
        }
        project.get().toProjectDto().setMembers(userDtos);
        return project.get().toProjectDto(userDtos,true);
    }

    @Override
    public void kickMembers(Integer userId, Integer projectId) {

    }

    @Override
    public void removeProject(Long id) {
        String email = CurrentUserUtil.getEmail();
        User user = userRepository.findUsersByEmail(email);
        Project project = projectRepository.findById(id).get();
        List<DeploymentAppDto> deploymentAppDtos=deploymentAppService.getAllDeploymentApps(id);
        List<DeploymentDBDto> deploymentDBDtos=deploymentDBService.getDeploymentDatabaseByProjectId(id);
        if (deploymentDBDtos!=null||deploymentAppDtos!=null){
            throw new AutoPilotException("Can not remove", HttpStatus.NOT_FOUND, urlError, "Your project has deploymentÏ");
        }
        ProjectDetails projectDetails = projectDetailRepository.findByUserAndProject(user, project);
        if (projectDetails == null) {
            throw new AutoPilotException("Not found", HttpStatus.NOT_FOUND, urlError, "Project not found");
        } else if (!projectDetails.getIs_owner()) {
            throw new AutoPilotException("Not owner", HttpStatus.BAD_REQUEST, urlError, "You are not project owner");
        } else {
            List<ProjectDetails> projectDetailsList = projectDetailRepository.findAllByProject(project);
            for (ProjectDetails ps : projectDetailsList
            ) {
                projectDetailRepository.deleteById(ps.getId());
            }
            //projectDetailRepository.deleteByProject(project);
            projectRepository.deleteById(id);
        }
    }

}
