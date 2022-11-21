package gg.nils.database.operator.crds.v1alpha1.databaseuser;

import gg.nils.database.operator.api.DatabaseApi;
import gg.nils.database.operator.crds.v1alpha1.databaseinstance.DatabaseInstance;
import gg.nils.database.operator.crds.v1alpha1.databaseinstance.DatabaseInstanceSpec;
import io.fabric8.kubernetes.api.model.Secret;
import io.fabric8.kubernetes.api.model.SecretBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.javaoperatorsdk.operator.api.reconciler.Context;
import io.javaoperatorsdk.operator.api.reconciler.ControllerConfiguration;
import io.javaoperatorsdk.operator.api.reconciler.Reconciler;
import io.javaoperatorsdk.operator.api.reconciler.UpdateControl;

import javax.inject.Inject;
import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static io.javaoperatorsdk.operator.api.reconciler.Constants.WATCH_ALL_NAMESPACES;

@ControllerConfiguration(namespaces = WATCH_ALL_NAMESPACES)
public class DatabaseUserReconciler implements Reconciler<DatabaseUser> {

    @Inject
    KubernetesClient client;

    @Inject
    DatabaseApi databaseApi;

    @Override
    public UpdateControl<DatabaseUser> reconcile(DatabaseUser resource, Context<DatabaseUser> context) throws Exception {
        DatabaseUserSpec spec = resource.getSpec();

        String databaseInstanceName = spec.getDatabaseInstance();

        DatabaseInstance databaseInstance = this.client.resources(DatabaseInstance.class)
                .inNamespace(resource.getMetadata().getNamespace())
                .withName(databaseInstanceName)
                .get();

        if (databaseInstance == null) {
            resource.setStatus(DatabaseUserStatus.builder()
                    .status(false)
                    .message("Database instance " + databaseInstanceName + " not found!")
                    .build());

            return UpdateControl.updateStatus(resource).rescheduleAfter(30, TimeUnit.SECONDS);
        }

        DatabaseInstanceSpec databaseInstanceSpec = databaseInstance.getSpec();

        String database = spec.getName();
        String username = spec.getName();
        String password;

        // mongodb-iwcrates-credentials
        String secretName = databaseInstanceName + "-" + resource.getMetadata().getName() + "-credentials";

        Secret existingSecret = null;

        try {
            existingSecret = this.client.secrets()
                    .inNamespace(resource.getMetadata().getNamespace())
                    .withName(secretName)
                    .get();
        } catch (Throwable ignored) {
        }

        if (existingSecret != null && existingSecret.getData() != null && existingSecret.getData().containsKey("password")) {
            password = new String(Base64.getDecoder().decode(existingSecret.getData().get("password")));
        } else {
            password = UUID.randomUUID().toString().replaceAll("-", "");
        }

        gg.nils.database.operator.api.database.DatabaseInstance instance = this.databaseApi.getDatabaseInstance(databaseInstance);
        instance.createOrUpdate(database, username, password);
        instance.close();

        Secret secret = new SecretBuilder()
                .editOrNewMetadata()
                .withNamespace(resource.getMetadata().getNamespace())
                .withName(secretName)
                .endMetadata()
                .addToStringData("database", database)
                .addToStringData("username", username)
                .addToStringData("password", password)
                .build();
        this.client.secrets()
                .inNamespace(secret.getMetadata().getNamespace())
                .createOrReplace(secret);

        resource.setStatus(DatabaseUserStatus.builder()
                .status(true)
                .build());

        return UpdateControl.updateStatus(resource);
    }
}
