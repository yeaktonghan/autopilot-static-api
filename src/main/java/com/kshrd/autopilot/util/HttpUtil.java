package com.kshrd.autopilot.util;

import com.kshrd.autopilot.exception.BadRequestException;
import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.model.Build;
import com.offbytwo.jenkins.model.BuildResult;
import com.offbytwo.jenkins.model.JobWithDetails;

import javax.xml.transform.Result;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class HttpUtil {
    public static int buildJob(String jobName) throws IOException {
        String apiUrl = "http://188.166.179.13:8080/job/" + jobName + "/build";
        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setRequestProperty("Authorization", "Basic a3NocmQ6MTFlYTQ4NjY5YmM1YjIxZjU2ZTExNmQwMDg2OGZhNjY5ZA==");
        connection.setRequestProperty("Cookie", "JSESSIONID.45cf3bd0=node01ti5nrexdix6nabl0dtxyof2c426.node0");
        String requestBody = "mode=org.jenkinsci.plugins.workflow.job.WorkflowJob";
        connection.setDoOutput(true);
        connection.getOutputStream().write(requestBody.getBytes());

        // send
        int responseCode = connection.getResponseCode();
        if (responseCode != 201) {
            throw new BadRequestException("Fail to build job", "Job have not been build successfully.");
        }
        connection.disconnect();
        return 201;
    }

    public static int getLastBuildStatus(String jobName) throws IOException {
        String apiUrl = "http://188.166.179.13:8080/job/" + jobName + "/lastBuild/api/json";
        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setRequestProperty("Authorization", "Basic a3NocmQ6MTFlYTQ4NjY5YmM1YjIxZjU2ZTExNmQwMDg2OGZhNjY5ZA==");
        connection.setRequestProperty("Cookie", "JSESSIONID.45cf3bd0=node01ti5nrexdix6nabl0dtxyof2c426.node0");
        String requestBody = "mode=org.jenkinsci.plugins.workflow.job.WorkflowJob";
        connection.setDoOutput(true);
        connection.getOutputStream().write(requestBody.getBytes());

        // send
        int responseCode = connection.getResponseCode();
        if (responseCode != 201) {
            throw new BadRequestException("Fail to build job", "Job have not been build successfully.");
        }
        connection.disconnect();
        return 201;
    }

    public static String getLastBuildJob(String jobName) {
        String jenkinsUrl = "http://188.166.179.13:8080/";
        String username = "kshrd";
        String apiToken = "113a92e3b821914adb7c544899738117e9";
        String result = "";
        try {
            JenkinsServer jenkins = new JenkinsServer(new URI(jenkinsUrl), username, apiToken);
            JobWithDetails job = jenkins.getJob(jobName);

             if (job.isBuildable()||job.isInQueue()) {
                result = "PENDING";
            }
            Build build = job.getLastBuild();
            BuildResult rs=build.details().getResult();
            if (rs==BuildResult.SUCCESS) {
                result = "SUCCESS";
            } else if (rs==BuildResult.FAILURE) {
                result = "FAILURE";
            }

        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("message" + result);
        return result;
    }


}
