package com.kshrd.autopilot.service.implementation;

import com.kshrd.autopilot.entities.DeploymentApp;
import com.kshrd.autopilot.entities.Project;
import com.kshrd.autopilot.entities.ProjectDetails;
import com.kshrd.autopilot.entities.dto.DeploymentAppDto;
import com.kshrd.autopilot.entities.request.DeploymentAppRequest;
import com.kshrd.autopilot.entities.user.User;
import com.kshrd.autopilot.exception.AutoPilotException;
import com.kshrd.autopilot.repository.DeploymentAppRepository;
import com.kshrd.autopilot.repository.ProjectDetailRepository;
import com.kshrd.autopilot.repository.ProjectRepository;
import com.kshrd.autopilot.repository.UserRepository;
import com.kshrd.autopilot.service.DeploymentAppService;
import com.kshrd.autopilot.util.CurrentUserUtil;
import com.kshrd.autopilot.util.GitUtil;
import com.kshrd.autopilot.util.Jenkins;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import java.net.URL;
import java.util.List;

@Service
public class DeploymentAppServiceImpl implements DeploymentAppService {
    @Value("${error.url}")
    private String urlError;
    private final DeploymentAppRepository deploymentAppRepository;
    private final ProjectRepository projectRepository;
    private final ProjectDetailRepository projectDetailRepository;
    private final UserRepository userRepository;


    public DeploymentAppServiceImpl(DeploymentAppRepository deploymentAppRepository, ProjectRepository projectRepository, ProjectDetailRepository projectDetailRepository, UserRepository userRepository) {
        this.deploymentAppRepository = deploymentAppRepository;
        this.projectRepository = projectRepository;

        this.projectDetailRepository = projectDetailRepository;
        this.userRepository = userRepository;
    }

    @Override
    public DeploymentAppDto createDeploymentApp(DeploymentAppRequest request) {
        String newUrl = null;
        String path="";
        String protocol="";
        try{
            URL url=new URL(request.getGit_src_url());
            newUrl=url.getHost();
            path=url.getPath();
            protocol=url.getProtocol();
        }catch (Exception e){
            e.printStackTrace();
        }
        if (request.getToken() != null) {
            request.setGit_src_url(protocol+"://"+request.getToken()+"@"+newUrl+path);
        }
      switch (request.getFramework().toLowerCase()){
          case "spring": deploymentSpring(request);
              break;
          case "react" :
              break;
      }

        return null;
    }

    @Override
    public List<DeploymentAppDto> getAllDeploymentApps(Integer project_id) {
        String email = CurrentUserUtil.getEmail();
        User user = userRepository.findUsersByEmail(email);
        Project project = projectRepository.findById(project_id)
                .orElseThrow(() -> new AutoPilotException("Not found", HttpStatus.NOT_FOUND, urlError, "Project not found"));
        ProjectDetails projectDetails = projectDetailRepository.findByUserAndProject(user, project);
        if (projectDetails == null && project != null) {
            throw new AutoPilotException("Not Owner", HttpStatus.BAD_REQUEST, urlError, "You are not project owner");
        }
        List<DeploymentAppDto> deploymentApps = deploymentAppRepository.findAllByProject(project).stream().map(DeploymentApp::toDeploymentAppDto).toList();
        return deploymentApps;
    }
    public static DeploymentAppDto deploymentSpring(DeploymentAppRequest request){
        Jenkins cli = new Jenkins();
        GitUtil gitUtil=new GitUtil();
        String image = "autopilot:customer-spring:2023-12-12-12:00";
        try{
            Integer code=gitUtil.createGitRepos(request.getAppName());
            System.out.println("this is code :"+code);
        }catch (Exception e){
            e.printStackTrace();
        }

       // cli.createJobConfig(request.getGit_src_url(),request.getBuild_tool(),request.getBranch(),request.getAppName());
        // push to docker hub
        // create cd repos
        // create deployment
        // create service
        return null;
    }
}
