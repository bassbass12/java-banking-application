package com.bassem.banking;
import java.util.ArrayList;
import java.util.List;
public class Customer {
    private Long id;
    private String name;
    private String email;
    private String passwordHash;
    private List<BankAccount> accounts;
   public Customer(){

   }
   public Customer(Long id,String name,String email,String passwordHash){
       this.id =id;
       this.name=name;
       this.email=email;
       this.passwordHash=passwordHash;
   }
    /*public List<BankAccount> getAccounts() {
        return accounts;
    }*/

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public List<BankAccount> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<BankAccount> accounts) {
        this.accounts = accounts;
    }
}
