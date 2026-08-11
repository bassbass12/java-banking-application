package com.bassem.banking.dao;

import com.bassem.banking.BankAccount;
import com.mongodb.client.ClientSession;

import java.sql.Connection;
import java.util.List;

public interface BankAccountDAO {

 BankAccount save(BankAccount account);

 BankAccount findById(Long id);

 List<BankAccount> findAll();

 void update(BankAccount account);

 default void update(
         Connection connection,
         BankAccount account)
         throws Exception {

  throw new UnsupportedOperationException(
          "PostgreSQL transaction update not supported."
  );
 }

 default void update(
         ClientSession session,
         BankAccount account) {

  throw new UnsupportedOperationException(
          "MongoDB transaction update not supported."
  );
 }

 void delete(Long id);
}