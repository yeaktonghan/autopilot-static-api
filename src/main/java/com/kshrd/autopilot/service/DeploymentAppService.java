package com.kshrd.autopilot.service;
import com.kshrd.autopilot.entities.dto.DeploymentAppDto;
import com.kshrd.autopilot.entities.request.DeploymentAppRequest;

import java.io.IOException;
import java.util.List;

public interface DeploymentAppService {
    DeploymentAppDto createDeploymentApp(DeploymentAppRequest request) throws IOException, InterruptedException;
    List<DeploymentAppDto>getAllDeploymentApps(Integer project_id);
}
