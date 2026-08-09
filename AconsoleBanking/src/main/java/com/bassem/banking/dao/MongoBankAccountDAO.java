package com.bassem.banking.dao;

import com.bassem.banking.AccountStatus;
import com.bassem.banking.AccountType;
import com.bassem.banking.BankAccount;
import com.bassem.banking.Customer;
import com.bassem.banking.MongoDatabaseConnection;
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
                .append("account_Number", account.getAccountNumber())
                .append("accountType", account.getAccountType().name())
                .append("balance", account.getBalance())
                .append("status", account.getStatus().name())
                .append("customerId", account.getOwner().getId());

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

        BankAccount account = new BankAccount();

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

        account.setBalance(
                document.get("balance", Decimal128.class)
                        .bigDecimalValue()
        );

        account.setStatus(
                AccountStatus.valueOf(
                        document.getString("status")
                )
        );

        // Get customerId from MongoDB
        Long customerId =
                document.getLong("customerId");

        // For now, create Customer with its ID
        Customer customer = new Customer();
        customer.setId(customerId);

        account.setOwner(customer);

        return account;
    }


    @Override
    public List<BankAccount> findAll() {

        MongoDatabase database =
                MongoDatabaseConnection.getDatabase();

        MongoCollection<Document> collection =
                database.getCollection("bank_accounts");

        List<BankAccount> accounts = new ArrayList<>();

        for (Document document : collection.find()) {

            BankAccount account = new BankAccount();

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

            account.setBalance(
                    document.get("balance", Decimal128.class)
                            .bigDecimalValue()
            );

            account.setStatus(
                    AccountStatus.valueOf(
                            document.getString("status")
                    )
            );

            Long customerId =
                    document.getLong("customerId");

            Customer customer = new Customer();
            customer.setId(customerId);

            account.setOwner(customer);

            accounts.add(account);
        }

        return accounts;
    }


    @Override
    public void update(BankAccount account) {

        MongoDatabase database =
                MongoDatabaseConnection.getDatabase();

        MongoCollection<Document> collection =
                database.getCollection("bank_accounts");

        Document update = new Document("$set",
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
                                account.getBalance()
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

        collection.updateOne(
                new Document("id", account.getId()),
                update
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
}