package com.kshrd.autopilot.controller;

import com.kshrd.autopilot.util.GitUtil;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/file/test")
@SecurityRequirement(name = "auth")
public class TestController {
    @PostMapping("/create")
    public Integer createGit(String name) throws IOException, InterruptedException {
        return GitUtil.createGitRepos(name);
    }

    @PostMapping("/deployment")
    public Integer test(String reposName) throws IOException, InterruptedException {
        return GitUtil.createSpringDeployment(reposName, "d1", "d2", 2, "d3", "d4", 1234);
    }

//    @PostMapping("/service")
//    public Integer createService(String reposName) throws IOException, InterruptedException {
//        return GitUtil.createSpringService(reposName, "spring-gradle.pipeline.xml-deployment", "d2", 30100, 8080, 8080);
//    }
}
