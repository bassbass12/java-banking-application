package com.bassem.banking.dao;

import com.bassem.banking.Transaction;
import com.bassem.banking.TransactionType;
import com.mongodb.client.ClientSession;

import java.sql.Connection;
import java.util.List;

public interface TransactionDAO {

    Transaction save(Transaction transaction);

    default Transaction save(
            Connection connection,
            Transaction transaction) {

        throw new UnsupportedOperationException(
                "PostgreSQL transaction save not supported."
        );
    }

    default Transaction save(
            ClientSession session,
            Transaction transaction) {

        throw new UnsupportedOperationException(
                "MongoDB transaction save not supported."
        );
    }

    Transaction findById(Long id);

    List<Transaction> findAll();

    void update(Transaction transaction);

    default void update(
            Connection connection,
            Transaction transaction)
            throws Exception {

        throw new UnsupportedOperationException(
                "PostgreSQL transaction update not supported."
        );
    }

    default void update(
            ClientSession session,
            Transaction transaction) {

        throw new UnsupportedOperationException(
                "MongoDB transaction update not supported."
        );
    }

    void delete(Long id);

    List<Transaction> findByAccountId(Long accountId);

    List<Transaction> findByType(TransactionType type);
}