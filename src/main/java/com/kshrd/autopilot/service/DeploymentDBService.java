package com.kshrd.autopilot.service;

import com.jcraft.jsch.JSchException;
import com.kshrd.autopilot.entities.dto.DeploymentDBDto;
import com.kshrd.autopilot.entities.request.DeploymentDBRequest;

import java.io.IOException;

public interface DeploymentDBService {
 DeploymentDBDto creatDatabase(DeploymentDBRequest request);

 DeploymentDBDto deployDatabase(DeploymentDBRequest request) throws JSchException, InterruptedException, IOException;
}
