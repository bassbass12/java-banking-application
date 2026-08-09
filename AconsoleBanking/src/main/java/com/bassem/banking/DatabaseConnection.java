package com.bassem.banking;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import  java.sql.SQLException;
import java.util.Properties;


public class DatabaseConnection {
    private static final Properties properties = new Properties();
    static {
        try (InputStream input =
                     DatabaseConnection.class
                             .getClassLoader()
                             .getResourceAsStream("application.properties")) {

            if (input == null) {
                throw new RuntimeException(
                        "application.properties not found!"
                );
            }

            properties.load(input);

        } catch (IOException e) {
            throw new RuntimeException(
                    "Could not load application.properties", e
            );
        }
    }

    public  static Connection getConnection()throws SQLException{

        String url = properties.getProperty("postgresql.url");
        String user = properties.getProperty("postgresql.username");
        String password = properties.getProperty("postgresql.password");

        return DriverManager.getConnection(url,user,password);

    }


}
