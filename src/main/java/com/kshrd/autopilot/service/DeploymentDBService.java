package com.kshrd.autopilot.service;

import com.jcraft.jsch.JSchException;
import com.kshrd.autopilot.entities.dto.DeploymentDBDto;
import com.kshrd.autopilot.entities.request.DeploymentDBRequest;

public interface DeploymentDBService {
 DeploymentDBDto creatDatabase(DeploymentDBRequest request);

 DeploymentDBDto deployDatabase(DeploymentDBRequest request) throws JSchException, InterruptedException;
}
