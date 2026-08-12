package com.bassem.banking.MianTest;


//package com.bassem.banking;

import com.bassem.banking.*;
import com.bassem.banking.service.TransactionService;
import com.bassem.banking.dao.BankAccountDAO;
import com.bassem.banking.dao.CustomerDAO;
import com.bassem.banking.dao.TransactionDAO;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Scanner;

public class TestMain {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        System.out.println("Choose database:");
        System.out.println("1. PostgreSQL");
        System.out.println("2. MongoDB");
        System.out.print("Enter choice: ");

        String choice = scanner.nextLine();

        DatabaseType type;

        if (choice.equals("1")) {
            type = DatabaseType.POSTGRES;
        } else if (choice.equals("2")) {
            type = DatabaseType.MONGO;
        } else {
            System.out.println("Invalid choice.");
            scanner.close();
            return;
        }

        System.out.println();
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

        // ==============================================
        // TEST DATABASE CONNECTION
        // ==============================================

        if (type == DatabaseType.MONGO) {

            MongoDatabaseConnection
                    .getDatabase()
                    .runCommand(
                            new org.bson.Document("ping", 1)
                    );

            System.out.println(
                    "MongoDB connection successful!"
            );
        }

        // ==============================================
        // CREATE TEST CUSTOMER
        // ==============================================

        Customer customer = new Customer(
                1020L,
                "Test Customer",
                "test1020@email.com",
                "testPassword"
        );

        customerDAO.save(customer);

        System.out.println(
                "Customer saved: "
                        + customer.getName()
        );

        // ==============================================
        // FIND CUSTOMER
        // ==============================================

        Customer foundCustomer =
                customerDAO.findById(1020L);

        if (foundCustomer != null) {

            System.out.println(
                    "Customer found: "
                            + foundCustomer.getName()
            );
        }

        // ==============================================
        // CREATE TEST ACCOUNT
        // ==============================================

        BankAccount account = new BankAccount(
                2020L,
                "TEST-1020",
                AccountType.CHECKING,
                new BigDecimal("500.00"),
                AccountStatus.ACTIVE,
                customer
        );

        bankAccountDAO.save(account);

        System.out.println(
                "Bank account saved: "
                        + account.getAccountNumber()
        );

        // ==============================================
        // FIND ACCOUNT
        // ==============================================

        BankAccount foundAccount =
                bankAccountDAO.findById(2020L);

        if (foundAccount != null) {

            System.out.println(
                    "Bank account found: "
                            + foundAccount.getAccountNumber()
            );

            System.out.println(
                    "Balance: "
                            + foundAccount.getBalance()
            );
        }

        // ==============================================
        // TRANSACTION SERVICE
        // ==============================================

        TransactionService transactionService =
                new TransactionService(
                        transactionDAO,
                        bankAccountDAO
                );

        // ==============================================
        // DEPOSIT
        // ==============================================

        System.out.println();
        System.out.println("Testing deposit...");

        Transaction deposit =
                transactionService.deposit(
                        customer,
                        2020L,
                        new BigDecimal("100.00")
                );

        System.out.println(
                "Deposit successful."
        );

        System.out.println(
                "New balance: "
                        + deposit.getResultingBalance()
        );

        // ==============================================
        // WITHDRAW
        // ==============================================

        System.out.println();
        System.out.println("Testing withdrawal...");

        Transaction withdrawal =
                transactionService.withdraw(
                        customer,
                        2020L,
                        new BigDecimal("50.00")
                );

        System.out.println(
                "Withdrawal successful."
        );

        System.out.println(
                "New balance: "
                        + withdrawal.getResultingBalance()
        );

        // ==============================================
        // HISTORY
        // ==============================================

        System.out.println();
        System.out.println(
                "Testing transaction history..."
        );

        var history =
                transactionService.getTransactionHistory(
                        customer,
                        2020L
                );

        System.out.println(
                "Transaction count: "
                        + history.size()
        );

        System.out.println();
        System.out.println("ALL TESTS COMPLETED.");

        scanner.close();
    }
}