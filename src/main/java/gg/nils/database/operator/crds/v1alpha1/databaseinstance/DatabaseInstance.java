package gg.nils.database.operator.crds.v1alpha1.databaseinstance;

import io.fabric8.kubernetes.api.model.Namespaced;
import io.fabric8.kubernetes.client.CustomResource;
import io.fabric8.kubernetes.model.annotation.Group;
import io.fabric8.kubernetes.model.annotation.Version;

@Version("v1alpha1")
@Group("db.nils.gg")
public class DatabaseInstance extends CustomResource<DatabaseInstanceSpec, DatabaseInstanceStatus> implements Namespaced {
}
