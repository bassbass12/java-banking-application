package com.bassem.banking.dao;

import com.bassem.banking.Transaction;
import com.bassem.banking.TransactionType;

import java.util.List;

public interface TransactionDAO {

    Transaction save(Transaction transaction);

    Transaction  findById(Long id);

    List<Transaction> findAll();

    void update(Transaction transaction);

    void delete(Long id);

    List<Transaction> findByAccountId(Long accountId);

    List<Transaction> findByType(TransactionType type);
}
