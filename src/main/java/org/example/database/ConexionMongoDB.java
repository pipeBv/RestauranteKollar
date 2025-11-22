package org.example.database;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class
ConexionMongoDB {

    private static final String CONNECTION_STRING = "mongodb://localhost:27017";
    private static final String DATABASE_NAME = "RestauranteDB";
    private static MongoClient mongoClient = null;
    private static MongoDatabase database = null;

    public static MongoDatabase getDatabase() {
        if (database == null) {
            try {
                CodecRegistry pojoCodecRegistry = fromProviders(PojoCodecProvider.builder().automatic(true).build());
                CodecRegistry customCodecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(), pojoCodecRegistry);
                MongoClientSettings settings = MongoClientSettings.builder().applyConnectionString(new ConnectionString(CONNECTION_STRING)).codecRegistry(customCodecRegistry).build();
                mongoClient = MongoClients.create(settings);
                database = mongoClient.getDatabase(DATABASE_NAME);
                System.out.println("Conectado a la base de datos");
            } catch (Exception e) {
                System.out.println("Error al conectar con la base de datos: " + e.getMessage());
                database = null;
            }
        }
        return database;
    }
    public static void closeConnection() {
        if (mongoClient != null) {
            mongoClient.close();
            mongoClient = null;
            database = null;
            System.out.println("Se cerró la conexión con la base de datos");
        }
    }
}

