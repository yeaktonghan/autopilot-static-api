package com.kshrd.autopilot.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseUtil {
    public static void createPostgres(String name,String username,String pwd){
        String url="jdbc:postgresql://128.199.138.228:5432/postgres";
        String password="12345";
        String usr="postgres";

        try {
            Connection connection= DriverManager.getConnection(url,usr,password);

            Statement statement=connection.createStatement();

            statement.executeUpdate("CREATE DATABASE " + name);

            statement.executeUpdate("CREATE USER " + username + " WITH PASSWORD '" + pwd + "'");

            statement.executeUpdate("GRANT ALL PRIVILEGES ON DATABASE " + name + " TO " + username);

            connection.close();
            statement.close();

        }catch (SQLException e) {
            e.printStackTrace();
        }

    }

}
