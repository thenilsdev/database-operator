package gg.nils.database.operator.crds.v1alpha1.databaseuser;

import io.fabric8.kubernetes.api.model.Namespaced;
import io.fabric8.kubernetes.client.CustomResource;
import io.fabric8.kubernetes.model.annotation.Group;
import io.fabric8.kubernetes.model.annotation.Version;

@Version("v1alpha1")
@Group("k8s.nils.gg")
public class DatabaseUser extends CustomResource<DatabaseUserSpec, DatabaseUserStatus> implements Namespaced {
}
