package com.bassem.banking;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class MongoDatabaseConnection {

    private static final String CONNECTION_STRING =
            "mongodb://localhost:27017/?replicaSet=rs0&retryWrites=false";

    private static final String DATABASE_NAME =
            "banking_db";

    private static final MongoClient mongoClient =
            MongoClients.create(CONNECTION_STRING);

    private MongoDatabaseConnection() {
    }

    public static MongoDatabase getDatabase() {
        return mongoClient.getDatabase(
                DATABASE_NAME
        );
    }

    public static MongoClient getClient() {
        return mongoClient;
    }
}