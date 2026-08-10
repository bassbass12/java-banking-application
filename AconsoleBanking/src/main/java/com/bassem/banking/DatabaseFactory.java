package com.bassem.banking;

import com.bassem.banking.dao.BankAccountDAO;
import com.bassem.banking.dao.CustomerDAO;
import com.bassem.banking.dao.TransactionDAO;

import com.bassem.banking.dao.MongoBankAccountDAO;
import com.bassem.banking.dao.MongoCustomerDAO;
import com.bassem.banking.dao.MongoTransactionDAO;

import com.bassem.banking.dao.PostgresBankAccountDAO;
import com.bassem.banking.dao.PostgresCustomerDAO;
import com.bassem.banking.dao.PostgresTransactionDAO;


public class DatabaseFactory {

    public static CustomerDAO getCustomerDAO(DatabaseType type){

        if(type == DatabaseType.MONGO){

            return new MongoCustomerDAO();
        }
        return new PostgresCustomerDAO();
    }


    public static BankAccountDAO getBankAccountDAO(DatabaseType type){

        if(type == DatabaseType.MONGO){

            return new MongoBankAccountDAO();
        }
        return new PostgresBankAccountDAO();
    }

    public static TransactionDAO getTransactionDAO(DatabaseType type){

        if(type == DatabaseType.MONGO){

            return new MongoTransactionDAO();
        }
        return new PostgresTransactionDAO();
    }

}
