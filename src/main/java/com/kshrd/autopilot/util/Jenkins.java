package com.kshrd.autopilot.util;

import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.model.Build;
import com.offbytwo.jenkins.model.BuildWithDetails;
import com.offbytwo.jenkins.model.ConsoleLog;
import com.offbytwo.jenkins.model.JobWithDetails;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Jenkins {
    public void buildReactJob(String appName, String jobName, String gitUrl, String project_name) {
        //System.out.println(gitUrl);
        try {
            // store this in application.yml
            String jenkinsUrl = "http://188.166.179.13:8080/";
            String username = "kshrd";
            String token = "11ea48669bc5b21f56e116d00868fa669d";

            String apiUrl = jenkinsUrl + "/job/" + jobName + "/buildWithParameters?appname=" + appName + "&giturl=" + gitUrl;
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            String auth = username + ":" + token;
            String encodedAuth = java.util.Base64.getEncoder().encodeToString(auth.getBytes());
            connection.setRequestProperty("Authorization", "Basic " + encodedAuth);
            connection.setDoOutput(true);
            OutputStream os = connection.getOutputStream();
            os.write("".getBytes());
            os.flush();

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_CREATED) {
                System.out.println("Jenkins job triggered successfully.");
            } else {
                System.out.println("Failed to trigger Jenkins job. Response code: " + responseCode);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void createSpringJobConfig(String customerRepository, String image, String branch, String cdRepos, String jobName, String namespace,String port,String tool) {
        try {
            String jenkinsUrl = "http://188.166.179.13:8080/";
            String username = "kshrd";
            String apiToken = "11494604d60cbd9709b8b582eedd62fab3";
            String toolType = "";
            String build_tool = "gradle";
            String b_project = "";
            switch (tool) {
                case "gradle":
                    toolType = "springGradle";
                    b_project = "gradle build";
                    break;
                case "maven":
                    toolType = "springMaven";
                    b_project = "mvn package";
                    build_tool = "maven";
                    break;
            }
            File fileDocker = new File("src/main/java/com/kshrd/autopilot/util/fileConfig/" + toolType);

            Map<String, String> docker = new HashMap<>();
            docker.put("x-port", port);
            String dockerfile = FileUtil.replaceText(fileDocker, docker);
            JenkinsServer jenkins = new JenkinsServer(new URI(jenkinsUrl), username, apiToken);
            File file = new File("src/main/java/com/kshrd/autopilot/util/fileConfig/spring/spring-gradle.pipeline.xml");
            Map<String, String> replacement = new HashMap<>();
            replacement.put("toolChange", tool);
            // replacement.put("appname",project_name);
            replacement.put("fordockerfile", dockerfile);
            replacement.put("b-project", b_project);
            replacement.put("gitUrl", customerRepository);
            replacement.put("buildtool", build_tool);
            replacement.put("var-image", image);
            replacement.put("path-repository", cdRepos);
            replacement.put("var-branch", branch);
            replacement.put("argo-namespace", namespace);
            replacement.put("argo-application-yaml", "https://raw.githubusercontent.com/KSGA-Autopilot/" + cdRepos + "/main/application.yaml");
            String jobConfig = FileUtil.replaceText(file, replacement);
            //   System.out.println(jobConfig);
            //String jobName = project_name + UUID.randomUUID().toString().substring(0, 4);
            jenkins.createJob(jobName, jobConfig);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createReactJobConfig(String customerRepository, String image, String branch, String cdRepos, String jobName, String namespace) {
        try {
            String jenkinsUrl = "http://188.166.179.13:8080/";
            String username = "kshrd";
            String apiToken = "11494604d60cbd9709b8b582eedd62fab3";
            String toolType = "";
            String build_tool = "npm";
            JenkinsServer jenkins = new JenkinsServer(new URI(jenkinsUrl), username, apiToken);
            String configXML = FileUtil.readFile("src/main/java/com/kshrd/autopilot/util/fileConfig/react/react-npm.pipeline.xml");
            System.out.println(configXML);
            Map<String, String> replaceString = new HashMap<>();
            replaceString.put("var-git_src_url", customerRepository);
            replaceString.put("${GITHUB_REPO}", cdRepos);
            replaceString.put("var-image", image);
            replaceString.put("var-branch", branch);
            replaceString.put("argo-namespace", namespace);
            replaceString.put("argo-application-yaml", "https://raw.githubusercontent.com/KSGA-Autopilot/" + cdRepos + "/main/application.yaml");
            // replace string operation
            for (Map.Entry<String, String> entry : replaceString.entrySet()) {
                configXML = configXML.replace(entry.getKey(), entry.getValue());
            }
            System.out.println("Job name: " + jobName);
            System.out.println("Job config xml");
            jenkins.createJob(jobName, configXML);
            System.out.println("End job create");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void backupPostgresDatabase(Long projectId, Integer port, String databaseName) {
        try {
            String jenkinsUrl = "http://188.166.179.13:8080/";
            String username = "kshrd";
            String apiToken = "11494604d60cbd9709b8b582eedd62fab3";
            JenkinsServer jenkins = new JenkinsServer(new URI(jenkinsUrl), username, apiToken);
            String postgresDatabasePipeline = FileUtil.readFile("src/main/java/com/kshrd/autopilot/util/fileConfig/DatabaseJenkinsPipeline/postgres-backup-pipeline.xml");
            System.out.println(postgresDatabasePipeline);
            Map<String, String> replaceString = new HashMap<>();
            replaceString.put("docker-ps-name", projectId + databaseName);
            replaceString.put("database-name", databaseName);
            replaceString.put("new-database-location", projectId + databaseName);
            replaceString.put("db-name", databaseName);
            // replace string operation
            for (Map.Entry<String, String> entry : replaceString.entrySet()) {
                postgresDatabasePipeline = postgresDatabasePipeline.replace(entry.getKey(), entry.getValue());
            }
            System.out.println("Job config xml");
            jenkins.createJob(projectId+databaseName+"-backup", postgresDatabasePipeline);
            System.out.println("End job create");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String consoleBuild(String jobname) {
        String consoleLog = "";
        try {
            String jenkinsUrl = "http://188.166.179.13:8080/";
            String username = "kshrd";
            String apiToken = "11494604d60cbd9709b8b582eedd62fab3";
            JenkinsServer jenkins = new JenkinsServer(new URI(jenkinsUrl), username, apiToken);
            JobWithDetails jobWithDetails = jenkins.getJob(jobname);
            if (jobWithDetails != null) {
                int buildNumber=jobWithDetails.getLastBuild().getNumber();
                Build build = jobWithDetails.getBuildByNumber(buildNumber);
                if (build != null) {
                    BuildWithDetails buildWithDetails = build.details();
                    if (buildWithDetails != null) {
                        consoleLog = buildWithDetails.getConsoleOutputText();
                        //System.out.println(consoleLog);
                    }

                }
            }
            // System.out.println(jobWithDetails.getBuildByNumber(1).details());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return consoleLog;
    }
    public static void deleteJob(String jobName) throws URISyntaxException, IOException {
        String jenkinsUrl = "http://188.166.179.13:8080/";
        String username = "kshrd";
        String apiToken = "11494604d60cbd9709b8b582eedd62fab3";
        JenkinsServer jenkinsServer = new JenkinsServer(new URI(jenkinsUrl), username, apiToken);
        jenkinsServer.deleteJob(jobName);
    }

    public void createFlaskJobConfig(String gitSrcUrl, String image, String branch, String cdRepos, String jobName, String namespace) {
        try {
            String jenkinsUrl = "http://188.166.179.13:8080/";
            String username = "kshrd";
            String apiToken = "11494604d60cbd9709b8b582eedd62fab3";
            String toolType = "";
            String build_tool = "npm";
            JenkinsServer jenkins = new JenkinsServer(new URI(jenkinsUrl), username, apiToken);
            String configXML = FileUtil.readFile("src/main/java/com/kshrd/autopilot/util/fileConfig/python/flask-pipeline.xml");
            System.out.println(configXML);
            Map<String, String> replaceString = new HashMap<>();
            replaceString.put("var-git_src_url", gitSrcUrl);
            replaceString.put("${GITHUB_REPO}", cdRepos);
            replaceString.put("var-image", image);
            replaceString.put("var-branch", branch);
            replaceString.put("argo-namespace", namespace);
            replaceString.put("argo-application-yaml", "https://raw.githubusercontent.com/KSGA-Autopilot/" + cdRepos + "/main/application.yaml");
            // replace string operation
            for (Map.Entry<String, String> entry : replaceString.entrySet()) {
                configXML = configXML.replace(entry.getKey(), entry.getValue());
            }
            System.out.println("Job name: " + jobName);
            System.out.println("Job config xml");
            jenkins.createJob(jobName, configXML);
            System.out.println("End job create");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
