package net.noscape.project.supremetags.storage;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.noscape.project.supremetags.SupremeTags;
import org.jetbrains.annotations.NotNull;

import java.sql.*;

public class MySQL {

    protected final HikariConfig config = new HikariConfig();
    protected final HikariDataSource ds;

    private boolean isConnected = false;

    public MySQL(String host, int port, String database, String username, String password, String options, boolean useSSL) {
        config.setIdleTimeout(870000000);
        config.setMaxLifetime(870000000);
        config.setConnectionTimeout(870000000);
        config.setMinimumIdle(20);
        config.setRegisterMbeans(true);
        config.setMaximumPoolSize(10);

        config.setConnectionTestQuery("SELECT 1");
        config.setDataSourceClassName("com.mysql.cj.jdbc.MysqlDataSource");
        config.addDataSourceProperty("serverName", host);
        config.addDataSourceProperty("port", port);
        config.addDataSourceProperty("databaseName", database);
        config.addDataSourceProperty("user", username);
        config.addDataSourceProperty("password", password);

        config.addDataSourceProperty( "cachePrepStmts" , "true" );
        config.addDataSourceProperty( "prepStmtCacheSize" , "250" );
        config.addDataSourceProperty( "prepStmtCacheSqlLimit" , "2048" );
        config.addDataSourceProperty("useSSL", useSSL);
        ds = new HikariDataSource(this.config);

        this.connect();
        this.createTable();
    }

    public void executeQuery(String query, Object... parameters) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = ds.getConnection();

            preparedStatement = prepareStatement(query, parameters);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnections(preparedStatement, connection, null);
        }
    }

    public void closeConnections(PreparedStatement preparedStatement, Connection connection, ResultSet resultSet) {
        try {
            if (!connection.isClosed()) {
                if (resultSet != null)
                    resultSet.close();
                if (preparedStatement != null)
                    preparedStatement.close();
                connection.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (NullPointerException exception) {
            exception.printStackTrace();
        }
    }

    public void openConnection() {
        try {
            Connection connection = this.getConnection();
            if (connection.isClosed() || !connection.isValid(2)) {
                // If the connection is closed or not valid, obtain a new one
                this.ds.getConnection();
                this.isConnected = true;
            }
        } catch (SQLException e) {
            SupremeTags.getInstance().getLogger().warning("MYSQL: Something went wrong with connecting to the MySQL database.\n" + e);
        }
    }

    public void connect() {
        openConnection();
    }

    public void createTable() {
        String userTable = "CREATE TABLE IF NOT EXISTS `users` (Name VARCHAR(255) NOT NULL, UUID VARCHAR(255) NOT NULL, Active VARCHAR(255) NOT NULL, PRIMARY KEY (UUID))";

        try (Connection connection = getConnection();
             Statement stmt = connection.createStatement()) {
            stmt.execute(userTable);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        try {
            getConnection();
            if (isConnected) {
                this.getConnection().close();
                this.isConnected = false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private PreparedStatement prepareStatement(String query, Object... parameters) throws SQLException {
        PreparedStatement preparedStatement = getConnection().prepareStatement(query);
        for (int i = 0; i < parameters.length; i++) {
            preparedStatement.setObject(i + 1, parameters[i]);
        }
        return preparedStatement;
    }

    @NotNull
    public final Connection getConnection() throws SQLException {
        return this.ds.getConnection();
    }
}