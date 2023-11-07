package com.kshrd.autopilot.util;


import com.kshrd.autopilot.exception.NotFoundException;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class GitUtil {
    public void gitCommand(String cmd) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.command("bash", "-c", cmd);
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void createRepository(String reposName) {
        try {
            String apiUrl = "https://api.github.com/user/repos";
            String token = "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";
            String owner = "KSGA-Autopilot";
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "token " + token);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            String jsonPayload = "{\"name\":\"" + reposName + "\",\"private\":false}";
            try (OutputStream os = connection.getOutputStream();
                 OutputStreamWriter osw = new OutputStreamWriter(os, StandardCharsets.UTF_8)) {
                osw.write(jsonPayload);
            }

            connection.connect();
            int responseCode = connection.getResponseCode();
            if (responseCode == 201) {
                System.out.println("GitHub repository created successfully.");
            } else {
                System.out.println("Error creating GitHub repository. Response code: " + responseCode);
            }

            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int createGitRepos(String name) throws IOException, InterruptedException {
        if (GitUtil.checkGitReposExist(name) == 200) {
            GitUtil.deleteGitReposExist(name);
        }
        HttpClient httpClient = HttpClient.newBuilder().build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.github.com/user/repos"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer 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")
                .POST(HttpRequest.BodyPublishers.ofString("{ \"name\": \"" + name + "\",  \"private\": false }"))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return response.statusCode();
    }

    public static int createSpringDeployment(String reposName, String deploymentName, String label, int replicaCount, String containerName, String image, int port) throws IOException, InterruptedException {
        if (GitUtil.checkGitReposExist(reposName) != 200) {
            throw new NotFoundException("Repository not found.", "This repository does not exist.");
        }
        // find template deployment file
        String deployment = FileUtil.readFile("src/main/java/com/kshrd/autopilot/util/fileConfig/spring-gradle.pipeline.xml/spring-gradle.pipeline.xml-deployment.yaml");
        // List spring-gradle.pipeline.xml to replace on the sameple file
        Map<String, String> replaceString = new HashMap<>();
        replaceString.put("d-name", deploymentName);
        replaceString.put("d-label", label);
        replaceString.put("d-replicas", String.valueOf(replicaCount));
        replaceString.put("d-container-name", containerName);
        replaceString.put("d-image", image);
        replaceString.put("d-port", String.valueOf(port));
        // replace string operation
        for (Map.Entry<String, String> entry : replaceString.entrySet()) {
            deployment = deployment.replace(entry.getKey(), entry.getValue());
        }
        // encode string to base64 for github api
        String encodedDeployment = Base64.getEncoder().encodeToString(deployment.getBytes());
        System.out.println(encodedDeployment);
        // build http request client
        HttpClient httpClient = HttpClient.newBuilder().build();
        String gitEndpoint = "https://api.github.com/repos/KSGA-Autopilot/" + reposName + "/contents/app/deployment.yaml";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(gitEndpoint))
                .header("Content-Type", "application/json")
                .header("Accept", "application/vnd.github+json")
                .header("Authorization", "Bearer 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")
                .PUT(HttpRequest.BodyPublishers.ofString("{ \"message\": \"create deployment.yaml\",  \"content\": \"" + encodedDeployment + "\" }"))
                .build();
        // send http request
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return response.statusCode();
    }

    public static int createSpringService(String reposName, String serviceName, String deploymentLabel, int targetPort, int port) throws IOException, InterruptedException {
        if (GitUtil.checkGitReposExist(reposName) != 200) {
            throw new NotFoundException("Repository not found.", "This repository does not exist.");
        }
        // find template deployment file
        String serviceYamlFile = FileUtil.readFile("src/main/java/com/kshrd/autopilot/util/fileConfig/spring-gradle.pipeline.xml/spring-gradle.pipeline.xml-service.yaml");
        System.out.println(serviceYamlFile);
        // List spring-gradle.pipeline.xml to replace on the sameple file
        Map<String, String> replaceString = new HashMap<>();
        replaceString.put("s-name", serviceName);
        replaceString.put("d-label", deploymentLabel);
        replaceString.put("s-target-port", String.valueOf(targetPort));
        replaceString.put("d-port", String.valueOf(port));
        // replace string operation
        for (Map.Entry<String, String> entry : replaceString.entrySet()) {
            serviceYamlFile = serviceYamlFile.replace(entry.getKey(), entry.getValue());
        }
        System.out.println(serviceYamlFile);
        // encode string to base64 for github api
        String encodedServiceYamlFile = Base64.getEncoder().encodeToString(serviceYamlFile.getBytes());
        System.out.println(encodedServiceYamlFile);
        // build http request client
        HttpClient httpClient = HttpClient.newBuilder().build();
        String gitEndpoint = "https://api.github.com/repos/KSGA-Autopilot/" + reposName + "/contents/app/service.yaml";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(gitEndpoint))
                .header("Content-Type", "application/json")
                .header("Accept", "application/vnd.github+json")
                .header("Authorization", "Bearer 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")
                .PUT(HttpRequest.BodyPublishers.ofString("{ \"message\": \"create service.yaml\",  \"content\": \"" + encodedServiceYamlFile + "\" }"))
                .build();
        // send http request
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return response.statusCode();
    }

    public static int createApplication(String reposName, String appName, String nameSpace) throws IOException, InterruptedException {
        if (GitUtil.checkGitReposExist(reposName) != 200) {
            throw new NotFoundException("Repository not found.", "This repository does not exist.");
        }
        // find template deployment file
        String applicationYamlFile = FileUtil.readFile("src/main/java/com/kshrd/autopilot/util/fileConfig/spring-gradle.pipeline.xml/application.yaml");
        System.out.println(applicationYamlFile);
        // List spring-gradle.pipeline.xml to replace on the sameple file
        Map<String, String> replaceString = new HashMap<>();
        replaceString.put("app-name", appName.toLowerCase());
        replaceString.put("app-repos", "https://github.com/KSGA-Autopilot/"+reposName);
        replaceString.put("app-namespace", nameSpace);
        // replace string operation
        for (Map.Entry<String, String> entry : replaceString.entrySet()) {
            applicationYamlFile = applicationYamlFile.replace(entry.getKey(), entry.getValue());
        }
        System.out.println(applicationYamlFile);
        // encode string to base64 for github api
        String encodedApplicationYamlFile = Base64.getEncoder().encodeToString(applicationYamlFile.getBytes());
        System.out.println(encodedApplicationYamlFile);
        // build http request client
        HttpClient httpClient = HttpClient.newBuilder().build();
        String gitEndpoint = "https://api.github.com/repos/KSGA-Autopilot/" + reposName + "/contents/application.yaml";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(gitEndpoint))
                .header("Content-Type", "application/json")
                .header("Accept", "application/vnd.github+json")
                .header("Authorization", "Bearer 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")
                .PUT(HttpRequest.BodyPublishers.ofString("{ \"message\": \"create application.yaml\",  \"content\": \"" + encodedApplicationYamlFile + "\" }"))
                .build();
        // send http request
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return response.statusCode();
    }

    public static int createIngress(String reposName, String ingressName, String nameSpace, String domainName, String path, String serviceName, String port) throws IOException, InterruptedException {
        if (GitUtil.checkGitReposExist(reposName) != 200) {
            throw new NotFoundException("Repository not found.", "This repository does not exist.");
        }
        // find template deployment file
        String ingressYamlFile = FileUtil.readFile("src/main/java/com/kshrd/autopilot/util/fileConfig/spring-gradle.pipeline.xml/ingress.yaml");
        System.out.println(ingressYamlFile);
        // List spring-gradle.pipeline.xml to replace on the sameple file
        Map<String, String> replaceString = new HashMap<>();
        replaceString.put("i-name", ingressName);
        replaceString.put("cert-namespace", nameSpace);
        replaceString.put("i-domain", domainName);
        replaceString.put("i-path", path);
        replaceString.put("service-name", serviceName);
        replaceString.put("service-port", port);
        // replace string operation
        for (Map.Entry<String, String> entry : replaceString.entrySet()) {
            ingressYamlFile = ingressYamlFile.replace(entry.getKey(), entry.getValue());
        }
        System.out.println(ingressYamlFile);
        // encode string to base64 for github api
        String encodedIngressYamlFile = Base64.getEncoder().encodeToString(ingressYamlFile.getBytes());
        System.out.println(encodedIngressYamlFile);
        // build http request client
        HttpClient httpClient = HttpClient.newBuilder().build();
        String gitEndpoint = "https://api.github.com/repos/KSGA-Autopilot/" + reposName + "/contents/app/ingress.yaml";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(gitEndpoint))
                .header("Content-Type", "application/json")
                .header("Accept", "application/vnd.github+json")
                .header("Authorization", "Bearer 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")
                .PUT(HttpRequest.BodyPublishers.ofString("{ \"message\": \"create ingress.yaml\",  \"content\": \"" + encodedIngressYamlFile + "\" }"))
                .build();
        // send http request
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return response.statusCode();
    }

    public static int createArgoApp(String reposName, String appName, String nameSpace) throws IOException, InterruptedException {
        if (GitUtil.checkGitReposExist(reposName) != 200) {
            throw new NotFoundException("Repository not found.", "This repository does not exist.");
        }
        // find template deployment file
        String argoAppYaml = FileUtil.readFile("src/main/java/com/kshrd/autopilot/util/fileConfig/argo-application.yaml");
        System.out.println(argoAppYaml);
        // List spring-gradle.pipeline.xml to replace on the sameple file
        Map<String, String> replaceString = new HashMap<>();
        replaceString.put("var-app-name", appName);
        replaceString.put("var-cd-repos", reposName);
        replaceString.put("var-namespace", nameSpace);
        // replace string operation
        for (Map.Entry<String, String> entry : replaceString.entrySet()) {
            argoAppYaml = argoAppYaml.replace(entry.getKey(), entry.getValue());
        }
        System.out.println(argoAppYaml);
        // encode string to base64 for github api
        String encodedArgoAppYamlFile = Base64.getEncoder().encodeToString(argoAppYaml.getBytes());
        System.out.println(encodedArgoAppYamlFile);
        // build http request client
        HttpClient httpClient = HttpClient.newBuilder().build();
        String gitEndpoint = "https://api.github.com/repos/KSGA-Autopilot/" + reposName + "/contents/argo-application.yaml";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(gitEndpoint))
                .header("Content-Type", "application/json")
                .header("Accept", "application/vnd.github+json")
                .header("Authorization", "Bearer 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")
                .PUT(HttpRequest.BodyPublishers.ofString("{ \"message\": \"create argo-app.yaml\",  \"content\": \"" + encodedArgoAppYamlFile + "\" }"))
                .build();
        // send http request
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return response.statusCode();
    }

    public static int createNamespaceTlsCertificate(String reposName, String dns, String nameSpace) throws IOException, InterruptedException {
        if (GitUtil.checkGitReposExist(reposName) != 200) {
            throw new NotFoundException("Repository not found.", "This repository does not exist.");
        }
        // find template deployment file
        String certYaml = FileUtil.readFile("src/main/java/com/kshrd/autopilot/util/fileConfig/certificate.yaml");
        System.out.println(certYaml);
        // List spring-gradle.pipeline.xml to replace on the sameple file
        Map<String, String> replaceString = new HashMap<>();
        replaceString.put("dns-name", dns);
        replaceString.put("cert-namespace", nameSpace);
        // replace string operation
        for (Map.Entry<String, String> entry : replaceString.entrySet()) {
            certYaml = certYaml.replace(entry.getKey(), entry.getValue());
        }
        System.out.println(certYaml);
        // encode string to base64 for github api
        String certYamlFile = Base64.getEncoder().encodeToString(certYaml.getBytes());
        System.out.println(certYamlFile);
        // build http request client
        HttpClient httpClient = HttpClient.newBuilder().build();
        String gitEndpoint = "https://api.github.com/repos/KSGA-Autopilot/" + reposName + "/contents/app/certificate.yaml";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(gitEndpoint))
                .header("Content-Type", "application/json")
                .header("Accept", "application/vnd.github+json")
                .header("Authorization", "Bearer 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")
                .PUT(HttpRequest.BodyPublishers.ofString("{ \"message\": \"create certificate.yaml\",  \"content\": \"" + certYamlFile + "\" }"))
                .build();
        // send http request
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return response.statusCode();
    }

    private static int checkGitReposExist(String reposName) throws IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newBuilder().build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.github.com/repos/KSGA-Autopilot/" + reposName))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer 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")
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return response.statusCode();
    }

    private static int deleteGitReposExist(String reposName) throws IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newBuilder().build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.github.com/repos/KSGA-Autopilot/" + reposName))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer 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")
                .DELETE()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return response.statusCode();
    }
}
