package gg.nils.database.operator.crds.v1alpha1.databaseuser;

import gg.nils.database.operator.api.DatabaseUserManager;
import gg.nils.database.operator.api.DatabaseUserManagerFactory;
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

import static io.javaoperatorsdk.operator.api.reconciler.Constants.WATCH_ALL_NAMESPACES;

@ControllerConfiguration(namespaces = WATCH_ALL_NAMESPACES)
public class DatabaseUserReconciler implements Reconciler<DatabaseUser> {

    @Inject
    KubernetesClient client;

    @Override
    public UpdateControl<DatabaseUser> reconcile(DatabaseUser resource, Context<DatabaseUser> context) throws Exception {
        DatabaseUserSpec spec = resource.getSpec();

        String database = spec.getName();
        String username = spec.getName();
        String password;

        //String secretName = spec.getConnection().getType() + "-" + database + "-" + username;
        String secretName = resource.getMetadata().getName() + "-credentials";

        Secret existingSecret = this.client.secrets()
                .inNamespace(resource.getMetadata().getNamespace())
                .withName(secretName)
                .get();

        if (existingSecret != null && existingSecret.getData() != null && existingSecret.getData().containsKey("password")) {
            password = new String(Base64.getDecoder().decode(existingSecret.getData().get("password")));
        } else {
            password = UUID.randomUUID().toString().replaceAll("-", "");
        }

        DatabaseUserManager databaseUserManager = DatabaseUserManagerFactory.get(resource);
        databaseUserManager.createOrUpdate(username, database, password);

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

        return UpdateControl.noUpdate();
    }
}
