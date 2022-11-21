package gg.nils.database.operator.api;

import gg.nils.database.operator.api.database.DatabaseInstance;

public interface DatabaseApi {

    DatabaseInstance getDatabaseInstance(gg.nils.database.operator.crds.v1alpha1.databaseinstance.DatabaseInstance databaseInstance);
}
