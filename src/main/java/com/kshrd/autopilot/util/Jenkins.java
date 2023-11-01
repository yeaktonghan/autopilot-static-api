package com.kshrd.autopilot.util;

import com.offbytwo.jenkins.JenkinsServer;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
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
            String token = "112c1c4092c8db6fb4e74c976f6e5d1ace";

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

    public void createSpringJobConfig(String gitUrl, String repoPath, String tool, String branch, String project_name) {
        try {
            String jenkinsUrl = "http://188.166.179.13:8080/";
            String username = "kshrd";
            String apiToken = "112de5f0b04bb2ad66d7f233a445f6b0fd";
            String toolType="";
            String build_tool="gradle";
            switch (tool){
                case "gradle" : toolType="springGradle";
                break;
                case "mavean" : toolType="springMavean"; build_tool="mvn";
                break;
            }
            File fileDocker = new File("src/main/java/com/kshrd/autopilot/util/fileConfig/"+toolType);

            Map<String,String> docker=new HashMap<>();
            docker.put("appname",project_name);
            String dockerfile=FileUtil.replaceText(fileDocker,docker);
            JenkinsServer jenkins = new JenkinsServer(new URI(jenkinsUrl), username, apiToken);
            File file = new File("src/main/java/com/kshrd/autopilot/util/fileConfig/spring/spring");
            Map<String,String> replacement=new HashMap<>();
            replacement.put("toolChange",tool);
            replacement.put("appname",project_name);
            replacement.put("fordockerfile",dockerfile);
            replacement.put("gitUrl",gitUrl);
            replacement.put("buildtool",build_tool);
            replacement.put("path-repository",repoPath);
            String jobConfig = FileUtil.replaceText(file, replacement);
            System.out.println(jobConfig);
            String jobName = project_name + UUID.randomUUID().toString().substring(0, 4);
            jenkins.createJob(jobName, jobConfig);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createReactJobConfig(String gitUrl, String repoPath, String tool, String branch, String project_name) {
        try {
            String jenkinsUrl = "http://188.166.179.13:8080/";
            String username = "kshrd";
            String apiToken = "112de5f0b04bb2ad66d7f233a445f6b0fd";
            String toolType="";
            String build_tool="gradle";
            switch (tool){
                case "gradle" : toolType="springGradle";
                    break;
                case "mavean" : toolType="springMavean"; build_tool="mvn";
                    break;
            }
            File fileDocker = new File("src/main/java/com/kshrd/autopilot/util/fileConfig/"+toolType);

            Map<String,String> docker=new HashMap<>();
            docker.put("appname",project_name);
            String dockerfile=FileUtil.replaceText(fileDocker,docker);
            JenkinsServer jenkins = new JenkinsServer(new URI(jenkinsUrl), username, apiToken);
            File file = new File("src/main/java/com/kshrd/autopilot/util/fileConfig/spring/spring");
            Map<String,String> replacement=new HashMap<>();
            replacement.put("toolChange",tool);
            replacement.put("appname",project_name);
            replacement.put("fordockerfile",dockerfile);
            replacement.put("gitUrl",gitUrl);
            replacement.put("buildtool",build_tool);
            replacement.put("path-repository",repoPath);
            String jobConfig = FileUtil.replaceText(file, replacement);
            System.out.println(jobConfig);
            String jobName = project_name + UUID.randomUUID().toString().substring(0, 4);
            jenkins.createJob(jobName, jobConfig);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
