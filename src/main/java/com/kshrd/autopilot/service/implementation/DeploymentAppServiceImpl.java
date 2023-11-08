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

        String email = CurrentUserUtil.getEmail();
        User user = userRepository.findUsersByEmail(email);
        Optional<Project> project = Optional.ofNullable(projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new AutoPilotException("Not found!", HttpStatus.NOT_FOUND, urlError, "You are not owner this project")));
        ProjectDetails projectDetails = projectDetailRepository.findByUserAndProject(user, project.get());
        if (projectDetails == null) {
            throw new AutoPilotException("Not owner!", HttpStatus.BAD_REQUEST, urlError, "You are not owner this project");
        }
        System.out.println("this project" + project);
        // insert to database
        DeploymentApp deploymentApp = new DeploymentApp();
        deploymentApp.setAppName(request.getAppName());
        deploymentApp.setProject(project.get());
        deploymentApp.setIpAddress("139.59.243.4");
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
        //System.out.println("all request" + deploymentApp);
        deploymentApp.setDomain(request.getDomain());


        // build url for repos (public or private)
        String jobName = "";
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
                jobName = deploymentSpring(request);
                break;
            case "react":
                jobName = deployReactJs(request);
                break;
        }
        deploymentApp.setJobName(jobName);
        deploymentAppRepository.save(deploymentApp);
        return deploymentApp.toDeploymentAppDto();
    }

    @Override
    public List<DeploymentAppDto> getAllDeploymentApps(Long project_id) {
        String email = CurrentUserUtil.getEmail();
        User user = userRepository.findUsersByEmail(email);
        Project project = projectRepository.findById(project_id)
                .orElseThrow(() -> new AutoPilotException("Not found", HttpStatus.NOT_FOUND, urlError, "Project not found"));
        ProjectDetails projectDetails = projectDetailRepository.findByUserAndProject(user, project);
        if (projectDetails == null && project != null) {
            throw new AutoPilotException("Not Owner", HttpStatus.BAD_REQUEST, urlError, "You are not project owner");
        }
        // if status == null then check last build and update sy

        List<DeploymentAppDto> deploymentApps = deploymentAppRepository.findAllByProject(project).stream().map(DeploymentApp::toDeploymentAppDto).toList();
        return deploymentApps;
    }

    @Override
    public DeploymentAppDto getDeploymentAppById(Integer id) {
        DeploymentApp deploymentApp = deploymentAppRepository.findById(id).get();
        if (deploymentApp == null) {
            throw new AutoPilotException("Not Found", HttpStatus.BAD_REQUEST, urlError, "Deployment is not found!");
        }
        String result = HttpUtil.getLastBuildJob(deploymentApp.getJobName());
        if (result == "PENDING") {
            deploymentApp.setStatus(null);
            deploymentAppRepository.save(deploymentApp);
        } else if (result == "SUCCESS") {
            deploymentApp.setStatus(true);
            deploymentAppRepository.save(deploymentApp);
        } else {
            deploymentApp.setStatus(false);
            deploymentAppRepository.save(deploymentApp);
            //throw new AutoPilotException("Unsuccessful", HttpStatus.BAD_REQUEST, urlError, "Your Deployment is not yet success !");
        }
        return deploymentApp.toDeploymentAppDto();
    }

    public String deploymentSpring(DeploymentAppRequest request) throws IOException, InterruptedException {
        // String repoName = "https://github.com/KSGA-Autopilot/" + request.getAppName() + "-cd" + ".git";
        URL url = new URL(request.getGitSrcUrl());
        String cdRepos = url.getPath();
        String[] arrayPath = cdRepos.split("/");
        String username = arrayPath[1].toLowerCase();
        System.out.println("Username: " + username);
        String projectName = arrayPath[2].toLowerCase().substring(0, arrayPath[2].length() - 4);
        System.out.println("Project Name: " + projectName);

        String applicationName = username.toLowerCase().replaceAll("_", "").replaceAll("/", "") + "-" + projectName.toLowerCase().replaceAll("_", "").replaceAll("/", "");
        System.out.println("Application name: " + applicationName);
        cdRepos = applicationName + "-cd";
        String namespace = username.toLowerCase().replaceAll("_", "");
        String serviceName = applicationName + "-svc";
        String deploymentName = applicationName + "-deployment";
        String deploymentLabel = applicationName;
        String containerName = applicationName;
        String ingressName = applicationName + "-ingress";

        String image = "kshrdautopilot/" + applicationName;
        System.out.println("Image: " + image);
        System.out.println("cd repos: " + cdRepos);

        String jobName = cdRepos + UUID.randomUUID().toString().substring(0, 4);
        // need to verify here
        int cdResponse = GitUtil.createGitRepos(cdRepos);

        if (cdResponse != 201) {
            throw new BadRequestException("Unable to create.", "Enable to create git repository." + cdRepos);
        }
        // create job: build, test, and push image, update cd repos image
        try {
            Jenkins cli = new Jenkins();
            // create application for argocd
            GitUtil.createApplication(cdRepos, applicationName, namespace);
            // create deployment file
            GitUtil.createSpringDeployment(cdRepos, deploymentName, deploymentLabel, 2, containerName, image, request.getProjectPort());
            // create service file
            GitUtil.createSpringService(cdRepos, serviceName, deploymentLabel, request.getProjectPort(), request.getProjectPort());
            // create ingress file
            GitUtil.createIngress(cdRepos, ingressName, namespace, request.getDomain() == null || request.getDomain().isEmpty() || request.getDomain().isBlank() ? "controlplane.hanyeaktong.site" : request.getDomain(), request.getPath(), serviceName, request.getProjectPort().toString());
//            GitUtil.createArgoApp(cdRepos, appName, username);
            // create certificate for namespace
            GitUtil.createNamespaceTlsCertificate(cdRepos, request.getDomain() == null || request.getDomain().isEmpty() || request.getDomain().isBlank() ? "controlplane.hanyeaktong.site" : request.getDomain(), namespace);
            // create jenkins job
            cli.createSpringJobConfig(request.getGitSrcUrl(), image, request.getBranch(), cdRepos, jobName, namespace, request.getProjectPort().toString(), request.getBuildTool());
        } catch (Exception e) {
            e.printStackTrace();
        }
        // build job
        if (HttpUtil.buildJob(jobName) != 201) {
            throw new BadRequestException("Fail to build job", "Job have not been build successfully.");
        }


        // create cd repos
        // create job: build, test, and push image, update cd repos image
        // check if job is built successfully
        // make argo cd connect to cd repos
        // add domain and secure ssl
        // setup monitoring: server up -> send alert


        // String image = "autopilot:customer-spring-gradle.pipeline.xml:2023-12-12-12:00";


        // cli.createJobConfig(request.getGit_src_url(),request.getBuild_tool(),request.getBranch(),request.getAppName());
        // push to docker hub
        // create cd repos
        // create deployment
        // create service
        return jobName;
    }

    public String deployReactJs(DeploymentAppRequest request) throws IOException, InterruptedException {
        // create cd repos
        URL url = new URL(request.getGitSrcUrl());
        String cdRepos = url.getPath();
        String[] arrayPath = cdRepos.split("/");
        String username = arrayPath[1].toLowerCase();
        System.out.println("Username: " + username);
        String projectName = arrayPath[2].toLowerCase().substring(0, arrayPath[2].length() - 4);
        System.out.println("Project Name: " + projectName);

        String applicationName = username.toLowerCase().replaceAll("_", "").replaceAll("/", "") + "-" + projectName.toLowerCase().replaceAll("_", "").replaceAll("/", "");
        System.out.println("Application name: " + applicationName);
        cdRepos = applicationName + "-cd";
        String namespace = username.toLowerCase().replaceAll("_", "");
        String serviceName = applicationName + "-svc";
        String deploymentName = applicationName + "-deployment";
        String deploymentLabel = applicationName;
        String containerName = applicationName;
        String ingressName = applicationName + "-ingress";

        String image = "kshrdautopilot/" + applicationName;
        System.out.println("Image: " + image);
        System.out.println("cd repos: " + cdRepos);

        String jobName = cdRepos + UUID.randomUUID().toString().substring(0, 4);
        // need to verify here
        int cdResponse = GitUtil.createGitRepos(cdRepos);

        if (cdResponse != 201) {
            throw new BadRequestException("Unable to create.", "Enable to create git repository." + cdRepos);
        }
        // create job: build, test, and push image, update cd repos image
        try {
            Jenkins cli = new Jenkins();
            // create application for argocd
            GitUtil.createApplication(cdRepos, applicationName, namespace);
            // create deployment file
            GitUtil.createSpringDeployment(cdRepos, deploymentName, deploymentLabel, 2, containerName, image, request.getProjectPort());
            // create service file
            GitUtil.createSpringService(cdRepos, serviceName, deploymentLabel, request.getProjectPort(), request.getProjectPort());
            // create ingress file
            GitUtil.createIngress(cdRepos, ingressName, namespace, request.getDomain() == null || request.getDomain().isEmpty() || request.getDomain().isBlank() ? "controlplane.hanyeaktong.site" : request.getDomain(), request.getPath(), serviceName, request.getProjectPort().toString());
//            GitUtil.createArgoApp(cdRepos, appName, username);
            // create certificate for namespace
            GitUtil.createNamespaceTlsCertificate(cdRepos, request.getDomain() == null || request.getDomain().isEmpty() || request.getDomain().isBlank() ? "controlplane.hanyeaktong.site" : request.getDomain(), namespace);
            // create jenkins job
            cli.createReactJobConfig(request.getGitSrcUrl(), image, request.getBranch(), cdRepos, jobName, namespace);
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
        //DeploymentApp deploymentApp = deploymentAppRepository.findByGitSrcUrl(request.getGitSrcUrl());
//        return deploymentApp.toDeploymentAppDto();
        return jobName;
    }
}
