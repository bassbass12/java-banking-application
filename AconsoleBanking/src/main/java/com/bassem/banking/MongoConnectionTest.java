package com.bassem.banking;

import com.bassem.banking.dao.MongoCustomerDAO;
import com.bassem.banking.dao.MongoBankAccountDAO;
import com.bassem.banking.dao.MongoTransactionDAO;

import com.mongodb.client.MongoDatabase;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.mongodb.client.MongoDatabase;

public class MongoConnectionTest
{

    public static void main(String[] args)
    {

        // CONNECTION TEST MongoDB

        MongoDatabase database = MongoDatabaseConnection.getDatabase();

        System.out.println("Connected to MongoDB!");
        System.out.println("Database: " + database.getName());

        // TEST ALL CUSTOMER DAO METHODS
        MongoCustomerDAO customerDAO = new MongoCustomerDAO();

        //----------SAVE-------
        Customer customer = new Customer(
                20L,
                "Test User",
                "test@email.com",
                "hashedPassword"

        );

        // ----Save Another Customer----

        Customer customer2 = new Customer(
                21L,
                "Alice",
                "alice@email.com",
                "hashedPassword2"
        );

        customerDAO.save(customer);
        customerDAO.save(customer2);

        System.out.println("Customer saved!");
        System.out.println("Second customer saved!");

        //---------FindById------
        Customer found = customerDAO.findById(20L);
        System.out.println("Customer found:");
        System.out.println(found.getId());
        System.out.println(found.getName());
        System.out.println(found.getEmail());

        //-----------Find All----
        List<Customer> customers = customerDAO.findAll();

        System.out.println("All customers:");

        for (Customer c : customers) {
            System.out.println(
                    c.getId() + " - " + c.getName()
            );
        }


        // ---------- Update-------

        customer.setName("John");
        customer.setEmail("john@email.com");

        customerDAO.update(customer2);
        System.out.println("Customer updated!");


        customerDAO.delete(20L);
        System.out.println("Customer deleted!");

        // Test All Methods for Bank Account

        MongoBankAccountDAO bankAccountDAO = new MongoBankAccountDAO();

        // ---------- Save----------

        BankAccount account = new BankAccount(
                1L,
                "CHK1001",
                AccountType.CHECKING,
                new BigDecimal("500.00"),
                AccountStatus.ACTIVE,
                customer2
        );

        bankAccountDAO.save(account);

        System.out.println("Bank account saved!");

        //--- Find By ID

        BankAccount foundAccount =
                bankAccountDAO.findById(1L);

        System.out.println("Bank account found:");

        System.out.println(foundAccount.getId());
        System.out.println(foundAccount.getAccountNumber());
        System.out.println(foundAccount.getAccountType());
        System.out.println(foundAccount.getBalance());
        System.out.println(foundAccount.getStatus());
        System.out.println(foundAccount.getOwner().getId());

        // ---------- Find All ----------

        List<BankAccount> accounts =
                bankAccountDAO.findAll();

        System.out.println("All bank accounts:");

        for (BankAccount a : accounts) {

            System.out.println(
                    a.getId() + " - " +
                            a.getAccountNumber() + " - " +
                            a.getAccountType() + " - " +
                            a.getBalance() + " - " +
                            a.getStatus() + " - Owner: " +
                            a.getOwner().getId()
            );

            // ---------- Update ----------

            account.setAccountNumber("CHK9999");
            account.setBalance(new BigDecimal("750.00"));
            account.setStatus(AccountStatus.CLOSED);

            bankAccountDAO.update(account);

            System.out.println("Bank account updated!");
        }

        // ---------- Verify Update ----------

        BankAccount updatedAccount =
                bankAccountDAO.findById(1L);

        System.out.println("Updated account:");

        System.out.println(updatedAccount.getAccountNumber());
        System.out.println(updatedAccount.getBalance());
        System.out.println(updatedAccount.getStatus());

        // ---------- Delete ----------

        bankAccountDAO.delete(1L);

        System.out.println("Bank account deleted!");

        // ---------- Verify Delete ----------

        BankAccount deletedAccount =
                bankAccountDAO.findById(1L);

        System.out.println("After delete: " + deletedAccount);

        //========================

        // Create Bank Account Inorder To Do Transaction Test.

        BankAccount transactionAccount = new BankAccount(
                2L,
                "SAV2001",
                AccountType.SAVING,
                new BigDecimal("1000.00"),
                AccountStatus.ACTIVE,
                customer2
        );

        bankAccountDAO.save(transactionAccount);

        System.out.println("Bank account for transaction saved!");

        // Test All Transaction Dao Methods

        MongoTransactionDAO transactionDAO =
                new MongoTransactionDAO();

        // ---------- Save  ----------

        Transaction transaction = new Transaction(
                1L,
                new BigDecimal("100.00"),
                LocalDateTime.now(),
                TransactionType.DEPOSIT,
                new BigDecimal("1100.00")
        );

        transaction.setAccount(transactionAccount);

        transactionDAO.save(transaction);

        System.out.println("Transaction saved!");

        // ---------- Find By ID ----------

        Transaction foundTransaction =
                transactionDAO.findById(1L);

        System.out.println("Transaction found:");

        System.out.println(foundTransaction.getId());
        System.out.println(foundTransaction.getAmount());
        System.out.println(foundTransaction.getDate());
        System.out.println(foundTransaction.getType());
        System.out.println(foundTransaction.getResultingBalance());

        // ---------- Find All ----------

          List<Transaction> transactions=
         transactionDAO.findAll();

        System.out.println("All transactions:");

        for (Transaction t : transactions) {

            System.out.println(
                    t.getId() + " - " +
                            t.getAmount() + " - " +
                            t.getType() + " - " +
                            t.getResultingBalance() + " - Account: " +
                            t.getAccount().getId()
            );

        }

        // ---------- Update ----------

        transaction.setAmount(new BigDecimal("200.00"));
        transaction.setType(TransactionType.WITHDRAW);
        transaction.setResultingBalance(new BigDecimal("900.00"));

        transactionDAO.update(transaction);

        System.out.println("Transaction updated!");

        // ---------- Verify Update ----------

        Transaction updatedTransaction =
                transactionDAO.findById(1L);

        System.out.println("Updated transaction:");

        System.out.println(updatedTransaction.getId());
        System.out.println(updatedTransaction.getAmount());
        System.out.println(updatedTransaction.getType());
        System.out.println(updatedTransaction.getResultingBalance());

        // ---------- Delete ----------

        transactionDAO.delete(1L);

        System.out.println("Transaction deleted!");

        // ---------- VERIFY Delete ----------

        Transaction deletedTransaction =
                transactionDAO.findById(1L);

        System.out.println("After delete: " + deletedTransaction);

    }



}
