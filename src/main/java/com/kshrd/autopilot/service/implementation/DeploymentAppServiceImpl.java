package com.kshrd.autopilot.service.implementation;

import com.kshrd.autopilot.entities.DeploymentApp;
import com.kshrd.autopilot.entities.Project;
import com.kshrd.autopilot.entities.ProjectDetails;
import com.kshrd.autopilot.entities.dto.DeploymentAppDto;
import com.kshrd.autopilot.entities.request.DeploymentAppRequest;
import com.kshrd.autopilot.entities.user.User;
import com.kshrd.autopilot.exception.AutoPilotException;
import com.kshrd.autopilot.exception.BadRequestException;
import com.kshrd.autopilot.repository.DeploymentAppRepository;
import com.kshrd.autopilot.repository.ProjectDetailRepository;
import com.kshrd.autopilot.repository.ProjectRepository;
import com.kshrd.autopilot.repository.UserRepository;
import com.kshrd.autopilot.service.DeploymentAppService;
import com.kshrd.autopilot.util.CurrentUserUtil;
import com.kshrd.autopilot.util.GitUtil;
import com.kshrd.autopilot.util.HttpUtil;
import com.kshrd.autopilot.util.Jenkins;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
    public DeploymentAppDto createDeploymentApp(DeploymentAppRequest request) throws IOException, InterruptedException {
        // check valid project id
        String email = CurrentUserUtil.getEmail();
        User user = userRepository.findUsersByEmail(email);
        Optional<Project> project = Optional.ofNullable(projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new AutoPilotException("Not found!", HttpStatus.NOT_FOUND, urlError, "You are not owner this project")));
        ProjectDetails projectDetails = projectDetailRepository.findByUserAndProject(user, project.get());
        if (projectDetails == null) {
            throw new AutoPilotException("Not owner!", HttpStatus.BAD_REQUEST, urlError, "You are not owner this project");
        }
        // insert to database
        DeploymentApp deploymentApp = new DeploymentApp();
        deploymentApp.setAppName(request.getAppName());
        deploymentApp.setProject(project.get());
        deploymentApp.setIpAddress("139.59.243.4");
        deploymentApp.setDomain("");
        deploymentApp.setBranch(request.getBranch());
        deploymentApp.setDescription(request.getDescription());
        deploymentApp.setBuildTool(request.getBuildTool());
        deploymentApp.setDependsOn(request.getDependsOn());
        deploymentApp.setCreateAt(LocalDateTime.now());
        deploymentApp.setGitPlatform(request.getGitPlatform());
        deploymentApp.setEmail(request.getEmail());
        deploymentApp.setFramework(request.getFramework());
        deploymentApp.setGitSrcUrl(request.getGitSrcUrl());
        deploymentApp.setToken(request.getToken());
        deploymentApp.setProjectPort(request.getProjectPort());
        deploymentApp.setPath(request.getPath());
//        Optional<DeploymentApp> deployment=deploymentAppRepository.findTopByOrderByCreate_atDesc();
//       if (deployment==null){
//           deploymentApp.setPort("30000");
//       }else {
//           deploymentApp.setPort(deployment.get().getPort()+1);
//       }
        // build url for repos (public or private)
        String newUrl = null;
        String path = "";
        String protocol = "";
        try {
            URL url = new URL(request.getGitSrcUrl());
            newUrl = url.getHost();
            path = url.getPath();
            protocol = url.getProtocol();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (request.getToken() != null) {
            request.setGitSrcUrl(protocol + "://" + request.getToken() + "@" + newUrl + path);
        }
        switch (request.getFramework().toLowerCase()) {
            case "spring":
                deploymentSpring(request);
                break;
            case "react":
                deployReactJs(request);
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

    public DeploymentAppDto deploymentSpring(DeploymentAppRequest request) {
        String repoName = "https://github.com/KSGA-Autopilot/" + request.getAppName() + "-cd" + ".git";
        try {
            Jenkins cli = new Jenkins();
            URL url = new URL(request.getGitSrcUrl());
            String appName = url.getPath();
            appName = appName.replaceAll("/", "").replaceAll(".git", "");
            String pathUsernmae = url.getPath();
            String[] arrayPath = pathUsernmae.split("/");
            String username = arrayPath[1];
            String repository = request.getAppName() + "-cd";
            String image = "autopilot/" + appName + ":1";
            GitUtil.createGitRepos(repository);
//            GitUtil.createApplication(repository, appName, repoName, username);
            GitUtil.createSpringDeployment(repository, request.getAppName() + "-deployment", username, 2, appName, request.getAppName(), 3000);
//            GitUtil.createSpringService(repository, request.getAppName() + "-service", username, 30000, 30000, 30001);
            GitUtil.createIngress(repository, request.getAppName() + "-ingress", username, "controlplane.hanyeaktong.site", appName, request.getAppName() + "-service", "30000");
            cli.createSpringJobConfig(request.getGitSrcUrl(), pathUsernmae, request.getBuildTool(), request.getBranch(), request.getAppName());
        } catch (Exception e) {
            e.printStackTrace();
        }


        // create cd repos
        // create job: build, test, and push image, update cd repos image
        // check if job is built successfully
        // make argo cd connect to cd repos
        // add domain and secure ssl
        // setup monitoring: server up -> send alert


        String image = "autopilot:customer-spring:2023-12-12-12:00";


        // cli.createJobConfig(request.getGit_src_url(),request.getBuild_tool(),request.getBranch(),request.getAppName());
        // push to docker hub
        // create cd repos
        // create deployment
        // create service
        return null;
    }

    public DeploymentAppDto deployReactJs(DeploymentAppRequest request) throws IOException, InterruptedException {
        // create cd repos
        URL url = new URL(request.getGitSrcUrl());
        String cdRepos = url.getPath();
        String[] arrayPath = cdRepos.split("/");
        String username = arrayPath[1].toLowerCase();
        System.out.println("Username: " + username);
        String projectName = arrayPath[2].toLowerCase().substring(0, arrayPath[2].length() - 4);
        System.out.println("Project Name: " + projectName);
        cdRepos = username + "-" + projectName + "-" + "cd";
        String jobName = cdRepos + UUID.randomUUID().toString().substring(0, 4);
        int cdResponse = GitUtil.createGitRepos(cdRepos);
        System.out.println(cdResponse);
        if (cdResponse != 201) {
            throw new BadRequestException("Unable to create.", "Enable to create git repository." + cdRepos);
        }
        // create job: build, test, and push image, update cd repos image
        try {
            Jenkins cli = new Jenkins();
            String appName = url.getPath();
            appName = appName.replaceAll("/", "").replaceAll(".git", "").replaceAll("_", "-");
            System.out.println("Appname=" + appName);
            String image = "autopilot/" + cdRepos + ":1";
            // create application for argocd
            GitUtil.createApplication(cdRepos, appName, username);
            // create deployment file
            GitUtil.createSpringDeployment(cdRepos, username + "-" + projectName + "-deployment", projectName, 2, projectName, image, request.getProjectPort());
            // create service file
            GitUtil.createSpringService(cdRepos, username + "-" + projectName + "-service", projectName, request.getProjectPort(), request.getProjectPort());
            // create ingress file
            GitUtil.createIngress(cdRepos, username + "-" + projectName + "-ingress", username, "controlplane.hanyeaktong.site", request.getPath(), username + "-" + projectName + "-service", request.getProjectPort().toString());
//            GitUtil.createArgoApp(cdRepos, appName, username);
            // create certificate for namespace
            if (request.getDomain() == null) {
                GitUtil.createNamespaceTlsCertificate(cdRepos, "controlplane.hanyeaktong.site", username);
            } else {
                GitUtil.createNamespaceTlsCertificate(cdRepos, request.getDomain(), username);
            }
            cli.createReactJobConfig(request.getGitSrcUrl(), username + "-" + projectName, request.getBranch(), cdRepos, jobName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // build job
        if (HttpUtil.buildJob(jobName) != 201) {
            throw new BadRequestException("Fail to build job", "Job have not been build successfully.");
        }
        // check if job is built successfully

        // generate cert for namespace
        // make argo cd create application
        // add domain and secure ssl
        // setup monitoring: server up -> send alert
        DeploymentApp deploymentApp = deploymentAppRepository.findByGitSrcUrl(request.getGitSrcUrl());
        return deploymentApp.toDeploymentAppDto();
    }
}
