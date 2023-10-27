package com.kshrd.autopilot.util;

import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.model.Job;

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

    public void createJobConfig(String gitUrl, String tool, String branch, String project_name) {
        try {
            String jenkinsUrl = "http://188.166.179.13:8080/";
            String username = "kshrd";
            String apiToken = "112c1c4092c8db6fb4e74c976f6e5d1ace";
            String toolType="";
            switch (tool){
                case "gradle" : toolType="springGradle";
                break;
                case "mavean" : toolType="springMavean";
                break;
            }
            File fileDocker = new File("src/main/java/com/kshrd/autopilot/util/fileConfig/"+toolType);
            String dockerfile=FileUtil.replaceText(fileDocker,"appname",project_name);
            JenkinsServer jenkins = new JenkinsServer(new URI(jenkinsUrl), username, apiToken);
            String pipeline = "pipeline {\n" +
                    "    agent {\n" +
                    "        node{\n" +
                    "            label 'worker1'\n" +
                    "        }\n" +
                    "    }\n" +
                    "   tools{\n" +
                    "        "+tool+"  '"+tool+"'\n" +
                    "    }\n" +
                    "    \n" +
                    "    environment{\n" +
                    "    CURRENT_DATETIME = new Date().format(\"yyyy-MM-dd-HH-mm-ss\")\n" +
                    "    }\n" +
                    "    \n" +
                    "    stages {\n" +
                    "       \n" +
                    "        stage('Clone Repository') {\n" +
                    "            steps {\n" +
                    "                 script{\n" +
                    "                    checkout([$class: 'GitSCM', branches: [[name: '"+branch+"']], doGenerateSubmoduleConfigurations: false, extensions: [[$class: 'CleanBeforeCheckout']], userRemoteConfigs: [[url: '"+gitUrl+"']]])\n" +
                    "            }\n" +
                    "        }\n" +
                    "        }\n" +
                    "        stage('Buid Project'){\n" +
                    "            steps{\n" +
                    "                script{\n" +
                    "                    sh 'gradle build'\n" +
                    "                    echo \"Build successfully\"\n" +
                    "                }\n" +
                    "            }\n" +
                    "        }\n" +
                    "\n" +
                    "         stage('Add Dockerfile') {\n" +
                    "            steps {\n" +
                    "                script {\n" +
                    "                   def dockerfileContent = \"\"\"\n" +
                                        dockerfile+
                    "                   writeFile file: 'Dockerfile', text: dockerfileContent\n" +
                    "                }\n" +
                    "            }\n" +
                    "         }\n" +
                    "       \n" +
                    "             stage('build to docker images') {\n" +
                    "            steps {\n" +
                    "                script{\n" +
                    "        \n" +
                    "                sh 'docker build -t kshrdautopilot/autopilot:${CURRENT_DATETIME} .'\n" +
                    "                \n" +
                    "                 sh 'docker push kshrdautopilot/autopilot:${CURRENT_DATETIME}'\n" +
                    "              \n" +
                    "                    echo \"build images successfully\"\n" +
                    "                   \n" +
                    "                }\n" +
                    "              \n" +
                    "            }\n" +
                    "        }\n" +
                    "                    stage('trigger Manifest') {\n" +
                    "            steps {\n" +
                    "                script{\n" +
                    "                    build job: 'autopilot-manifest', parameters: [string(name: 'DOCKERTAG', value: evn.CURRENT_DATETIME)]\n" +
                    "                }\n" +
                    "              \n" +
                    "            }\n" +
                    "    \n" +
                    "    }\n" +
                    "}\n" +
                    "}\n";
            File file = new File("src/main/java/com/kshrd/autopilot/util/fileConfig/spring/spring");
            String jobConfig = FileUtil.replaceText(file, "replacePipeline", pipeline);
            String jobName = project_name + UUID.randomUUID().toString().substring(0, 4);
            jenkins.createJob(jobName, jobConfig);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
