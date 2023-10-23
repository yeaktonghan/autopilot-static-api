package com.kshrd.autopilot.service.implementation;

import com.kshrd.autopilot.entities.DeploymentDb;
import com.kshrd.autopilot.entities.dto.DeploymentAppDto;
import com.kshrd.autopilot.entities.request.DeploymentDBRequest;
import com.kshrd.autopilot.repository.DeploymentDbRepository;
import com.kshrd.autopilot.service.DeploymentDBService;
import com.kshrd.autopilot.util.DatabaseUtil;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class DeploymentDBServiceImp implements DeploymentDBService {
    private final DeploymentDbRepository repository;

    public DeploymentDBServiceImp(DeploymentDbRepository repository) {
        this.repository = repository;
    }

    @Override
    public DeploymentAppDto creatDatabase(DeploymentDBRequest request) {
        DeploymentDb deploymentDb=new DeploymentDb();
        deploymentDb.setDbName(request.getName());
        deploymentDb.setDbPassword(request.getPassword());
        deploymentDb.setDbUsername(request.getUsername());
        deploymentDb.setPort("5432");
        deploymentDb.setIpAddress("128.199.138.228");
        deploymentDb.setCreated_at(LocalDateTime.now());
        DatabaseUtil.createPostgres(request.getName(),request.getUsername(),request.getPassword());
        return null;
    }
}
