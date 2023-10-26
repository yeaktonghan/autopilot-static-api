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
    //    public void createJob(String jobName,String pipelineConfig) {
//        try {
//            String jenkinsUrl = "http://188.166.179.13:8080/";
//            String jobConfigXml = "<project><builders/><publishers/></project>";
//            //String pipelineConfig = "node {\n    echo 'Hello, Jenkins Pipeline!'\n}";
//            String autoGen= UUID.randomUUID().toString();
//
//            URL url = new URL(jenkinsUrl + "createItem?name=" + jobName+autoGen);
//            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//            connection.setRequestMethod("POST");
//            String authHeaderValue = "Basic " + Base64.getEncoder().encodeToString("kshrd:11fdcca00117763b5dd3e14d3ca2b9dfb9".getBytes());
//            connection.setRequestProperty("Authorization", authHeaderValue);
//            connection.setRequestProperty("Content-Type", "application/xml");
//            connection.setDoOutput(true);
//            try (OutputStream os = connection.getOutputStream()) {
//                String pipelineXml = String.format("<flow-definition>%s</flow-definition>", pipelineConfig);
//                os.write(pipelineXml.getBytes("utf-8"));
//            }
//            int responseCode = connection.getResponseCode();
//            System.out.println(responseCode == HttpURLConnection.HTTP_OK ? "Jenkins job created" : "Failed to create Jenkins job");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
    public void buildReactJob(String appName, String jobName, String gitUrl, String project_name) {
        //System.out.println(gitUrl);
        try {
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
                    "                  \n" +
                    "                      \n" +
                    "FROM openjdk:17\n" +
                    "\n" +
                    "\n" +
                    "WORKDIR /app\n" +
                    "\n" +
                    "COPY build/libs/"+project_name+"-0.0.1-SNAPSHOT.jar "+project_name+"-0.0.1-SNAPSHOT.jar\n" +
                    "\n" +
                    "CMD [\"java\", \"-jar\", \""+project_name+"-0.0.1-SNAPSHOT.jar\"]\n" +
                    "\n" +
                    "                                            \"\"\"\n" +
                    "                    \n" +
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
            System.out.println(jobConfig);
            String jobName = project_name + UUID.randomUUID().toString().substring(0, 4);
            jenkins.createJob(jobName, jobConfig);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
