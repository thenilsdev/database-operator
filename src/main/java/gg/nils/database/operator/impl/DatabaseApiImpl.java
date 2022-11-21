package gg.nils.database.operator.impl;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import gg.nils.database.operator.api.DatabaseApi;
import gg.nils.database.operator.api.database.DatabaseInstance;
import gg.nils.database.operator.crds.v1alpha1.databaseinstance.DatabaseInstanceSpec;
import gg.nils.database.operator.impl.database.MongoDBDatabaseInstance;
import gg.nils.database.operator.impl.database.MySQLDatabaseInstance;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.Secret;
import io.fabric8.kubernetes.api.model.SecretKeySelector;
import io.fabric8.kubernetes.client.KubernetesClient;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Base64;

@Singleton
public class DatabaseApiImpl implements DatabaseApi {

    @Inject
    KubernetesClient client;

    @Override
    public DatabaseInstance getDatabaseInstance(gg.nils.database.operator.crds.v1alpha1.databaseinstance.DatabaseInstance databaseInstance) {
        ObjectMeta metadata = databaseInstance.getMetadata();
        DatabaseInstanceSpec spec = databaseInstance.getSpec();

        String uri = null;
        String host = null;
        String database = null;
        String username = null;
        String password = null;

        if (spec.getUri() != null || spec.getUriRef() != null) {
            uri = spec.getUri() != null
                    ? spec.getUri()
                    : resolveSecret(metadata.getNamespace(), spec.getUriRef());
        } else {
            host = spec.getHost() != null
                    ? spec.getHost()
                    : resolveSecret(metadata.getNamespace(), spec.getHostRef());
            database = spec.getDatabase() != null
                    ? spec.getDatabase()
                    : resolveSecret(metadata.getNamespace(), spec.getDatabaseRef());
            username = spec.getUsername() != null
                    ? spec.getUsername()
                    : resolveSecret(metadata.getNamespace(), spec.getUsernameRef());
            password = spec.getPassword() != null
                    ? spec.getPassword()
                    : resolveSecret(metadata.getNamespace(), spec.getPasswordRef());
        }

        try {
            switch (spec.getType()) {
                case "mysql": {
                    Connection connection;

                    if (uri != null) {
                        connection = DriverManager.getConnection(uri);
                    } else {
                        connection = DriverManager.getConnection("jdbc:mysql://" + host + "/" + database, username, password);
                    }

                    return new MySQLDatabaseInstance(connection);
                }
                case "mongodb": {
                    MongoClient client;

                    if (uri != null) {
                        client = MongoClients.create(uri);
                    } else {
                        client = MongoClients.create("mongodb://" + username + ":" + password + "@" + host + "/" + database);
                    }

                    return new MongoDBDatabaseInstance(client);
                }
                default: {
                    throw new IllegalArgumentException("Type " + spec.getType() + " does not exists!");
                }
            }
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    private String resolveSecret(String namespace, SecretKeySelector selector) {
        Secret secret = this.client.secrets()
                .inNamespace(namespace)
                .withName(selector.getName())
                .get();

        String base64Uri = secret.getData().get(selector.getKey());

        return new String(Base64.getDecoder().decode(base64Uri));
    }
}
