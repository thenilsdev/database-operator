package gg.nils.database.operator.api.database;

public interface DatabaseInstance {

    void createOrUpdate(String database, String username, String password);

    void close();

    void ping() throws Exception;
}
