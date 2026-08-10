package com.bassem.banking;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DatabaseConfig {

    private static final Properties properties = new Properties();

    static {
        try (InputStream input =
                     DatabaseConfig.class
                             .getClassLoader()
                             .getResourceAsStream("application.properties"))
        {

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

    public static DatabaseType getDatabaseType() {

        String type = properties.getProperty("database.type");

        return DatabaseType.valueOf(type.toUpperCase());
    }
}