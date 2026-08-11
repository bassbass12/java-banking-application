package com.bassem.banking.dao;

import com.bassem.banking.BankAccount;
import com.bassem.banking.MongoDatabaseConnection;
import com.bassem.banking.Transaction;
import com.bassem.banking.TransactionType;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;
import org.bson.types.Decimal128;

import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MongoTransactionDAO implements TransactionDAO {

    @Override
    public Transaction save(Transaction transaction) {

        MongoDatabase database =
                MongoDatabaseConnection.getDatabase();

        MongoCollection<Document> collection =
                database.getCollection("transactions");

        Document document =
                createDocument(transaction);

        collection.insertOne(document);

        return transaction;
    }


    // =========================================================
    // MongoDB ATOMIC TRANSACTION SAVE
    // =========================================================

    @Override
    public Transaction save(
            ClientSession session,
            Transaction transaction) {

        MongoDatabase database =
                MongoDatabaseConnection.getDatabase();

        MongoCollection<Document> collection =
                database.getCollection("transactions");

        Document document =
                createDocument(transaction);

        collection.insertOne(
                session,
                document
        );

        return transaction;
    }


    private Document createDocument(
            Transaction transaction) {

        return new Document(
                "id",
                transaction.getId()
        )
                .append(
                        "amount",
                        new Decimal128(
                                transaction.getAmount()
                        )
                )
                .append(
                        "transaction_Date",
                        Date.from(
                                transaction.getDate()
                                        .toInstant(
                                                ZoneOffset.UTC
                                        )
                        )
                )
                .append(
                        "transaction_Type",
                        transaction.getType().name()
                )
                .append(
                        "resulting_Balance",
                        new Decimal128(
                                transaction
                                        .getResultingBalance()
                        )
                )
                .append(
                        "accountId",
                        transaction
                                .getAccount()
                                .getId()
                );
    }


    @Override
    public Transaction findById(Long id) {

        MongoDatabase database =
                MongoDatabaseConnection.getDatabase();

        MongoCollection<Document> collection =
                database.getCollection("transactions");

        Document document =
                collection.find(
                        new Document("id", id)
                ).first();

        if (document == null) {
            return null;
        }

        return toTransaction(document);
    }


    @Override
    public List<Transaction> findAll() {

        MongoDatabase database =
                MongoDatabaseConnection.getDatabase();

        MongoCollection<Document> collection =
                database.getCollection("transactions");

        List<Transaction> transactions =
                new ArrayList<>();

        for (Document document :
                collection.find()) {

            transactions.add(
                    toTransaction(document)
            );
        }

        return transactions;
    }


    @Override
    public void update(Transaction transaction) {

        MongoDatabase database =
                MongoDatabaseConnection.getDatabase();

        MongoCollection<Document> collection =
                database.getCollection("transactions");

        collection.updateOne(
                new Document(
                        "id",
                        transaction.getId()
                ),
                createUpdate(transaction)
        );
    }


    // =========================================================
    // MongoDB ATOMIC TRANSACTION UPDATE
    // =========================================================

    @Override
    public void update(
            ClientSession session,
            Transaction transaction) {

        MongoDatabase database =
                MongoDatabaseConnection.getDatabase();

        MongoCollection<Document> collection =
                database.getCollection("transactions");

        collection.updateOne(
                session,
                new Document(
                        "id",
                        transaction.getId()
                ),
                createUpdate(transaction)
        );
    }


    private Document createUpdate(
            Transaction transaction) {

        return new Document(
                "$set",
                new Document(
                        "amount",
                        new Decimal128(
                                transaction.getAmount()
                        )
                )
                        .append(
                                "transaction_Date",
                                Date.from(
                                        transaction.getDate()
                                                .toInstant(
                                                        ZoneOffset.UTC
                                                )
                                )
                        )
                        .append(
                                "transaction_Type",
                                transaction.getType().name()
                        )
                        .append(
                                "resulting_Balance",
                                new Decimal128(
                                        transaction
                                                .getResultingBalance()
                                )
                        )
                        .append(
                                "accountId",
                                transaction
                                        .getAccount()
                                        .getId()
                        )
        );
    }


    @Override
    public void delete(Long id) {

        MongoDatabase database =
                MongoDatabaseConnection.getDatabase();

        MongoCollection<Document> collection =
                database.getCollection("transactions");

        collection.deleteMany(
                new Document("id", id)
        );
    }


    @Override
    public List<Transaction> findByAccountId(
            Long accountId) {

        MongoDatabase database =
                MongoDatabaseConnection.getDatabase();

        MongoCollection<Document> collection =
                database.getCollection("transactions");

        List<Transaction> transactions =
                new ArrayList<>();

        for (Document document :
                collection.find(
                        new Document(
                                "accountId",
                                accountId
                        )
                )) {

            transactions.add(
                    toTransaction(document)
            );
        }

        return transactions;
    }


    @Override
    public List<Transaction> findByType(
            TransactionType type) {

        MongoDatabase database =
                MongoDatabaseConnection.getDatabase();

        MongoCollection<Document> collection =
                database.getCollection("transactions");

        List<Transaction> transactions =
                new ArrayList<>();

        for (Document document :
                collection.find(
                        new Document(
                                "transaction_Type",
                                type.name()
                        )
                )) {

            transactions.add(
                    toTransaction(document)
            );
        }

        return transactions;
    }


    private Transaction toTransaction(
            Document document) {

        Transaction transaction =
                new Transaction();

        transaction.setId(
                document.getLong("id")
        );

        Decimal128 amount =
                document.get(
                        "amount",
                        Decimal128.class
                );

        transaction.setAmount(
                amount.bigDecimalValue()
        );

        Date date =
                document.getDate(
                        "transaction_Date"
                );

        transaction.setDate(
                date.toInstant()
                        .atZone(ZoneOffset.UTC)
                        .toLocalDateTime()
        );

        transaction.setType(
                TransactionType.valueOf(
                        document.getString(
                                "transaction_Type"
                        )
                )
        );

        Decimal128 resultingBalance =
                document.get(
                        "resulting_Balance",
                        Decimal128.class
                );

        transaction.setResultingBalance(
                resultingBalance.bigDecimalValue()
        );

        Long accountId =
                document.getLong("accountId");

        BankAccount account =
                new BankAccount();

        account.setId(accountId);

        transaction.setAccount(account);

        return transaction;
    }
}