package gg.nils.database.operator.impl.database;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import gg.nils.database.operator.api.database.DatabaseInstance;
import org.bson.Document;

import java.util.Collections;
import java.util.Set;

public class MongoDBDatabaseInstance implements DatabaseInstance {

    private final MongoClient client;

    public MongoDBDatabaseInstance(MongoClient client) {
        this.client = client;
    }

    @Override
    public void createOrUpdate(String database, String username, String password) {
        MongoDatabase adminDatabase = this.client.getDatabase("admin");
        MongoCollection<Document> userCollection = adminDatabase.getCollection("system.users");

        Document user = userCollection.find(Filters.and(
                Filters.eq("user", username),
                Filters.eq("db", database)
        )).first();

        Set<BasicDBObject> roles = Collections.singleton(
                new BasicDBObject("role", "readWrite")
                        .append("db", database)
        );

        MongoDatabase db = this.client.getDatabase(database);

        if (user == null) {
            db.runCommand(new BasicDBObject("createUser", username)
                    .append("pwd", password)
                    .append("roles", roles)
            );
        } else {
            db.runCommand(new BasicDBObject("updateUser", username)
                    .append("pwd", password)
                    .append("roles", roles)
            );
        }
    }

    @Override
    public void close() {
        this.client.close();
    }
}
