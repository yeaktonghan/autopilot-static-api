package com.kshrd.autopilot.service.implementation;

import com.jcraft.jsch.JSchException;
import com.kshrd.autopilot.entities.DeploymentDb;
import com.kshrd.autopilot.entities.Project;
import com.kshrd.autopilot.entities.dto.DeploymentDBDto;
import com.kshrd.autopilot.entities.request.DeploymentDBRequest;
import com.kshrd.autopilot.entities.user.User;
import com.kshrd.autopilot.exception.AutoPilotException;
import com.kshrd.autopilot.exception.BadRequestException;
import com.kshrd.autopilot.exception.ConflictException;
import com.kshrd.autopilot.repository.DeploymentDbRepository;
import com.kshrd.autopilot.repository.ProjectDetailRepository;
import com.kshrd.autopilot.repository.ProjectRepository;
import com.kshrd.autopilot.service.DeploymentDBService;
import com.kshrd.autopilot.util.HttpUtil;
import com.kshrd.autopilot.util.Jenkins;
import com.kshrd.autopilot.util.SSHUtil;
import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.model.Build;
import com.offbytwo.jenkins.model.JobWithDetails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class DeploymentDBServiceImp implements DeploymentDBService {
    private final DeploymentDbRepository repository;
    private final ProjectDetailRepository projectDetailRepository;
    private final ProjectRepository projectRepository;
    @Value("${error.url}")
    String errUrl;

    public DeploymentDBServiceImp(DeploymentDbRepository repository, ProjectDetailRepository projectDetailRepository, ProjectRepository projectRepository) {
        this.repository = repository;
        this.projectDetailRepository = projectDetailRepository;
        this.projectRepository = projectRepository;

    }

    @Override
    public DeploymentDBDto creatDatabase(DeploymentDBRequest request) {
        Project project = projectRepository.findById(request.getProject_id()).get();
        String jenkinsUrl = "https://jenkins.hanyeaktong.site/";
        String username = "kshrd";
        String apiToken = "113a92e3b821914adb7c544899738117e9";
        String jobName = "";
        if (request.getDbType().equals("POSTGRES")) {
            jobName = "deployment-postgres";
        }
        // verify string
        // if database type is not correct
        switch (request.getDbType().toLowerCase().trim()) {
            case "postgres" -> {
                request.setDbType(request.getDbType().toLowerCase().trim());
                request.setUsername("postgres");
            }
            case "mysql" -> {
                request.setDbType(request.getDbType().toLowerCase().trim());
                request.setUsername("root");
            }
            default -> throw new UnsupportedOperationException("Database type not supported");
        }


        DeploymentDb deploymentDb = new DeploymentDb();
        deploymentDb.setDbName(request.getDbName());
        deploymentDb.setDbPassword(request.getPassword());
        deploymentDb.setDbUsername(request.getUsername());
        deploymentDb.setProject(project);
        deploymentDb.setPort("5433");
        deploymentDb.setDbType(request.getDbType());
        deploymentDb.setIpAddress("178.128.111.115");
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
            String result = String.valueOf(build.details().getResult());

            // Check the build status
            if (Objects.equals(result, "SUCCESS")) {
                repository.save(deploymentDb);
                System.out.println("Jenkins job build was successful.");
            } else {
                System.out.println("Jenkins job build failed.");
            }
        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
        return deploymentDb.toDeploymentDBDto();
    }

    // real method
    @Override
    public DeploymentDBDto deployDatabase(DeploymentDBRequest request) throws JSchException, InterruptedException, IOException {

        // verify not duplicate
        if (repository.findDeploymentDbByDbNameAndProject(request.getDbName(), projectRepository.findById(request.getProject_id()).get()) != null) {
            throw new ConflictException("Database name " + request.getDbName() + " already exist in this project.", "Please choose different name for database");
        }
        // verify input
        if (!request.getDbName().matches("^[a-zA-Z0-9]+$")) {
            throw new BadRequestException("Database name only accept numbers and letters.", "Special character(s) are not allowed.");
        }

        // check db port
        Integer lastPort = repository.findLastPort(request.getProject_id());
        if (lastPort == null) {
            lastPort = 5433;
        } else {
            lastPort++;
        }

        // setup for deployment
        String sshRequest = "";
        request.setDbType(request.getDbType().toLowerCase().trim());
        String databaseImage;
        String databasePath;
        String databasePort;
        switch (request.getDbType()) {
            case "postgres" -> {
                databaseImage = "postgres:15-alpine";
                databasePath = "/var/lib/postgresql/data";
                databasePort = "5432";
                request.setUsername("postgres");
                sshRequest = "docker run -d -p " + lastPort + ":" + databasePort + " --restart always --name=" + request.getProject_id().toString() + request.getDbName() + " -e POSTGRES_DB=" + request.getDbName() + " -e POSTGRES_USER=" + request.getUsername() + " -e POSTGRES_PASSWORD=" + request.getPassword() + " -v " + request.getProject_id() + request.getDbName() + ":" + databasePath + " " + databaseImage;
            }
            case "mysql" -> {
                request.setUsername("root");
            }
            default ->
                    throw new BadRequestException("Database type not supported.", "Supported database are 'postgres', and 'mysql'");
        }

        // ssh and create db
        SSHUtil.sshExecCommand(sshRequest);

        // create jenkins job to backup database
        try {
            Jenkins jenkins = new Jenkins();
            jenkins.backupPostgresDatabase(request.getProject_id(), lastPort, request.getDbName());
        } catch (Exception e) {
            throw new RuntimeException();
        }
        String jobName = request.getProject_id() + request.getDbName() + "-backup";
        if (HttpUtil.buildJob(jobName) != 201) {
            throw new BadRequestException("Fail to build job", "Job have not been build successfully.");
        }

        // save to db
        DeploymentDb deploymentDb = new DeploymentDb();
        deploymentDb.setCreated_at(LocalDateTime.now());
        deploymentDb.setPort(lastPort.toString());
        deploymentDb.setDbType(request.getDbType());
        deploymentDb.setDbUsername(request.getUsername());
        deploymentDb.setDbName(request.getDbName());
        deploymentDb.setDbPassword(request.getPassword());
        deploymentDb.setProject(projectRepository.findById(request.getProject_id()).get());
        deploymentDb.setIpAddress("178.128.111.115");
        repository.save(deploymentDb);
        return repository.findDeploymentDbByPort(lastPort.toString()).toDeploymentDBDto();
    }

    @Override
    public List<DeploymentDBDto> getDeploymentDatabaseByProjectId(Long project_id) {
        Optional<Project> project = projectRepository.findById(project_id);
        //System.out.println(project);
        if (!project.isPresent()) {
            throw new AutoPilotException("Not found", HttpStatus.NOT_FOUND, errUrl, "Project id is not found!");
        }
        List<DeploymentDb> deploymentDbs = repository.findAllByProject(project.get());
        List<DeploymentDBDto> deploymentDBDtoList = deploymentDbs.stream().map(deploymentDb ->
                deploymentDb.toDeploymentDBDto()
        ).toList();

        // return  null;
        return deploymentDBDtoList;
    }

    @Override
    public String deleteDatabaseByDatabaseId(Long databaseId, Long projectId, Boolean deleteBackup) throws JSchException, InterruptedException, URISyntaxException, IOException {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long currentUserId = user.getId();
        if (!(projectDetailRepository.existsByUserIdAndProjectId(currentUserId, projectId))) {
            throw new BadRequestException("Project not found.", "Incorrect project for this user.");
        } else if (!(repository.existsByIdAndProjectId(databaseId, projectId))) {
            throw new BadRequestException("Database not found.", "This database does not belong to this project.");
        }
        DeploymentDb deploymentDb = repository.findById(databaseId.intValue()).get();

        // setup for ssh
        String request = null;
        switch (deploymentDb.getDbType()) {
            case "postgres" -> {
                request = "docker rm " + deploymentDb.getProject().getId().toString() + deploymentDb.getDbName() + " -f";
            }
//            case "mysql" -> {
//                request.setUsername("root");
//            }
            default ->
                    throw new BadRequestException("Database type not supported.", "Supported database are 'postgres', and 'mysql'");
        }
        // delete docker ps via ssh
        SSHUtil.sshExecCommand(request);

        // delete backup if yes         // remove from data
        if (deleteBackup) {
            SSHUtil.sshExecCommand("rm -rf /root/sftp/" + projectId + deploymentDb.getDbName());
            repository.deleteById(databaseId.intValue());
        } else {
            repository.disableDatabase(databaseId);
        }
        // delete job
        Jenkins.deleteJob(deploymentDb.getProject().getId() + deploymentDb.getDbName() + "-backup");

        return "Database waa deleted successfully.";
    }


}
