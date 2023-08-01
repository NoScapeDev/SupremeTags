package net.noscape.project.supremetags.storage;

import org.bukkit.*;

import java.sql.*;
import java.util.logging.*;

public class MySQLDatabase {

    private final String host;
    private final int port;
    private final String database;
    private final String username;
    private final String password;
    private final String options;

    public Connection connection;
    private boolean isconnected = false;

    public MySQLDatabase(String host, int port, String database, String username, String password, String options) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
        this.options = options;

        this.connect(host, port, database, username, password, true);
    }

    public void openConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();

            this.connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + "?" + options, username, password);

        }
    }

    public void connect(String host, int port, String database, String user, String pass, boolean ssl) {
        try {
            synchronized (this) {
                if (connection != null && !connection.isClosed()) {
                    Logger.getLogger("Error: connection to sql was not successful.");
                    return;
                }
                Class.forName("com.mysql.cj.jdbc.Driver");
                this.connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=" + ssl + "&autoReconnect=true", user, pass);
                this.isconnected = true;
                createTable();
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            Bukkit.getConsoleSender().sendMessage("MYSQL: Something wrong with connecting to mysql database type, check mysql data details before contacting the developer if you see this.");
        }
    }

    public void createTable() throws SQLException {
        String userTable = "CREATE TABLE IF NOT EXISTS `users` (Name VARCHAR(255) NOT NULL, UUID VARCHAR(255) NOT NULL, Active VARCHAR(255) NOT NULL, PRIMARY KEY (UUID))";

        Statement stmt = connection.createStatement();
        stmt.execute(userTable);
    }

    public void updateQuery(String query) {
        Connection con = connection;
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = con.prepareStatement(query);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(null, preparedStatement);
        }
    }

    public void disconnected() {
        try {
            if (connection != null && isConnected()) {
                this.connection.close();
                this.isconnected = false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ResultSet executeQuery(String query) {
        if (isConnected()) {
            return null;
        } else {
            try {
                this.connection.createStatement().executeQuery(query);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public void closeResources(ResultSet rs, PreparedStatement st) {
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
        return !this.isconnected;
    }

    public Connection getConnection() {
        return this.connection;
    }

}
