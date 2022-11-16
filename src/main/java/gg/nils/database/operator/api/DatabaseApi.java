package gg.nils.database.operator.api;

import gg.nils.database.operator.api.database.DatabaseInstance;

public interface DatabaseApi {

    DatabaseInstance getDatabaseInstance(String type, String uri);
}
