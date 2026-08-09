package com.bassem.banking;

import java.sql.Connection;
import java.sql.SQLException;
public class Main {
     public static void main(String[] args) {
        System.out.println("hello");

         try {
             Connection connection = DatabaseConnection.getConnection();
             System.out.println("Connected successfully!");
             connection.close();

         } catch (SQLException e) {
             e.printStackTrace();
         }


     }
}
