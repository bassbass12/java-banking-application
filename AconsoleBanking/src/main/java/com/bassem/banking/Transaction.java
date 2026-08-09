package com.bassem.banking;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Transaction {
    private Long id;
    private BigDecimal amount;
    private LocalDateTime date;
    private TransactionType type;
    private BigDecimal resultingBalance;
    private BankAccount account;
    public Transaction(){

    }

    public Transaction(long id, BigDecimal amount, LocalDateTime date,
                       TransactionType type, BigDecimal resultingBalance){
        this.id = id;
        this.amount = amount;
        this.date = date;
        this.type = type;
        this.resultingBalance = resultingBalance;


    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public BigDecimal getResultingBalance() {
        return resultingBalance;
    }

    public void setResultingBalance(BigDecimal resultingBalance) {
        this.resultingBalance = resultingBalance;
    }

    public BankAccount getAccount() {
        return account;
    }

    public void setAccount(BankAccount account) {
        this.account = account;
    }


}