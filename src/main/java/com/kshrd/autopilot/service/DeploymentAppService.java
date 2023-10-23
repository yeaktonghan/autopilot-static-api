package com.kshrd.autopilot.service;
import com.kshrd.autopilot.entities.dto.DeploymentAppDto;
import com.kshrd.autopilot.entities.request.DeploymentAppRequest;
import java.util.List;

public interface DeploymentAppService {
    DeploymentAppDto createDeploymentApp(DeploymentAppRequest request) ;
    List<DeploymentAppDto>getAllDeploymentApps(Integer project_id);
}
