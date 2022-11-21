package gg.nils.database.operator.crds.v1alpha1.databaseinstance;

import gg.nils.database.operator.api.DatabaseApi;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.javaoperatorsdk.operator.api.reconciler.Context;
import io.javaoperatorsdk.operator.api.reconciler.ControllerConfiguration;
import io.javaoperatorsdk.operator.api.reconciler.Reconciler;
import io.javaoperatorsdk.operator.api.reconciler.UpdateControl;

import javax.inject.Inject;
import java.util.concurrent.TimeUnit;

import static io.javaoperatorsdk.operator.api.reconciler.Constants.WATCH_ALL_NAMESPACES;

@ControllerConfiguration(namespaces = WATCH_ALL_NAMESPACES)
public class DatabaseInstanceReconciler implements Reconciler<DatabaseInstance> {

    @Inject
    KubernetesClient client;

    @Inject
    DatabaseApi databaseApi;

    @Override
    public UpdateControl<DatabaseInstance> reconcile(DatabaseInstance databaseInstance, Context<DatabaseInstance> context) throws Exception {
        DatabaseInstanceSpec spec = databaseInstance.getSpec();

        try {
            gg.nils.database.operator.api.database.DatabaseInstance db = this.databaseApi.getDatabaseInstance(databaseInstance);
            db.ping();
            db.close();
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
