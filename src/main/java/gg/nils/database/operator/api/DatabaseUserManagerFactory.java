package gg.nils.database.operator.api;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import gg.nils.database.operator.api.impl.MongoDBDatabaseUserManager;
import gg.nils.database.operator.api.impl.MySQLDatabaseUserManager;
import gg.nils.database.operator.crds.v1alpha1.databaseuser.DatabaseUser;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseUserManagerFactory {

    public static DatabaseUserManager get(DatabaseUser resource) {
        try {
            switch (resource.getSpec().getConnection().getType()) {
                case "mysql":
                    Connection connection = DriverManager.getConnection(resource.getSpec().getConnection().getUri());
                    return new MySQLDatabaseUserManager(connection);
                case "mongodb":
                    MongoClient mongoClient = MongoClients.create(resource.getSpec().getConnection().getUri());
                    return new MongoDBDatabaseUserManager(mongoClient);
                default:
                    throw new IllegalArgumentException("Type " + resource.getSpec().getConnection().getType() + " does not exists!");
            }
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }
}
