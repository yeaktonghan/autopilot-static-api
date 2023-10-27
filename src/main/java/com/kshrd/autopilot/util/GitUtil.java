package com.kshrd.autopilot.util;


import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class GitUtil {
    public  void gitCommand(String cmd){
        try{
            ProcessBuilder processBuilder=new ProcessBuilder();
            processBuilder.command("bash","-c",cmd);
            processBuilder.redirectErrorStream(true);
            Process process=processBuilder.start();

            BufferedReader reader=new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            while ((line=reader.readLine())!=null){
                System.out.println(line);
            }
        }catch (IOException  e){
            e.printStackTrace();
        }
    }
    public static void  createRepository(String reposName){
        try{
            String apiUrl="https://api.github.com/user/repos";
            String token="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";
            String owner="KSGA-Autopilot";
            URL url=new URL(apiUrl);
            HttpURLConnection connection= (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization","token "+token);
            connection.setRequestProperty("Content-Type","application/json");
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

        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
