package com.kshrd.autopilot.util;
import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.model.Job;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

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
    public void buildReactJob(String appName, String jobName, String gitUrl) {
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

    public void createJobConfig(String gitUrl) {
        try {

            String jenkinsUrl = "http://188.166.179.13:8080/";
            String jobName = "test-jenkins";
            String username = "kshrd";
            String apiToken = "112c1c4092c8db6fb4e74c976f6e5d1ace";

            JenkinsServer jenkins = new JenkinsServer(new URI(jenkinsUrl), username, apiToken);
             String dockerfile=FileUtil.readFile("src/main/java/com/kshrd/autopilot/util/dockerfile/react-npm");
            // Create a new Jenkins job
//            String pipelineScript = "pipeline {\n" +
//                    "    agent any\n" +
//                    "    stages {\n" +
//                    "        stage('Hello') {\n" +
//                    "            steps {\n" +
//                    "                echo 'Hello, Jenkins!'\n" +
//                    "            }\n" +
//                    "        }\n" +
//                    "    }\n" +
//                    "}\n";
            String jobConfig = "<?xml version='1.0' encoding='UTF-8'?>\n" +
                    "<project>\n" +
                    "  <actions/>\n" +
                    "  <description>Build for admin_add_number_range</description>\n" +
                    "  <logRotator class=\"hudson.tasks.LogRotator\">\n" +
                    "    <daysToKeep>-1</daysToKeep>\n" +
                    "    <numToKeep>3</numToKeep>\n" +
                    "    <artifactDaysToKeep>-1</artifactDaysToKeep>\n" +
                    "    <artifactNumToKeep>-1</artifactNumToKeep>\n" +
                    "  </logRotator>\n" +
                    "  <keepDependencies>false</keepDependencies>\n" +
                    "  <properties>\n" +
                    "    <hudson.plugins.disk__usage.DiskUsageProperty plugin=\"disk-usage@0.28\"/>\n" +
                    "  </properties>\n" +
                    "  <scm class=\"hudson.plugins.git.GitSCM\" plugin=\"git@2.3.5\">\n" +
                    "    <configVersion>2</configVersion>\n" +
                    "    <userRemoteConfigs>\n" +
                    "      <hudson.plugins.git.UserRemoteConfig>\n" +
                    "        <name>origin</name>\n" +
                    "        <url>"+gitUrl+"</url>\n" +
                    "      </hudson.plugins.git.UserRemoteConfig>\n" +
                    "    </userRemoteConfigs>\n" +
                    "    <branches>\n" +
                    "      <hudson.plugins.git.BranchSpec>\n" +
                    "        <name>*/main</name>\n" +
                    "      </hudson.plugins.git.BranchSpec>\n" +
                    "    </branches>\n" +
                    "    <doGenerateSubmoduleConfigurations>false</doGenerateSubmoduleConfigurations>\n" +
                    "    <browser class=\"hudson.plugins.git.browser.Stash\">\n" +
                    "      <url>https://example.com.au/eg</url>\n" +
                    "    </browser>\n" +
                    "    <submoduleCfg class=\"list\"/>\n" +
                    "    <extensions>\n" +
                    "      <hudson.plugins.git.extensions.impl.LocalBranch>\n" +
                    "        <localBranch>admin_add_number_range</localBranch>\n" +
                    "      </hudson.plugins.git.extensions.impl.LocalBranch>\n" +
                    "    </extensions>\n" +
                    "  </scm>\n" +
                    "  <assignedNode>slave</assignedNode>\n" +
                    "  <canRoam>false</canRoam>\n" +
                    "  <disabled>false</disabled>\n" +
                    "  <blockBuildWhenDownstreamBuilding>false</blockBuildWhenDownstreamBuilding>\n" +
                    "  <blockBuildWhenUpstreamBuilding>false</blockBuildWhenUpstreamBuilding>\n" +
                    "  <jdk>(Default)</jdk>\n" +
                    "  <triggers>\n" +
                    "    <hudson.triggers.SCMTrigger>\n" +
                    "      <spec>@hourly</spec>\n" +
                    "      <ignorePostCommitHooks>false</ignorePostCommitHooks>\n" +
                    "    </hudson.triggers.SCMTrigger>\n" +
                    "  </triggers>\n" +
                    "  <concurrentBuild>false</concurrentBuild>\n" +
                    "  <builders>\n" +
                    "    <hudson.tasks.Shell>\n" +
                    "      <command>#!/bin/bash --login\n" +
                    "# the &quot;--login&quot; means that Bash will reload the .bashrc profile\n" +
                    " \n" +
                    "# Switch node version using nvm.\n" +
                    "nvm use 0.12\n" +
                    " \n" +
                    "# Run everything, including updating deps.\n" +
                    "npm run ci</command>\n" +
                    "    </hudson.tasks.Shell>\n" +
                    "  </builders>\n" +
                    "  <publishers>\n" +
                    "    <org.jenkinsci.plugins.stashNotifier.StashNotifier plugin=\"stashNotifier@1.8\">\n" +
                    "      <stashServerBaseUrl></stashServerBaseUrl>\n" +
                    "      <stashUserName></stashUserName>\n" +
                    "      <stashUserPassword>Q7WMbss05lWqRXm260a==</stashUserPassword>\n" +
                    "      <ignoreUnverifiedSSLPeer>false</ignoreUnverifiedSSLPeer>\n" +
                    "      <commitSha1></commitSha1>\n" +
                    "      <includeBuildNumberInKey>false</includeBuildNumberInKey>\n" +
                    "    </org.jenkinsci.plugins.stashNotifier.StashNotifier>\n" +
                    "  </publishers>\n" +
                    "  <buildWrappers>\n" +
                    "    <hudson.plugins.build__timeout.BuildTimeoutWrapper plugin=\"build-timeout@1.16\">\n" +
                    "      <strategy class=\"hudson.plugins.build_timeout.impl.AbsoluteTimeOutStrategy\">\n" +
                    "        <timeoutMinutes>25</timeoutMinutes>\n" +
                    "      </strategy>\n" +
                    "      <operationList>\n" +
                    "        <hudson.plugins.build__timeout.operations.FailOperation/>\n" +
                    "      </operationList>\n" +
                    "    </hudson.plugins.build__timeout.BuildTimeoutWrapper>\n" +
                    "    <hudson.plugins.ansicolor.AnsiColorBuildWrapper plugin=\"ansicolor@0.4.1\">\n" +
                    "      <colorMapName>xterm</colorMapName>\n" +
                    "    </hudson.plugins.ansicolor.AnsiColorBuildWrapper>\n" +
                    "    <EnvInjectBuildWrapper plugin=\"envinject@1.91.1\">\n" +
                    "      <info>\n" +
                    "        <loadFilesFromMaster>false</loadFilesFromMaster>\n" +
                    "      </info>\n" +
                    "    </EnvInjectBuildWrapper>\n" +
                    "  </buildWrappers>\n" +
                    "</project>";
         jenkins.createJob(jobName,jobConfig);
        }catch (Exception e){
            e.printStackTrace();
    }
    }
}
