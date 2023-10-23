package com.kshrd.autopilot.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileUtil {
    public static String readFile(String file) {
        Path filePaths= Paths.get(file);
       String filePath= String.valueOf(filePaths.toAbsolutePath());
        StringBuilder text=new StringBuilder();
        try {
            // Create a FileReader to open the file
            FileReader fileReader = new FileReader(filePath);

            // Wrap the FileReader in a BufferedReader for efficient reading
            BufferedReader bufferedReader = new BufferedReader(fileReader);
         String line;
           // /Users/somnangpho/Desktop/finalProject/autopilot/src/main/java/com/kshrd/autopilot/util/dockerfile/react-npm
            // Read and print each line from the file
            while ((line = bufferedReader.readLine()) != null) {
                text.append(line).append("\n");
                //System.out.println(line);
            }

            // Close the BufferedReader and FileReader
            bufferedReader.close();
            fileReader.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return text.toString();
    }
    
}
