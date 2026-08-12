package com.bassem.banking;

import com.bassem.banking.dao.BankAccountDAO;
import com.bassem.banking.dao.CustomerDAO;
import com.bassem.banking.dao.TransactionDAO;
import com.bassem.banking.service.BankAccountService;
import com.bassem.banking.service.CustomerService;
import com.bassem.banking.service.TransactionService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        // =====================================================
        // DATABASE SELECTION
        // =====================================================

        DatabaseType type = null;

        while (type == null) {

            System.out.println();
            System.out.println("Choose database:");
            System.out.println();
            System.out.println("1. PostgreSQL");
            System.out.println("2. MongoDB");
            System.out.println("3. Exit");
            System.out.println();

            System.out.print("Enter choice: ");

            String choice = scanner.nextLine();

            if (choice.equals("1")) {
                type = DatabaseType.POSTGRES;

            } else if (choice.equals("2")) {
                type = DatabaseType.MONGO;

            } else if (choice.equals("3")) {
                System.out.println("Goodbye!");
                scanner.close();
                return;

            } else {
                System.out.println("Invalid choice.");
            }
        }

        System.out.println();
        System.out.println("Selected database: " + type);

        // =====================================================
        // DAOs
        // =====================================================

        CustomerDAO customerDAO =
                DatabaseFactory.getCustomerDAO(type);

        BankAccountDAO bankAccountDAO =
                DatabaseFactory.getBankAccountDAO(type);

        TransactionDAO transactionDAO =
                DatabaseFactory.getTransactionDAO(type);

        // =====================================================
        // SERVICES
        // =====================================================

        CustomerService customerService =
                new CustomerService(customerDAO);

        BankAccountService bankAccountService =
                new BankAccountService(bankAccountDAO);

        TransactionService transactionService =
                new TransactionService(
                        transactionDAO,
                        bankAccountDAO
                );

        // =====================================================
        // LOGIN / REGISTER MENU
        // =====================================================

        Customer loggedInCustomer = null;

        while (true) {

            System.out.println();
            System.out.println("================================");
            System.out.println("        BANKING APPLICATION");
            System.out.println("================================");
            System.out.println();

            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            System.out.println();

            System.out.print("Enter choice: ");

            String choice = scanner.nextLine();

            // =================================================
            // REGISTER
            // =================================================

            if (choice.equals("1")) {

                try {

                    System.out.println();
                    System.out.println("----- Register -----");

                    System.out.print("Enter name: ");
                    String name = scanner.nextLine();

                    System.out.print("Enter email: ");
                    String email = scanner.nextLine();

                    System.out.print("Enter password: ");
                    String password = scanner.nextLine();

                    /*
                     * Generate a unique ID.
                     * We do NOT manually use 1001, 1002, etc.
                     */
                    long customerId =
                            System.currentTimeMillis();

                    Customer customer =
                            new Customer(
                                    customerId,
                                    name,
                                    email,
                                    password
                            );

                    customerService.registerCustomer(customer);

                    System.out.println();
                    System.out.println(
                            "Registration successful!"
                    );

                } catch (Exception e) {

                    System.out.println();
                    System.out.println(
                            "Registration failed: "
                                    + e.getMessage()
                    );
                }

                // =================================================
                // LOGIN
                // =================================================

            } else if (choice.equals("2")) {

                System.out.println();
                System.out.println("----- Login -----");

                System.out.print("Enter email: ");
                String email = scanner.nextLine();

                System.out.print("Enter password: ");
                String password = scanner.nextLine();

                try {

                    loggedInCustomer =
                            customerService.login(
                                    email,
                                    password
                            );

                    if (loggedInCustomer == null) {

                        System.out.println();
                        System.out.println(
                                "Invalid email or password."
                        );

                        continue;
                    }

                    System.out.println();
                    System.out.println(
                            "Login successful!"
                    );

                    System.out.println(
                            "Welcome, "
                                    + loggedInCustomer.getName()
                                    + "!"
                    );

                    // Go to customer menu
                    customerMenu(
                            scanner,
                            loggedInCustomer,
                            customerService,
                            bankAccountService,
                            transactionService
                    );

                    // Logout
                    loggedInCustomer = null;

                } catch (Exception e) {

                    System.out.println();
                    System.out.println(
                            "Login failed: "
                                    + e.getMessage()
                    );
                }

                // =================================================
                // EXIT
                // =================================================

            } else if (choice.equals("3")) {

                System.out.println();
                System.out.println("Goodbye!");

                scanner.close();
                return;

            } else {

                System.out.println();
                System.out.println("Invalid choice.");
            }
        }
    }


    // =========================================================
    // CUSTOMER MENU
    // =========================================================

    private static void customerMenu(
            Scanner scanner,
            Customer customer,
            CustomerService customerService,
            BankAccountService bankAccountService,
            TransactionService transactionService) {

        while (true) {

            System.out.println();
            System.out.println("================================");
            System.out.println("          CUSTOMER MENU");
            System.out.println("================================");
            System.out.println();

            System.out.println("1. View Profile");
            System.out.println("2. Update Profile");
            System.out.println("3. Open Checking Account");
            System.out.println("4. Open Savings Account");
            System.out.println("5. View My Accounts");
            System.out.println("6. View Balance");
            System.out.println("7. Deposit");
            System.out.println("8. Withdraw");
            System.out.println("9. Transfer");
            System.out.println("10. Transaction History");
            System.out.println("11. Close Account");
            System.out.println("12. Logout");
            System.out.println();

            System.out.print("Enter choice: ");

            String choice = scanner.nextLine();

            try {

                // =================================================
                // 1. VIEW PROFILE
                // =================================================

                if (choice.equals("1")) {

                    System.out.println();
                    System.out.println("----- My Profile -----");

                    System.out.println(
                            "ID: "
                                    + customer.getId()
                    );

                    System.out.println(
                            "Name: "
                                    + customer.getName()
                    );

                    System.out.println(
                            "Email: "
                                    + customer.getEmail()
                    );

                    // =================================================
                    // 2. UPDATE PROFILE
                    // =================================================

                } else if (choice.equals("2")) {

                    System.out.println();
                    System.out.println("----- Update Profile -----");

                    System.out.print(
                            "Enter new name: "
                    );

                    String name =
                            scanner.nextLine();

                    System.out.print(
                            "Enter new email: "
                    );

                    String email =
                            scanner.nextLine();

                    customer.setName(name);
                    customer.setEmail(email);

                    customerService.updateProfile(
                            customer
                    );

                    System.out.println();
                    System.out.println(
                            "Profile updated successfully."
                    );

                    // =================================================
                    // 3. OPEN CHECKING
                    // =================================================

                } else if (choice.equals("3")) {

                    openAccount(
                            scanner,
                            customer,
                            AccountType.CHECKING,
                            bankAccountService
                    );

                    // =================================================
                    // 4. OPEN SAVINGS
                    // =================================================

                } else if (choice.equals("4")) {

                    openAccount(
                            scanner,
                            customer,
                            AccountType.SAVING,
                            bankAccountService
                    );

                    // =================================================
                    // 5. VIEW MY ACCOUNTS
                    // =================================================

                } else if (choice.equals("5")) {

                    List<BankAccount> accounts =
                            bankAccountService.findAllAccounts();

                    System.out.println();
                    System.out.println("----- My Accounts -----");

                    boolean found = false;

                    for (BankAccount account : accounts) {

                        if (account.getOwner() != null
                                && account.getOwner()
                                .getId()
                                .equals(customer.getId())) {

                            found = true;

                            System.out.println();
                            System.out.println(
                                    "Account ID: "
                                            + account.getId()
                            );

                            System.out.println(
                                    "Account Number: "
                                            + account.getAccountNumber()
                            );

                            System.out.println(
                                    "Type: "
                                            + account.getAccountType()
                            );

                            System.out.println(
                                    "Balance: $"
                                            + account.getBalance()
                            );

                            System.out.println(
                                    "Status: "
                                            + account.getStatus()
                            );
                        }
                    }

                    if (!found) {

                        System.out.println(
                                "You do not have any accounts."
                        );
                    }

                    // =================================================
                    // 6. VIEW BALANCE
                    // =================================================

                } else if (choice.equals("6")) {

                    System.out.print(
                            "Enter account ID: "
                    );

                    Long accountId =
                            Long.parseLong(
                                    scanner.nextLine()
                            );

                    BankAccount account =
                            bankAccountService.findAccountById(
                                    accountId
                            );

                    if (account == null) {

                        System.out.println(
                                "Account not found."
                        );

                    } else if (
                            account.getOwner() == null
                                    || !account.getOwner()
                                    .getId()
                                    .equals(customer.getId())) {

                        System.out.println(
                                "You are not authorized."
                        );

                    } else {

                        System.out.println();
                        System.out.println(
                                "Balance: $"
                                        + account.getBalance()
                        );
                    }

                    // =================================================
                    // 7. DEPOSIT
                    // =================================================

                } else if (choice.equals("7")) {

                    System.out.print(
                            "Enter account ID: "
                    );

                    Long accountId =
                            Long.parseLong(
                                    scanner.nextLine()
                            );

                    System.out.print(
                            "Enter deposit amount: $"
                    );

                    BigDecimal amount =
                            new BigDecimal(
                                    scanner.nextLine()
                            );

                    Transaction transaction =
                            transactionService.deposit(
                                    customer,
                                    accountId,
                                    amount
                            );

                    System.out.println();
                    System.out.println(
                            "Deposit successful."
                    );

                    System.out.println(
                            "New balance: $"
                                    + transaction
                                    .getResultingBalance()
                    );

                    // =================================================
                    // 8. WITHDRAW
                    // =================================================

                } else if (choice.equals("8")) {

                    System.out.print(
                            "Enter account ID: "
                    );

                    Long accountId =
                            Long.parseLong(
                                    scanner.nextLine()
                            );

                    System.out.print(
                            "Enter withdrawal amount: $"
                    );

                    BigDecimal amount =
                            new BigDecimal(
                                    scanner.nextLine()
                            );

                    Transaction transaction =
                            transactionService.withdraw(
                                    customer,
                                    accountId,
                                    amount
                            );

                    System.out.println();
                    System.out.println(
                            "Withdrawal successful."
                    );

                    System.out.println(
                            "New balance: $"
                                    + transaction
                                    .getResultingBalance()
                    );

                    // =================================================
                    // 9. TRANSFER
                    // =================================================

                } else if (choice.equals("9")) {

                    System.out.print(
                            "Enter source account ID: "
                    );

                    Long sourceId =
                            Long.parseLong(
                                    scanner.nextLine()
                            );

                    System.out.print(
                            "Enter destination account ID: "
                    );

                    Long destinationId =
                            Long.parseLong(
                                    scanner.nextLine()
                            );

                    System.out.print(
                            "Enter transfer amount: $"
                    );

                    BigDecimal amount =
                            new BigDecimal(
                                    scanner.nextLine()
                            );

                    transactionService.transfer(
                            customer,
                            sourceId,
                            destinationId,
                            amount
                    );

                    System.out.println();
                    System.out.println(
                            "Transfer successful."
                    );

                    // =================================================
                    // 10. TRANSACTION HISTORY
                    // =================================================

                } else if (choice.equals("10")) {

                    System.out.print(
                            "Enter account ID: "
                    );

                    Long accountId =
                            Long.parseLong(
                                    scanner.nextLine()
                            );

                    List<Transaction> history =
                            transactionService
                                    .getTransactionHistory(
                                            customer,
                                            accountId
                                    );

                    System.out.println();
                    System.out.println(
                            "----- Transaction History -----"
                    );

                    if (history.isEmpty()) {

                        System.out.println(
                                "No transactions found."
                        );

                    } else {

                        for (Transaction transaction :
                                history) {

                            System.out.println();

                            System.out.println(
                                    "ID: "
                                            + transaction.getId()
                            );

                            System.out.println(
                                    "Type: "
                                            + transaction.getType()
                            );

                            System.out.println(
                                    "Amount: $"
                                            + transaction.getAmount()
                            );

                            System.out.println(
                                    "Date: "
                                            + transaction.getDate()
                            );

                            System.out.println(
                                    "Resulting Balance: $"
                                            + transaction
                                            .getResultingBalance()
                            );
                        }
                    }

                    // =================================================
                    // 11. CLOSE ACCOUNT
                    // =================================================

                } else if (choice.equals("11")) {

                    System.out.print(
                            "Enter account ID: "
                    );

                    Long accountId =
                            Long.parseLong(
                                    scanner.nextLine()
                            );

                    BankAccount account =
                            bankAccountService
                                    .findAccountById(
                                            accountId
                                    );

                    if (account == null) {

                        System.out.println(
                                "Account not found."
                        );

                    } else if (
                            account.getOwner() == null
                                    || !account.getOwner()
                                    .getId()
                                    .equals(customer.getId())) {

                        System.out.println(
                                "You are not authorized."
                        );

                    } else {

                        bankAccountService.closeAccount(
                                accountId
                        );

                        System.out.println();
                        System.out.println(
                                "Account closed successfully."
                        );
                    }

                    // =================================================
                    // 12. LOGOUT
                    // =================================================

                } else if (choice.equals("12")) {

                    System.out.println();
                    System.out.println(
                            "Logged out successfully."
                    );

                    return;

                } else {

                    System.out.println();
                    System.out.println(
                            "Invalid choice."
                    );
                }

            } catch (Exception e) {

                System.out.println();
                System.out.println(
                        "Operation failed: "
                                + e.getMessage()
                );
            }
        }
    }


    // =========================================================
    // OPEN ACCOUNT
    // =========================================================

    private static void openAccount(
            Scanner scanner,
            Customer customer,
            AccountType accountType,
            BankAccountService bankAccountService) {

        System.out.println();

        System.out.println(
                "----- Open "
                        + accountType
                        + " Account -----"
        );

        System.out.print(
                "Enter opening balance: $"
        );

        BigDecimal balance =
                new BigDecimal(
                        scanner.nextLine()
                );

        /*
         * Generate unique IDs automatically.
         * No more manually changing 2001 -> 2005 -> 2020.
         */
        long accountId =
                System.currentTimeMillis();

        String accountNumber =
                "ACC-" + accountId;

        BankAccount account =
                new BankAccount(
                        accountId,
                        accountNumber,
                        accountType,
                        balance,
                        AccountStatus.ACTIVE,
                        customer
                );

        bankAccountService.openAccount(
                account
        );

        System.out.println();
        System.out.println(
                "Account opened successfully!"
        );

        System.out.println(
                "Account ID: "
                        + account.getId()
        );

        System.out.println(
                "Account Number: "
                        + account.getAccountNumber()
        );

        System.out.println(
                "Type: "
                        + account.getAccountType()
        );

        System.out.println(
                "Balance: $"
                        + account.getBalance()
        );
    }
}