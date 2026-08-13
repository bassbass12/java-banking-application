package com.bassem.banking.service;

import com.bassem.banking.AccountStatus;
import com.bassem.banking.BankAccount;
import com.bassem.banking.Customer;
import com.bassem.banking.DatabaseConnection;
import com.bassem.banking.MongoDatabaseConnection;
import com.bassem.banking.Transaction;
import com.bassem.banking.TransactionType;

import com.bassem.banking.dao.BankAccountDAO;
import com.bassem.banking.dao.TransactionDAO;
import com.bassem.banking.dao.MongoBankAccountDAO;
import com.bassem.banking.dao.MongoTransactionDAO;

import com.mongodb.client.ClientSession;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class TransactionService {

    private final TransactionDAO transactionDAO;
    private final BankAccountDAO bankAccountDAO;


    public TransactionService(
            TransactionDAO transactionDAO,
            BankAccountDAO bankAccountDAO) {

        this.transactionDAO = transactionDAO;
        this.bankAccountDAO = bankAccountDAO;
    }


    // =========================================================
    // DEPOSIT
    // =========================================================

    public Transaction deposit(
            Customer customer,
            Long accountId,
            BigDecimal amount) {

        BankAccount account =
                bankAccountDAO.findById(accountId);

        if (account == null) {
            throw new IllegalArgumentException(
                    "Bank account not found."
            );
        }

        if (account.getOwner() == null ||
                customer == null ||
                !account.getOwner()
                        .getId()
                        .equals(customer.getId())) {

            throw new IllegalArgumentException(
                    "You are not authorized to access this account."
            );
        }

        if (account.getStatus() !=
                AccountStatus.ACTIVE) {

            throw new IllegalArgumentException(
                    "Account is not active."
            );
        }

        if (amount == null ||
                amount.signum() <= 0) {

            throw new IllegalArgumentException(
                    "Deposit amount must be greater than zero."
            );
        }

        BigDecimal newBalance =
                account.getBalance()
                        .add(amount);

        account.setBalance(newBalance);

        Transaction transaction =
                new Transaction();

        // fix bugs1
        transaction.setId(System.currentTimeMillis());

        transaction.setAmount(amount);
        transaction.setDate(
                LocalDateTime.now()
        );
        transaction.setType(
                TransactionType.DEPOSIT
        );
        transaction.setResultingBalance(
                newBalance
        );
        transaction.setAccount(account);


        // =====================================================
        // MONGODB
        // =====================================================

        if (isMongo()) {

            try (ClientSession session =
                         MongoDatabaseConnection
                                 .getClient()
                                 .startSession()) {

                try {

                    session.startTransaction();

                    bankAccountDAO.update(
                            session,
                            account
                    );

                    Transaction saved =
                            transactionDAO.save(
                                    session,
                                    transaction
                            );

                    session.commitTransaction();

                    return saved;

                } catch (Exception e) {

                    session.abortTransaction();

                    throw new RuntimeException(
                            "Deposit failed. Transaction rolled back.",
                            e
                    );
                }
            }
        }


        // =====================================================
        // POSTGRESQL
        // =====================================================

        try (Connection connection =
                     DatabaseConnection.getConnection()) {

            connection.setAutoCommit(false);

            try {

                bankAccountDAO.update(
                        connection,
                        account
                );

                Transaction saved =
                        transactionDAO.save(
                                connection,
                                transaction
                        );

                connection.commit();

                return saved;

            } catch (Exception e) {

                connection.rollback();



                throw new RuntimeException(
                        "Deposit failed. Transaction rolled back.",
                        e
                );
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Database transaction error.",
                    e
            );
        }
    }


    // =========================================================
    // WITHDRAW
    // =========================================================

    public Transaction withdraw(
            Customer customer,
            Long accountId,
            BigDecimal amount) {

        BankAccount account =
                bankAccountDAO.findById(accountId);

        if (account == null) {
            throw new IllegalArgumentException(
                    "Bank account not found."
            );
        }

        if (account.getOwner() == null ||
                customer == null ||
                !account.getOwner()
                        .getId()
                        .equals(customer.getId())) {

            throw new IllegalArgumentException(
                    "You are not authorized to access this account."
            );
        }

        if (account.getStatus() !=
                AccountStatus.ACTIVE) {

            throw new IllegalArgumentException(
                    "Account is not active."
            );
        }

        if (amount == null ||
                amount.signum() <= 0) {

            throw new IllegalArgumentException(
                    "Withdrawal amount must be greater than zero."
            );
        }

        if (account.getBalance()
                .compareTo(amount) < 0) {

            throw new IllegalArgumentException(
                    "Insufficient balance."
            );
        }

        BigDecimal newBalance =
                account.getBalance()
                        .subtract(amount);

        account.setBalance(newBalance);

        Transaction transaction =
                new Transaction();

        // fix withdraw by id
        transaction.setId(System.currentTimeMillis());

        transaction.setAmount(amount);
        transaction.setDate(
                LocalDateTime.now()
        );
        transaction.setType(
                TransactionType.WITHDRAW
        );
        transaction.setResultingBalance(
                newBalance
        );
        transaction.setAccount(account);


        // =====================================================
        // MONGODB
        // =====================================================

        if (isMongo()) {

            try (ClientSession session =
                         MongoDatabaseConnection
                                 .getClient()
                                 .startSession()) {

                try {

                    session.startTransaction();

                    bankAccountDAO.update(
                            session,
                            account
                    );

                    Transaction saved =
                            transactionDAO.save(
                                    session,
                                    transaction
                            );

                    session.commitTransaction();

                    return saved;

                } catch (Exception e) {

                    session.abortTransaction();

                    throw new RuntimeException(
                            "Withdrawal failed. Transaction rolled back.",
                            e
                    );
                }
            }
        }


        // =====================================================
        // POSTGRESQL
        // =====================================================

        try (Connection connection =
                     DatabaseConnection.getConnection()) {

            connection.setAutoCommit(false);

            try {

                bankAccountDAO.update(
                        connection,
                        account
                );

                Transaction saved =
                        transactionDAO.save(
                                connection,
                                transaction
                        );

                connection.commit();

                return saved;

            } catch (Exception e) {

                connection.rollback();

                throw new RuntimeException(
                        "Withdrawal failed. Transaction rolled back.",
                        e
                );
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Database transaction error.",
                    e
            );
        }
    }


    // =========================================================
    // TRANSFER
    // =========================================================

    public void transfer(
            Customer customer,
            Long sourceAccountId,
            Long destinationAccountId,
            BigDecimal amount) {

        BankAccount source =
                bankAccountDAO.findById(
                        sourceAccountId
                );

        BankAccount destination =
                bankAccountDAO.findById(
                        destinationAccountId
                );

        if (source == null ||
                destination == null) {

            throw new IllegalArgumentException(
                    "Source or destination account not found."
            );
        }

        if (customer == null ||
                source.getOwner() == null ||
                !source.getOwner()
                        .getId()
                        .equals(customer.getId())) {

            throw new IllegalArgumentException(
                    "You are not authorized to transfer from this account."
            );
        }

        if (source.getStatus() !=
                AccountStatus.ACTIVE ||
                destination.getStatus() !=
                        AccountStatus.ACTIVE) {

            throw new IllegalArgumentException(
                    "Both accounts must be active."
            );
        }

        if (sourceAccountId.equals(
                destinationAccountId)) {

            throw new IllegalArgumentException(
                    "Source and destination accounts must be different."
            );
        }

        if (amount == null ||
                amount.signum() <= 0) {

            throw new IllegalArgumentException(
                    "Transfer amount must be greater than zero."
            );
        }

        if (source.getBalance()
                .compareTo(amount) < 0) {

            throw new IllegalArgumentException(
                    "Insufficient balance."
            );
        }

        BigDecimal sourceBalance =
                source.getBalance()
                        .subtract(amount);

        BigDecimal destinationBalance =
                destination.getBalance()
                        .add(amount);

        source.setBalance(sourceBalance);
        destination.setBalance(destinationBalance);


        // =====================================================
        // WITHDRAWAL TRANSACTION
        // =====================================================

        Transaction withdrawal =
                new Transaction();

        // fix get id bugs//

        withdrawal.setId(System.currentTimeMillis());

        withdrawal.setAmount(amount);
        withdrawal.setDate(
                LocalDateTime.now()
        );
        withdrawal.setType(
                TransactionType.TRANSFER
        );
        withdrawal.setResultingBalance(
                sourceBalance
        );
        withdrawal.setAccount(source);


        // =====================================================
        // DEPOSIT TRANSACTION
        // =====================================================

        Transaction deposit =
                new Transaction();

        // fix get id bugs//

        deposit.setId(System.currentTimeMillis());

        deposit.setAmount(amount);
        deposit.setDate(
                LocalDateTime.now()
        );
        deposit.setType(
                TransactionType.TRANSFER
        );
        deposit.setResultingBalance(
                destinationBalance
        );
        deposit.setAccount(destination);


        // =====================================================
        // MONGODB ATOMIC TRANSFER
        // =====================================================

        if (isMongo()) {

            try (ClientSession session =
                         MongoDatabaseConnection
                                 .getClient()
                                 .startSession()) {

                try {

                    session.startTransaction();

                    bankAccountDAO.update(
                            session,
                            source
                    );

                    bankAccountDAO.update(
                            session,
                            destination
                    );

                    transactionDAO.save(
                            session,
                            withdrawal
                    );

                    transactionDAO.save(
                            session,
                            deposit
                    );

                    session.commitTransaction();

                } catch (Exception e) {

                    session.abortTransaction();

                    throw new RuntimeException(
                            "Transfer failed. Everything was rolled back.",
                            e
                    );
                }
            }

            return;
        }


        // =====================================================
        // POSTGRESQL ATOMIC TRANSFER
        // =====================================================

        try (Connection connection =
                     DatabaseConnection.getConnection()) {

            connection.setAutoCommit(false);

            try {

                bankAccountDAO.update(
                        connection,
                        source
                );

                bankAccountDAO.update(
                        connection,
                        destination
                );

                transactionDAO.save(
                        connection,
                        withdrawal
                );

                transactionDAO.save(
                        connection,
                        deposit
                );

                connection.commit();

            } catch (Exception e) {

                connection.rollback();


                throw new RuntimeException(
                        "Transfer failed. Everything was rolled back.",
                        e
                );
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Database transaction error.",
                    e
            );
        }
    }


    // =========================================================
    // CHECK DATABASE TYPE
    // =========================================================

    private boolean isMongo() {

        return bankAccountDAO instanceof MongoBankAccountDAO
                && transactionDAO instanceof MongoTransactionDAO;
    }


    // =========================================================
    // TRANSACTION HISTORY
    // =========================================================

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
                !account.getOwner()
                        .getId()
                        .equals(customer.getId())) {

            throw new IllegalArgumentException(
                    "You are not authorized to view this account."
            );
        }

        return transactionDAO.findByAccountId(
                accountId
        );
    }


    // =========================================================
    // FIND TRANSACTIONS BY TYPE
    // =========================================================

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
                                transaction.getAccount()
                                        .getOwner() != null &&
                                transaction.getAccount()
                                        .getOwner()
                                        .getId()
                                        .equals(
                                                customer.getId()
                                        )
                )
                .toList();
    }


    // =========================================================
    // FIND TRANSACTION BY ID
    // =========================================================

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
                transaction.getAccount()
                        .getOwner() == null ||
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