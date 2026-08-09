package com.bassem.banking.dao;

import com.bassem.banking.BankAccount;
import java.util.List;

public interface BankAccountDAO {
 BankAccount save(BankAccount account);

 BankAccount findById(Long id);

 List<BankAccount> findAll();

 void update(BankAccount account);

 void delete (Long id);
}
