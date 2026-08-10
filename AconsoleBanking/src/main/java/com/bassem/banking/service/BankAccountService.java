package com.bassem.banking.service;

import com.bassem.banking.BankAccount;
import com.bassem.banking.AccountStatus;
import com.bassem.banking.dao.BankAccountDAO;

import java.util.List;

public class BankAccountService {

    private final BankAccountDAO bankAccountDAO;

    public BankAccountService(BankAccountDAO bankAccountDAO) {
        this.bankAccountDAO = bankAccountDAO;
    }

    // 1 ---------- Open Account ----------

    public BankAccount openAccount(BankAccount account) {

        if (account == null) {
            throw new IllegalArgumentException("Account cannot be null.");
        }

        if (account.getAccountNumber() == null ||
                account.getAccountNumber().isBlank()) {
            throw new IllegalArgumentException(
                    "Account number cannot be empty."
            );
        }

        if (account.getAccountType() == null) {
            throw new IllegalArgumentException(
                    "Account type is required."
            );
        }

        if (account.getBalance() == null) {
            throw new IllegalArgumentException(
                    "Balance cannot be null."
            );
        }

        if (account.getBalance().signum() < 0) {
            throw new IllegalArgumentException(
                    "Opening balance cannot be negative."
            );
        }

        if (account.getOwner() == null) {
            throw new IllegalArgumentException(
                    "Account must have an owner."
            );
        }

        account.setStatus(AccountStatus.ACTIVE);

        return bankAccountDAO.save(account);
    }


    // ---------- Close Account ----------

    public void closeAccount(Long id) {

        BankAccount account = bankAccountDAO.findById(id);

        if (account == null) {
            throw new IllegalArgumentException(
                    "Bank account not found."
            );
        }

        if (account.getStatus() == AccountStatus.CLOSED) {
            throw new IllegalArgumentException(
                    "Account is already closed."
            );
        }

        if (account.getBalance() == null ||
                account.getBalance().signum() != 0) {
            throw new IllegalArgumentException(
                    "Account must have a zero balance before closing."
            );
        }

        // Do NOT delete the account.
        // Transactions must remain available as history.

        account.setStatus(AccountStatus.CLOSED);

        bankAccountDAO.update(account);


    }

// ---------- Find Account By ID----------

    public BankAccount findAccountById(Long id) {
           return bankAccountDAO.findById(id);

    }

    public List<BankAccount> findAllAccounts() {
           return bankAccountDAO.findAll();
    }


    public void updateAccount(BankAccount account) {

        if(account == null){
            throw new IllegalArgumentException(
                    "Account cant be null"
            );
        }
        bankAccountDAO.update(account);
    }
}


