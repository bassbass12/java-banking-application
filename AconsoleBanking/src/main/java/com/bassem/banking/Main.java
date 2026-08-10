package com.bassem.banking;

import java.sql.Connection;
import java.sql.SQLException;

import com.bassem.banking.dao.BankAccountDAO;
import com.bassem.banking.dao.CustomerDAO;
import com.bassem.banking.dao.TransactionDAO;

public class Main {
    public static void main(String[] args) {
        // test connection
        try {
            Connection connection = DatabaseConnection.getConnection();
            System.out.println("Connected successfully!");
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        DatabaseType type = DatabaseConfig.getDatabaseType();

        System.out.println("Selected database: " + type);

        CustomerDAO customerDAO =
                DatabaseFactory.getCustomerDAO(type);

        BankAccountDAO bankAccountDAO =
                DatabaseFactory.getBankAccountDAO(type);

        TransactionDAO transactionDAO =
                DatabaseFactory.getTransactionDAO(type);

        System.out.println(
                customerDAO.getClass().getSimpleName()
        );

        System.out.println(
                bankAccountDAO.getClass().getSimpleName()
        );

        System.out.println(
                transactionDAO.getClass().getSimpleName()
        );

    }
}
