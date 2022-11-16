package gg.nils.database.operator.crds.v1alpha1.databaseinstance;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import gg.nils.database.operator.api.DatabaseApi;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.javaoperatorsdk.operator.api.reconciler.Context;
import io.javaoperatorsdk.operator.api.reconciler.ControllerConfiguration;
import io.javaoperatorsdk.operator.api.reconciler.Reconciler;
import io.javaoperatorsdk.operator.api.reconciler.UpdateControl;
import org.bson.BsonDocument;
import org.bson.BsonInt64;
import org.bson.conversions.Bson;

import javax.inject.Inject;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.concurrent.TimeUnit;

import static io.javaoperatorsdk.operator.api.reconciler.Constants.WATCH_ALL_NAMESPACES;

@ControllerConfiguration(namespaces = WATCH_ALL_NAMESPACES)
public class DatabaseInstanceReconciler implements Reconciler<DatabaseInstance> {

    @Inject
    KubernetesClient client;

    @Override
    public UpdateControl<DatabaseInstance> reconcile(DatabaseInstance databaseInstance, Context<DatabaseInstance> context) throws Exception {
        DatabaseInstanceSpec spec = databaseInstance.getSpec();
        String type = spec.getType();
        String uri = spec.getUri();

        boolean couldConnect;

        try {
            switch (type) {
                case "mysql": {
                    Connection connection = DriverManager.getConnection(uri);
                    Statement statement = connection.createStatement();
                    statement.execute("SELECT 1;");
                    statement.close();
                    connection.close();
                    couldConnect = true;
                    break;
                }
                case "mongodb": {
                    MongoClient client = MongoClients.create(uri);
                    MongoDatabase database = client.getDatabase("admin");
                    Bson command = new BsonDocument("ping", new BsonInt64(1));
                    database.runCommand(command);
                    client.close();
                    couldConnect = true;
                    break;
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();

            databaseInstance.setStatus(DatabaseInstanceStatus.builder()
                    .status(false)
                    .message(t.getMessage())
                    .build());

            return UpdateControl.updateStatus(databaseInstance).rescheduleAfter(30, TimeUnit.SECONDS);
        }

        databaseInstance.setStatus(DatabaseInstanceStatus.builder()
                .status(true)
                .build());

        return UpdateControl.updateStatus(databaseInstance).rescheduleAfter(30, TimeUnit.SECONDS);
    }
}
