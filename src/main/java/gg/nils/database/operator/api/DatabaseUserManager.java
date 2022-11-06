package gg.nils.database.operator.api;

import gg.nils.database.operator.crds.v1alpha1.databaseuser.DatabaseUser;

public interface DatabaseUserManager {

    void createOrUpdate(String database, String username, String password);

    void close();

    // void delete(String database, String username);
}
