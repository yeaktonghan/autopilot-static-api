package com.kshrd.autopilot.service.implementation;

import com.kshrd.autopilot.entities.DeploymentDb;
import com.kshrd.autopilot.entities.Project;
import com.kshrd.autopilot.entities.dto.DeploymentDBDto;
import com.kshrd.autopilot.entities.dto.ProjectDto;
import com.kshrd.autopilot.entities.request.DeploymentDBRequest;
import com.kshrd.autopilot.repository.DeploymentDbRepository;
import com.kshrd.autopilot.repository.ProjectRepository;
import com.kshrd.autopilot.service.DeploymentDBService;
import com.kshrd.autopilot.util.DatabaseUtil;
import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.model.Build;
import com.offbytwo.jenkins.model.BuildResult;
import com.offbytwo.jenkins.model.JobWithDetails;
import com.offbytwo.jenkins.model.QueueReference;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class DeploymentDBServiceImp implements DeploymentDBService {
    private final DeploymentDbRepository repository;
    private final ProjectRepository projectRepository;

    public DeploymentDBServiceImp(DeploymentDbRepository repository, ProjectRepository projectRepository) {
        this.repository = repository;
        this.projectRepository = projectRepository;

    }

    @Override
    public DeploymentDBDto creatDatabase(DeploymentDBRequest request) {
        Project project = projectRepository.findById(request.getProject_id()).get();
        String jenkinsUrl = "http://188.166.179.13:8080/";
        String username = "kshrd";
        String apiToken = "113a92e3b821914adb7c544899738117e9";
        String jobName = "";
        if (request.getDbType().equals("POSTGRES")) {
            jobName = "deployment-postgres";
        }

        DeploymentDb deploymentDb = new DeploymentDb();
        deploymentDb.setDbName(request.getName());
        deploymentDb.setDbPassword(request.getPassword());
        deploymentDb.setDbUsername(request.getUsername());
        deploymentDb.setProject(project);
        deploymentDb.setPort("5433");
        deploymentDb.setDbType(request.getDbType());
        deploymentDb.setIpAddress("128.199.138.228");
        deploymentDb.setCreated_at(LocalDateTime.now());
        // DatabaseUtil.createPostgres(request.getName(),request.getUsername(),request.getPassword());
        try {
            JenkinsServer jenkins = new JenkinsServer(new URI(jenkinsUrl), username, apiToken);

            JobWithDetails job = jenkins.getJob(jobName);

            // Define job parameters
            Map<String, String> params = new HashMap<>();
            params.put("USERNAME", deploymentDb.getDbUsername());
            params.put("DB_NAME", deploymentDb.getDbName());
            params.put("PORT", deploymentDb.getPort());
            params.put("DB_PASSWORD", deploymentDb.getDbPassword());
            job.build(params);
            Build build = job.getLastBuild();
           String result= String.valueOf(build.details().getResult());

            // Check the build status
            if (result=="SUCCESS") {
                repository.save(deploymentDb);
                System.out.println("Jenkins job build was successful.");
            } else {
                System.out.println("Jenkins job build failed.");
            }
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return deploymentDb.toDeploymentDBDto();
    }
}
