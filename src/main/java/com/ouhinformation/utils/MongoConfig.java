package com.ouhinformation.utils;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class MongoConfig {
    // Hardcoded connection URI per user request
    private static final String CONNECTION_STRING = "mongodb://localhost:27017";
    private static final String DATABASE_NAME = "ouh_information_db";
    
    private static MongoClient mongoClient = null;
    private static MongoDatabase mongoDatabase = null;

    private MongoConfig() {}

    public static synchronized MongoDatabase getDatabase() {
        if (mongoDatabase == null) {
            mongoClient = MongoClients.create(CONNECTION_STRING);
            mongoDatabase = mongoClient.getDatabase(DATABASE_NAME);
            System.out.println("Connected to MongoDB database: " + DATABASE_NAME);
        }
        return mongoDatabase;
    }

    public static synchronized void closeConnection() {
        if (mongoClient != null) {
            mongoClient.close();
        }
    }
}
