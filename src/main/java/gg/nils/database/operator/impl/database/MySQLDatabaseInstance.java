package gg.nils.database.operator.impl.database;

import gg.nils.database.operator.api.database.DatabaseInstance;

import java.sql.*;

public class MySQLDatabaseInstance implements DatabaseInstance {

    private final Connection connection;

    public MySQLDatabaseInstance(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void createOrUpdate(String database, String username, String password) {
        try {
            if (!this.exists(username)) {
                PreparedStatement preparedStatement = this.connection.prepareStatement("CREATE USER ?@? IDENTIFIED BY ?;");
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, "%");
                preparedStatement.setString(3, password);
                preparedStatement.execute();
            } else {
                PreparedStatement preparedStatement = this.connection.prepareStatement("ALTER USER ?@? IDENTIFIED BY ?;");
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, "%");
                preparedStatement.setString(3, password);
                preparedStatement.execute();
            }

            Statement statement = this.connection.createStatement();
            statement.execute(String.format("GRANT ALL PRIVILEGES ON %s.* TO '%s'@'%%';", database, username));
            statement.close();

            this.connection.createStatement().execute("FLUSH PRIVILEGES;");
        } catch (RuntimeException e) {
            throw e;
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }

        // CREATE USER 'username'@'%' IDENTIFIED BY 'password';
        // ALTER USER 'username'@'%' IDENTIFIED BY 'password';
        // SELECT count(*) FROM mysql.user WHERE user = %s AND host = %s
        // GRANT ALL PRIVILEGES ON database.* TO 'username'@'%';
        // FLUSH PRIVILEGES;
    }

    @Override
    public void close() {
        try {
            this.connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void ping() throws Exception {
        Statement statement = this.connection.createStatement();
        statement.execute("SELECT 1;");
        statement.close();
    }

    private boolean exists(String username) {
        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement("SELECT * FROM mysql.user WHERE user = ? AND host = ?");
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, "%");

            ResultSet resultSet = preparedStatement.executeQuery();

            return resultSet.next();
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }
}
