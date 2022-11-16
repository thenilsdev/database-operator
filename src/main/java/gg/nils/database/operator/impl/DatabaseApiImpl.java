package gg.nils.database.operator.impl;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import gg.nils.database.operator.api.DatabaseApi;
import gg.nils.database.operator.api.database.DatabaseInstance;
import gg.nils.database.operator.impl.database.MongoDBDatabaseInstance;
import gg.nils.database.operator.impl.database.MySQLDatabaseInstance;

import javax.inject.Singleton;
import java.sql.Connection;
import java.sql.DriverManager;

@Singleton
public class DatabaseApiImpl implements DatabaseApi {

    @Override
    public DatabaseInstance getDatabaseInstance(String type, String uri) {
        try {
            switch (type) {
                case "mysql": {
                    Connection connection = DriverManager.getConnection(uri);
                    return new MySQLDatabaseInstance(connection);
                }
                case "mongodb": {
                    MongoClient client = MongoClients.create(uri);
                    return new MongoDBDatabaseInstance(client);
                }
                default: {
                    throw new IllegalArgumentException("Type " + type + " does not exists!");
                }
            }
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }
}
