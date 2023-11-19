package com.kshrd.autopilot.service;

import com.jcraft.jsch.JSchException;
import com.kshrd.autopilot.entities.dto.DeploymentDBDto;
import com.kshrd.autopilot.entities.request.DeploymentDBRequest;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public interface DeploymentDBService {
 DeploymentDBDto creatDatabase(DeploymentDBRequest request);

 DeploymentDBDto deployDatabase(DeploymentDBRequest request) throws JSchException, InterruptedException, IOException;

 List<DeploymentDBDto>getDeploymentDatabaseByProjectId(Long project_id);

 String deleteDatabaseByDatabaseId(Long databaseId, Long projectId, Boolean deleteBackup) throws JSchException, InterruptedException, URISyntaxException, IOException;
}
