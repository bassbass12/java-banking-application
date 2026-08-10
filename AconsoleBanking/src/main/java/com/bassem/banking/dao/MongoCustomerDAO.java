package com.bassem.banking.dao;

import com.bassem.banking.Customer;
import com.bassem.banking.MongoDatabaseConnection;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;


public class MongoCustomerDAO implements CustomerDAO {
    @Override
    public Customer save(Customer customer) {
        MongoDatabase Db = MongoDatabaseConnection.getDatabase();
        MongoCollection<Document> collection =
                Db.getCollection("customers");
        Document document = new Document("id", customer.getId())
                .append("name", customer.getName())
                .append("email", customer.getEmail())
                .append("passwordHash", customer.getPasswordHash());
        collection.insertOne(document);
        return customer;
    }

    @Override
    public Customer findById(Long id) {
        MongoDatabase Db = MongoDatabaseConnection.getDatabase();
        MongoCollection<Document> collection =
                Db.getCollection("customers");
        Document document = collection.find(
                new Document("id", id)
        ).first();
        if (document == null) {
            return null;
        }
        Customer customer = new Customer();
        customer.setId(document.getLong("id"));
        customer.setName(document.getString("name"));
        customer.setEmail(document.getString("email"));
        customer.setPasswordHash(document.getString("passwordHash")

        );
        return customer;
    }

    @Override
    public Customer findByEmail(String email) {
        MongoDatabase Db = MongoDatabaseConnection.getDatabase();
        MongoCollection<Document> collection =
                Db.getCollection("customers");

        Document document = collection.find(
                new Document("email", email)
        ).first();

        if (document == null) {
            return null;
        }

        return new Customer(
                document.getLong("id"),
                document.getString("name"),
                document.getString("email"),
                document.getString("passwordHash")
        );


    }

    @Override
    public List<Customer> findAll() {
        MongoDatabase Db = MongoDatabaseConnection.getDatabase();
        MongoCollection<Document> collection =
                Db.getCollection("customers");
        List<Customer> customers = new ArrayList<>();
        for (Document document : collection.find()) {
            Customer customer = new Customer();
            customer.setId(document.getLong("id"));
            customer.setName(document.getString("name"));
            customer.setEmail(document.getString("email"));
            customer.setPasswordHash(document.getString("passwordHash")
            );

            customers.add(customer);
        }
        return customers;
    }

    @Override
    public void update(Customer customer) {
        MongoDatabase Db = MongoDatabaseConnection.getDatabase();
        MongoCollection<Document> collection =
                Db.getCollection("customers");

        Document update = new Document("$set",
                new Document("name", customer.getName())
                        .append("email", customer.getEmail())
                        .append("passwordHash", customer.getPasswordHash())
        );
        collection.updateOne(
                new Document("id", customer.getId()),
                update
        );

    }

    @Override
    public void delete(Long id) {
        MongoDatabase database = MongoDatabaseConnection.getDatabase();

        MongoCollection<Document> collection =
                database.getCollection("customers");

        collection.deleteOne(
                new Document("id", id)
        );


    }
}
