package com.kshrd.autopilot.util;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class FileUtil {
    public static String readFile(String file) {
        Path filePaths = Paths.get(file);
        System.out.println(filePaths.toString());
        String filePath = String.valueOf(filePaths.toAbsolutePath());
        System.out.println(filePath);
        StringBuilder text = new StringBuilder();
        try {
            // Create a FileReader to open the file
            FileReader fileReader = new FileReader(filePath);

            // Wrap the FileReader in a BufferedReader for efficient reading
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                text.append(line).append("\n");
                //System.out.println(line);
            }

            // Close the BufferedReader and FileReader
            bufferedReader.close();
            fileReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return text.toString();
    }

    public static String replaceText(File path, Map<String, String> replacements) {
        try (BufferedReader reader = new BufferedReader(new FileReader(path.getAbsoluteFile()));
             BufferedWriter writer = new BufferedWriter(new FileWriter("temp_file.txt"))) {

            String line;
            while ((line = reader.readLine()) != null) {
                for (Map.Entry<String, String> entry : replacements.entrySet()) {
                    line = line.replace(entry.getKey(), entry.getValue());
                }
                writer.write(line);
                writer.newLine(); // Add a new line after each line
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return readFile("temp_file.txt");
        //System.out.println(readFile("temp_file.txt"));
        // Rename the temp file to the original file to replace it
    }

}
