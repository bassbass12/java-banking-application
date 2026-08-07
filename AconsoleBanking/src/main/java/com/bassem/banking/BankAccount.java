package com.bassem.banking;

import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;
public class BankAccount {
    private Long id;
    private String accountNumber;
    private AccountType accountType;
    private BigDecimal  balance;
    private AccountStatus status;
    private Customer owner;
    private List<Transaction> transactions = new ArrayList<>();

    public BankAccount(){
    }
    public BankAccount(long id,String accountNumber,AccountType accountType,
                       BigDecimal balance,AccountStatus status,Customer owner){
        this.id=id;
        this.accountNumber=accountNumber;
        this.accountType=accountType;
        this.balance=balance;
        this.status=status;
        this.owner=owner;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public void setStatus(AccountStatus status) {
        this.status = status;
    }

    public Customer getOwner() {
        return owner;
    }

    public void setOwner(Customer owner) {
        this.owner = owner;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }
}