package net.noscape.project.supremetags.storage;

import org.mariadb.jdbc.MariaDbDataSource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MariaDatabase {

    private final String host;
    private final int port;
    private final String database;
    private final String username;
    private final String password;
    private final String options;

    private Connection connection;
    private boolean isConnected = false;

    public MariaDatabase(String host, int port, String database, String username, String password, String options) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
        this.options = options;

        this.connect();
    }

    public void openConnection() throws SQLException {
        this.connect();
    }

    public void connect() {
        try {
            MariaDbDataSource dataSource = new MariaDbDataSource();
            dataSource.setUser(username);
            dataSource.setPassword(password);
            dataSource.setUrl("jdbc:mariadb://" + host + ":" + port + "/" + database + "?" + options);

            synchronized (this) {
                if (connection != null && !connection.isClosed()) {
                    System.err.println("Error: Connection to MariaDB is already open.");
                    return;
                }
                this.connection = dataSource.getConnection();
                this.isConnected = true;
                createTable();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("MariaDB: Error connecting to the database. Check the MariaDB data details before contacting the developer.");
        }
    }

    public void createTable() throws SQLException {
        String userTable = "CREATE TABLE IF NOT EXISTS `users` (Name VARCHAR(255) NOT NULL, UUID VARCHAR(255) NOT NULL, Active VARCHAR(255) NOT NULL, PRIMARY KEY (UUID))";

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(userTable);
        }
    }

    public void updateQuery(String query) {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        try {
            if (connection != null && isConnected()) {
                this.connection.close();
                this.isConnected = false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ResultSet executeQuery(String query) {
        if (!isConnected()) {
            System.err.println("MariaDB: Not connected to the database.");
            return null;
        } else {
            try {
                return connection.createStatement().executeQuery(query);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public void closeResources(ResultSet rs, Statement st) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if (st != null) {
            try {
                st.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isConnected() {
        return isConnected;
    }

    public Connection getConnection() {
        return connection;
    }
}