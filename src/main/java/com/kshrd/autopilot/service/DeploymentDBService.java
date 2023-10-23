package com.kshrd.autopilot.service;

import com.kshrd.autopilot.entities.dto.DeploymentAppDto;
import com.kshrd.autopilot.entities.request.DeploymentDBRequest;

public interface DeploymentDBService {
 DeploymentAppDto creatDatabase(DeploymentDBRequest request);
}
