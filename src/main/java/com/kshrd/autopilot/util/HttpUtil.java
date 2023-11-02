package com.kshrd.autopilot.util;

import com.kshrd.autopilot.exception.BadRequestException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUtil {
    public static int buildJob(String jobName) throws IOException {
            String apiUrl = "http://188.166.179.13:8080/job/"+jobName+"/build";
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
        String apiUrl = "http://188.166.179.13:8080/job/"+jobName+"/build";
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
}
