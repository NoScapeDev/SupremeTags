package net.noscape.project.supremetags.storage;

import net.noscape.project.supremetags.*;

import java.sql.*;

public class H2Database {

    private final String ConnectionURL;

    public H2Database(String connectionURL) {
        ConnectionURL = connectionURL;

        this.initialiseDatabase();
    }

    public Connection getConnection() {

        Connection connection = null;

        try {
            Class.forName("org.h2.Driver");
            connection = DriverManager.getConnection(SupremeTagsPremium.getConnectionURL());
        } catch (SQLException | ClassNotFoundException throwables) {
            throwables.printStackTrace();
            SupremeTagsPremium.getInstance().getLogger().info("------------------------------");
            SupremeTagsPremium.getInstance().getLogger().info("H2: Something wrong with connecting to h2-sql for SupremeTags, contact the developer if you see this.");
            SupremeTagsPremium.getInstance().getLogger().info("------------------------------");
        }

        return connection;
    }

    public void initialiseDatabase() {
        PreparedStatement preparedStatement;

        String userTable = "CREATE TABLE IF NOT EXISTS `users` (Name VARCHAR(255) NOT NULL, UUID VARCHAR(255) NOT NULL, Active VARCHAR(255) NOT NULL, PRIMARY KEY (UUID))";

        try {
            preparedStatement = getConnection().prepareStatement(userTable);
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public String getConnectionURL() {
        return ConnectionURL;
    }
}