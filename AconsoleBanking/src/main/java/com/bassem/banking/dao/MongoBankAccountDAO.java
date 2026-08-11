package com.bassem.banking.dao;

import com.bassem.banking.AccountStatus;
import com.bassem.banking.AccountType;
import com.bassem.banking.BankAccount;
import com.bassem.banking.Customer;
import com.bassem.banking.MongoDatabaseConnection;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;
import org.bson.types.Decimal128;

import java.util.ArrayList;
import java.util.List;

public class MongoBankAccountDAO implements BankAccountDAO {

    @Override
    public BankAccount save(BankAccount account) {

        MongoDatabase database =
                MongoDatabaseConnection.getDatabase();

        MongoCollection<Document> collection =
                database.getCollection("bank_accounts");

        Document document = new Document("id", account.getId())
                .append(
                        "account_Number",
                        account.getAccountNumber()
                )
                .append(
                        "accountType",
                        account.getAccountType().name()
                )
                .append(
                        "balance",
                        new Decimal128(account.getBalance())
                )
                .append(
                        "status",
                        account.getStatus().name()
                )
                .append(
                        "customerId",
                        account.getOwner().getId()
                );

        collection.insertOne(document);

        return account;
    }


    @Override
    public BankAccount findById(Long id) {

        MongoDatabase database =
                MongoDatabaseConnection.getDatabase();

        MongoCollection<Document> collection =
                database.getCollection("bank_accounts");

        Document document = collection.find(
                new Document("id", id)
        ).first();

        if (document == null) {
            return null;
        }

        return toBankAccount(document);
    }


    @Override
    public List<BankAccount> findAll() {

        MongoDatabase database =
                MongoDatabaseConnection.getDatabase();

        MongoCollection<Document> collection =
                database.getCollection("bank_accounts");

        List<BankAccount> accounts =
                new ArrayList<>();

        for (Document document : collection.find()) {
            accounts.add(toBankAccount(document));
        }

        return accounts;
    }


    @Override
    public void update(BankAccount account) {

        MongoDatabase database =
                MongoDatabaseConnection.getDatabase();

        MongoCollection<Document> collection =
                database.getCollection("bank_accounts");

        collection.updateOne(
                new Document("id", account.getId()),
                createUpdate(account)
        );
    }


    // =========================================================
    // MongoDB ATOMIC TRANSACTION UPDATE
    // =========================================================

    @Override
    public void update(
            ClientSession session,
            BankAccount account) {

        MongoDatabase database =
                MongoDatabaseConnection.getDatabase();

        MongoCollection<Document> collection =
                database.getCollection("bank_accounts");

        collection.updateOne(
                session,
                new Document("id", account.getId()),
                createUpdate(account)
        );
    }


    private Document createUpdate(BankAccount account) {

        return new Document(
                "$set",
                new Document(
                        "account_Number",
                        account.getAccountNumber()
                )
                        .append(
                                "accountType",
                                account.getAccountType().name()
                        )
                        .append(
                                "balance",
                                new Decimal128(
                                        account.getBalance()
                                )
                        )
                        .append(
                                "status",
                                account.getStatus().name()
                        )
                        .append(
                                "customerId",
                                account.getOwner().getId()
                        )
        );
    }


    @Override
    public void delete(Long id) {

        MongoDatabase database =
                MongoDatabaseConnection.getDatabase();

        MongoCollection<Document> collection =
                database.getCollection("bank_accounts");

        collection.deleteMany(
                new Document("id", id)
        );
    }


    private BankAccount toBankAccount(
            Document document) {

        BankAccount account =
                new BankAccount();

        account.setId(
                document.getLong("id")
        );

        account.setAccountNumber(
                document.getString("account_Number")
        );

        account.setAccountType(
                AccountType.valueOf(
                        document.getString("accountType")
                )
        );

        Decimal128 balance =
                document.get(
                        "balance",
                        Decimal128.class
                );

        account.setBalance(
                balance.bigDecimalValue()
        );

        account.setStatus(
                AccountStatus.valueOf(
                        document.getString("status")
                )
        );

        Long customerId =
                document.getLong("customerId");

        Customer customer =
                new Customer();

        customer.setId(customerId);

        account.setOwner(customer);

        return account;
    }
}