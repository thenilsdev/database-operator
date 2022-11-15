package gg.nils.database.operator.crds.v1alpha1.databaseinstance;

import io.fabric8.kubernetes.client.KubernetesClient;
import io.javaoperatorsdk.operator.api.reconciler.Context;
import io.javaoperatorsdk.operator.api.reconciler.ControllerConfiguration;
import io.javaoperatorsdk.operator.api.reconciler.Reconciler;
import io.javaoperatorsdk.operator.api.reconciler.UpdateControl;

import javax.inject.Inject;

import static io.javaoperatorsdk.operator.api.reconciler.Constants.WATCH_ALL_NAMESPACES;

@ControllerConfiguration(namespaces = WATCH_ALL_NAMESPACES)
public class DatabaseInstanceReconciler implements Reconciler<DatabaseInstance> {

    @Inject
    KubernetesClient client;


    @Override
    public UpdateControl<DatabaseInstance> reconcile(DatabaseInstance databaseInstance, Context<DatabaseInstance> context) throws Exception {

        // TODO: check connection

        return UpdateControl.noUpdate();
    }
}
