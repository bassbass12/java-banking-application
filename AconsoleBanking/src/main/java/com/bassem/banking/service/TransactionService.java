package com.bassem.banking.service;


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

    public Transaction deposit(Long accountId, BigDecimal amount) {

        BankAccount account = bankAccountDAO.findById(accountId);

        if (account == null) {
            throw new IllegalArgumentException(
                    "Bank account not found."
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

    public Transaction withdraw(Long accountId, BigDecimal amount) {

        BankAccount account = bankAccountDAO.findById(accountId);

        if (account == null) {
            throw new IllegalArgumentException(
                    "Bank account not found."
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

        source.setBalance(sourceBalance);
        destination.setBalance(destinationBalance);

        bankAccountDAO.update(source);
        bankAccountDAO.update(destination);

        Transaction withdrawal = new Transaction();

        withdrawal.setAmount(amount);
        withdrawal.setDate(LocalDateTime.now());
        withdrawal.setType(TransactionType.TRANSFER);
        withdrawal.setResultingBalance(sourceBalance);
        withdrawal.setAccount(source);

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

    public List<Transaction> getTransactionHistory(Long accountId) {

        BankAccount account =
                bankAccountDAO.findById(accountId);

        if (account == null) {
            throw new IllegalArgumentException(
                    "Bank account not found."
            );
        }

        return transactionDAO.findByAccountId(accountId);
    }

    // ---------- Find Transactions By Type ----------

    public List<Transaction> findByType(TransactionType type) {

        if (type == null) {
            throw new IllegalArgumentException(
                    "Transaction type is required."
            );
        }

        return transactionDAO.findByType(type);
    }
    // ---------- Find Any Transaction By ID ----------

    public Transaction findTransactionById(Long id) {
        return transactionDAO.findById(id);
    }

    }
