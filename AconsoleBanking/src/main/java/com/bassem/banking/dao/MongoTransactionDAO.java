package com.bassem.banking.dao;

import com.bassem.banking.BankAccount;
import com.bassem.banking.MongoDatabaseConnection;
import com.bassem.banking.Transaction;
import com.bassem.banking.TransactionType;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;
import org.bson.types.Decimal128;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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

        Document document = new Document("id", transaction.getId())
                .append("amount",
                        new Decimal128(transaction.getAmount()))
                .append("transaction_Date",
                        Date.from(
                                transaction.getDate()
                                        .toInstant(ZoneOffset.UTC)
                        ))
                .append("transaction_Type",
                        transaction.getType().name())
                .append("resulting_Balance",
                        new Decimal128(
                                transaction.getResultingBalance()
                        ))
                .append("accountId",
                        transaction.getAccount().getId());

        collection.insertOne(document);

        return transaction;
    }



    @Override
    public Transaction findById(Long id) {

        MongoDatabase database =
                MongoDatabaseConnection.getDatabase();

        MongoCollection<Document> collection =
                database.getCollection("transactions");

        Document document = collection.find(
                new Document("id", id)
        ).first();

        if (document == null) {
            return null;
        }

        Transaction transaction = new Transaction();

        transaction.setId(
                document.getLong("id")
        );

        Decimal128 amount =
                document.get("amount", Decimal128.class);

        transaction.setAmount(
                amount.bigDecimalValue()
        );

        Date date =
                document.getDate("transaction_Date");

        transaction.setDate(
                date.toInstant()
                        .atZone(ZoneOffset.UTC)
                        .toLocalDateTime()
        );

        transaction.setType(
                TransactionType.valueOf(
                        document.getString("transaction_Type")
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

        BankAccount account = new BankAccount();
        account.setId(accountId);

        transaction.setAccount(account);

        return transaction;
    }


    @Override
    public List<Transaction> findAll() {

        MongoDatabase database =
                MongoDatabaseConnection.getDatabase();

        MongoCollection<Document> collection =
                database.getCollection("transactions");

        List<Transaction> transactions =
                new ArrayList<>();

        for (Document document : collection.find()) {

            Transaction transaction = new Transaction();

            transaction.setId(
                    document.getLong("id")
            );

            Decimal128 amount =
                    document.get("amount", Decimal128.class);

            transaction.setAmount(
                    amount.bigDecimalValue()
            );

            Date date =
                    document.getDate("transaction_Date");

            transaction.setDate(
                    date.toInstant()
                            .atZone(ZoneOffset.UTC)
                            .toLocalDateTime()
            );

            transaction.setType(
                    TransactionType.valueOf(
                            document.getString("transaction_Type")
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

            BankAccount account = new BankAccount();
            account.setId(accountId);

            transaction.setAccount(account);

            transactions.add(transaction);
        }

        return transactions;
    }



    @Override
    public void update(Transaction transaction) {

        MongoDatabase database =
                MongoDatabaseConnection.getDatabase();

        MongoCollection<Document> collection =
                database.getCollection("transactions");

        Document update = new Document("$set",
                new Document(
                        "amount",
                        new Decimal128(transaction.getAmount())
                )
                        .append(
                                "transaction_Date",
                                Date.from(
                                        transaction.getDate()
                                                .toInstant(ZoneOffset.UTC)
                                )
                        )
                        .append(
                                "transaction_Type",
                                transaction.getType().name()
                        )
                        .append(
                                "resulting_Balance",
                                new Decimal128(
                                        transaction.getResultingBalance()
                                )
                        )
                        .append(
                                "accountId",
                                transaction.getAccount().getId()
                        )
        );

        collection.updateOne(
                new Document("id", transaction.getId()),
                update
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
    public List<Transaction> findByAccountId(Long accountId) {

        MongoDatabase database =
                MongoDatabaseConnection.getDatabase();

        MongoCollection<Document> collection =
                database.getCollection("transactions");

        List<Transaction> transactions = new ArrayList<>();

        for (Document document : collection.find(
                new Document("accountId", accountId)
        )) {

            Transaction transaction = new Transaction();

            transaction.setId(
                    document.getLong("id")
            );

            Decimal128 amount =
                    document.get("amount", Decimal128.class);

            transaction.setAmount(
                    amount.bigDecimalValue()
            );

            Date date =
                    document.getDate("transaction_Date");

            transaction.setDate(
                    date.toInstant()
                            .atZone(ZoneOffset.UTC)
                            .toLocalDateTime()
            );

            transaction.setType(
                    TransactionType.valueOf(
                            document.getString("transaction_Type")
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

            BankAccount account = new BankAccount();
            account.setId(accountId);

            transaction.setAccount(account);

            transactions.add(transaction);
        }

        return transactions;
    }

    @Override
    public List<Transaction> findByType(TransactionType type) {

        MongoDatabase database =
                MongoDatabaseConnection.getDatabase();

        MongoCollection<Document> collection =
                database.getCollection("transactions");

        List<Transaction> transactions = new ArrayList<>();

        for (Document document : collection.find(
                new Document("transaction_Type", type.name())
        )) {

            Transaction transaction = new Transaction();

            transaction.setId(
                    document.getLong("id")
            );

            Decimal128 amount =
                    document.get("amount", Decimal128.class);

            transaction.setAmount(
                    amount.bigDecimalValue()
            );

            Date date =
                    document.getDate("transaction_Date");

            transaction.setDate(
                    date.toInstant()
                            .atZone(ZoneOffset.UTC)
                            .toLocalDateTime()
            );

            transaction.setType(
                    TransactionType.valueOf(
                            document.getString("transaction_Type")
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

            BankAccount account = new BankAccount();
            account.setId(accountId);

            transaction.setAccount(account);

            transactions.add(transaction);
        }

        return transactions;
    }

}