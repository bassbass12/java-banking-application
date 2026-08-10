package com.bassem.banking.service;

import com.bassem.banking.Customer;
import com.bassem.banking.BankAccount;
import com.bassem.banking.Transaction;
import com.bassem.banking.TransactionType;
import com.bassem.banking.AccountStatus;

import com.bassem.banking.dao.BankAccountDAO;
import com.bassem.banking.dao.TransactionDAO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


public class TransactionService {

    private final TransactionDAO transactionDAO;
    private final BankAccountDAO bankAccountDAO;

    public TransactionService(
            TransactionDAO transactionDAO,
            BankAccountDAO bankAccountDAO){
        this.transactionDAO = transactionDAO;
        this.bankAccountDAO = bankAccountDAO;
    }



    // ---------- Deposit ----------

    public Transaction deposit(
            Customer customer,
            Long accountId,
            BigDecimal amount) {

        BankAccount account = bankAccountDAO.findById(accountId);

        if (account == null) {
            throw new IllegalArgumentException(
                    "Bank account not found."
            );
        }

        if (account.getOwner() == null ||
                customer == null ||
                !account.getOwner().getId().equals(customer.getId())) {

            throw new IllegalArgumentException(
                    "You are not authorized to access this account."
            );
        }

        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new IllegalArgumentException(
                    "Account is not active."
            );
        }

        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException(
                    "Deposit amount must be greater than zero."
            );
        }

        BigDecimal newBalance =
                account.getBalance().add(amount);

        account.setBalance(newBalance);
        bankAccountDAO.update(account);

        Transaction transaction = new Transaction();

        transaction.setAmount(amount);
        transaction.setDate(LocalDateTime.now());
        transaction.setType(TransactionType.DEPOSIT);
        transaction.setResultingBalance(newBalance);
        transaction.setAccount(account);

        return transactionDAO.save(transaction);
    }


    // ---------- Withdraw ----------

    public Transaction withdraw(
            Customer customer,
            Long accountId,
            BigDecimal amount) {

        BankAccount account = bankAccountDAO.findById(accountId);

        if (account == null) {
            throw new IllegalArgumentException(
                    "Bank account not found."
            );
        }

        if (account.getOwner() == null ||
                customer == null ||
                !account.getOwner().getId().equals(customer.getId())) {

            throw new IllegalArgumentException(
                    "You are not authorized to access this account."
            );
        }

        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new IllegalArgumentException(
                    "Account is not active."
            );
        }

        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException(
                    "Withdrawal amount must be greater than zero."
            );
        }

        if (account.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException(
                    "Insufficient balance."
            );
        }

        BigDecimal newBalance =
                account.getBalance().subtract(amount);

        account.setBalance(newBalance);
        bankAccountDAO.update(account);

        Transaction transaction = new Transaction();

        transaction.setAmount(amount);
        transaction.setDate(LocalDateTime.now());
        transaction.setType(TransactionType.WITHDRAW);
        transaction.setResultingBalance(newBalance);
        transaction.setAccount(account);

        return transactionDAO.save(transaction);
    }


    // ---------- Transfer ----------

    public void transfer(
            Customer customer,
            Long sourceAccountId,
            Long destinationAccountId,
            BigDecimal amount) {

        BankAccount source =
                bankAccountDAO.findById(sourceAccountId);

        BankAccount destination =
                bankAccountDAO.findById(destinationAccountId);

        if (source == null || destination == null) {
            throw new IllegalArgumentException(
                    "Source or destination account not found."
            );
        }

        if (customer == null ||
                source.getOwner() == null ||
                !source.getOwner().getId().equals(customer.getId())) {

            throw new IllegalArgumentException(
                    "You are not authorized to transfer from this account."
            );
        }

        if (source.getStatus() != AccountStatus.ACTIVE ||
                destination.getStatus() != AccountStatus.ACTIVE) {

            throw new IllegalArgumentException(
                    "Both accounts must be active."
            );
        }

        if (sourceAccountId.equals(destinationAccountId)) {
            throw new IllegalArgumentException(
                    "Source and destination accounts must be different."
            );
        }

        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException(
                    "Transfer amount must be greater than zero."
            );
        }

        if (source.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException(
                    "Insufficient balance."
            );
        }

        BigDecimal sourceBalance =
                source.getBalance().subtract(amount);

        BigDecimal destinationBalance =
                destination.getBalance().add(amount);

        // calculate balances

        source.setBalance(sourceBalance);
        destination.setBalance(destinationBalance);

        // update accounts
        bankAccountDAO.update(source);
        bankAccountDAO.update(destination);

        //create withdrawal transaction
        Transaction withdrawal = new Transaction();

        withdrawal.setAmount(amount);
        withdrawal.setDate(LocalDateTime.now());
        withdrawal.setType(TransactionType.TRANSFER);
        withdrawal.setResultingBalance(sourceBalance);
        withdrawal.setAccount(source);

        // create deposit transaction
        transactionDAO.save(withdrawal);

        Transaction deposit = new Transaction();

        deposit.setAmount(amount);
        deposit.setDate(LocalDateTime.now());
        deposit.setType(TransactionType.TRANSFER);
        deposit.setResultingBalance(destinationBalance);
        deposit.setAccount(destination);

        transactionDAO.save(deposit);
    }

    // ---------- Transaction History ----------

    public List<Transaction> getTransactionHistory(
            Customer customer,
            Long accountId) {

        BankAccount account =
                bankAccountDAO.findById(accountId);

        if (account == null) {
            throw new IllegalArgumentException(
                    "Bank account not found."
            );
        }

        if (customer == null ||
                account.getOwner() == null ||
                !account.getOwner().getId().equals(customer.getId())) {

            throw new IllegalArgumentException(
                    "You are not authorized to view this account."
            );
        }

        return transactionDAO.findByAccountId(accountId);
    }



    // ---------- Find Transactions By Type ----------

    public List<Transaction> findByType(
            Customer customer,
            TransactionType type) {

        if (customer == null) {
            throw new IllegalArgumentException(
                    "Customer cannot be null."
            );
        }


        if (type == null) {
            throw new IllegalArgumentException(
                    "Transaction type is required."
            );
        }

        List<Transaction> transactions =
                transactionDAO.findByType(type);

        return transactions.stream()
                .filter(transaction ->
                        transaction.getAccount() != null &&
                                transaction.getAccount().getOwner() != null &&
                                transaction.getAccount()
                                        .getOwner()
                                        .getId()
                                        .equals(customer.getId())
                )
                .toList();




    }
    // ---------- Find Any Transaction By ID ----------

    public Transaction findTransactionById(
            Customer customer,
            Long id) {

        if (customer == null) {
            throw new IllegalArgumentException(
                    "Customer cannot be null."
            );
        }

        if (id == null) {
            throw new IllegalArgumentException(
                    "Transaction ID cannot be null."
            );
        }

        Transaction transaction =
                transactionDAO.findById(id);

        if (transaction == null) {
            return null;
        }

        if (transaction.getAccount() == null ||
                transaction.getAccount().getOwner() == null ||
                !transaction.getAccount()
                        .getOwner()
                        .getId()
                        .equals(customer.getId())) {

            throw new IllegalArgumentException(
                    "You are not authorized to view this transaction."
            );
        }

        return transaction;
    }

    }
