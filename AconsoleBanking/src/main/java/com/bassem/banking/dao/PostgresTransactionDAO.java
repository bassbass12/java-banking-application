package com.bassem.banking.dao;

import com.bassem.banking.AccountStatus;
import com.bassem.banking.AccountType;
import com.bassem.banking.BankAccount;
import com.bassem.banking.DatabaseConnection;
import com.bassem.banking.Transaction;
import com.bassem.banking.TransactionType;

import java.sql.*;
import java.util.List;
import java.util.ArrayList;


public class PostgresTransactionDAO implements TransactionDAO
{
    @Override
    public Transaction save(Transaction transaction) {

        String sql = """
                INSERT INTO transactions
                (id, amount, transaction_date, transaction_type,
                 resulting_balance, account_id)
                VALUES (?, ?, ?, ?, ?, ?)
                """;
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, transaction.getId());
            ps.setBigDecimal(2, transaction.getAmount());
            ps.setTimestamp(3, Timestamp.valueOf(transaction.getDate()));
            ps.setString(4, transaction.getType().name());
            ps.setBigDecimal(5, transaction.getResultingBalance());
            ps.setLong(6, transaction.getAccount().getId());

            ps.executeUpdate();
            return transaction;

        } catch (SQLException e) {
            throw new RuntimeException("Error saving Transaction", e);

        }


    }


    //===============================================================

    @Override
    public Transaction findById(Long id) {

        String sql = """
                SELECT
                    t.id,
                    t.amount,
                    t.transaction_date,
                    t.transaction_type,
                    t.resulting_balance,
                    b.id AS account_id,
                    b.account_number,
                    b.account_type,
                    b.balance,
                    b.status,
                    b.customer_id
                FROM transactions t
                JOIN bank_accounts b
                    ON t.account_id = b.id
                WHERE t.id = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    // Create BankAccount object

                    BankAccount account = new BankAccount();

                    account.setId(rs.getLong("account_id"));

                    account.setAccountNumber(
                            rs.getString("account_number")
                    );

                    account.setAccountType(
                            AccountType.valueOf(
                                    rs.getString("account_type")
                            )
                    );
                    account.setBalance(
                            rs.getBigDecimal("balance")
                    );

                    account.setStatus(
                            AccountStatus.valueOf(
                                    rs.getString("status")
                            )
                    );
                    // Create BankAccount object

                    Transaction transaction = new Transaction();

                    transaction.setId(rs.getLong("id"));

                    transaction.setAmount(
                            rs.getBigDecimal("amount")
                    );

                    transaction.setDate(
                            rs.getTimestamp("transaction_date")
                                    .toLocalDateTime()
                    );

                    transaction.setType(
                            TransactionType.valueOf(
                                    rs.getString("transaction_type")
                            )
                    );

                    transaction.setResultingBalance(
                            rs.getBigDecimal("resulting_balance")
                    );

                    // Connect Transaction BankAccount

                    transaction.setAccount(account);

                    return transaction;
                }

                return null;
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Error finding transaction", e
            );
        }
    }

    //=============================

    @Override
    public List<Transaction> findAll() {

        String sql = """
        SELECT
            t.id,
            t.amount,
            t.transaction_date,
            t.transaction_type,
            t.resulting_balance,
            b.id AS account_id,
            b.account_number,
            b.account_type,
            b.balance,
            b.status,
            b.customer_id
        FROM transactions t
        JOIN bank_accounts b
            ON t.account_id = b.id
        """;

        List<Transaction> transactions = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                // Create BankAccount
                BankAccount account = new BankAccount();

                account.setId(
                        rs.getLong("account_id")
                );

                account.setAccountNumber(
                        rs.getString("account_number")
                );

                account.setAccountType(
                        AccountType.valueOf(
                                rs.getString("account_type")
                        )
                );

                account.setBalance(
                        rs.getBigDecimal("balance")
                );

                account.setStatus(
                        AccountStatus.valueOf(
                                rs.getString("status")
                        )
                );

                // Create Transaction
                Transaction transaction = new Transaction();

                transaction.setId(
                        rs.getLong("id")
                );

                transaction.setAmount(
                        rs.getBigDecimal("amount")
                );

                transaction.setDate(
                        rs.getTimestamp("transaction_date")
                                .toLocalDateTime()
                );

                transaction.setType(
                        TransactionType.valueOf(
                                rs.getString("transaction_type")
                        )
                );

                transaction.setResultingBalance(
                        rs.getBigDecimal("resulting_balance")
                );

                // Connect Transaction to BankAccount
                transaction.setAccount(account);

                transactions.add(transaction);
            }

            return transactions;

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Error finding transactions", e
            );
        }
    }

    //==========

    @Override
    public void update(Transaction transaction) {

        String sql = """
        UPDATE transactions
        SET amount = ?,
            transaction_date = ?,
            transaction_type = ?,
            resulting_balance = ?,
            account_id = ?
        WHERE id = ?
        """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setBigDecimal(1,
                    transaction.getAmount()
            );

            ps.setTimestamp(2,
                    Timestamp.valueOf(transaction.getDate())
            );

            ps.setString(3,
                    transaction.getType().name()
            );

            ps.setBigDecimal(4,
                    transaction.getResultingBalance()
            );

            ps.setLong(5,
                    transaction.getAccount().getId()
            );

            ps.setLong(6,
                    transaction.getId()
            );

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Error updating transaction", e
            );
        }
    }

    //=====
    @Override
    public void delete(Long id) {

        String sql = "DELETE FROM transactions WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, id);

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Error deleting transaction", e
            );
        }
    }

    @Override
    public List<Transaction> findByAccountId(Long accountId) {
        return List.of();
    }

    @Override
    public List<Transaction> findByType(TransactionType type) {
        return List.of();
    }


}

