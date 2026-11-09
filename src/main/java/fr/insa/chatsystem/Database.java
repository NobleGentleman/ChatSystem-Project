package fr.insa.chatsystem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.*;
import java.util.Optional;

public class Database {

    private static final Logger LOG = LoggerFactory.getLogger(Database.class);

    private static final String DATABASE_URL = "jdbc:sqlite:chatSystemDatabase.db";
    private final Connection connection;

    /**
     * Opens the connection to the database and creates the tables if needed.
     *
     * @throws SQLException if the connection cannot be opened or the tables cannot be created
     */
    public Database(String url) throws SQLException {
        this.connection = DriverManager.getConnection(url);
        this.createTables();
    }

    public Database() throws SQLException {
        this(DATABASE_URL);
    }

    /**
     * Creates the tables needed by the application if they do not exist yet.
     * Called once from the constructor, so the tables are ready as soon as
     * the Database object is created.
     *
     * @throws SQLException if a database access error occurs while creating the tables
     */
    private void createTables() throws SQLException {
        String localUserTable = "CREATE TABLE IF NOT EXISTS local_user ("
                + "id INTEGER PRIMARY KEY CHECK (id = 1),"
                + "username TEXT NOT NULL,"
                + "ipAddress TEXT NOT NULL"
                +  ");";

        try (Statement statement = this.connection.createStatement()) {
            statement.execute(localUserTable);
        }
    }

    /**
     * Saves the local user in the database.
     * If a local user already exists (for example after a username change), it is replaced.
     *
     * @param user the local user to save, must not be null
     */
    public void saveLocalUser(User user) {
        String upsertLocalUser = "INSERT OR REPLACE INTO local_user (id, username, ipAddress) VALUES (1, ?, ?);";
        try (PreparedStatement statement = this.connection.prepareStatement(upsertLocalUser)) {
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getIpAddress().getHostAddress());
            statement.executeUpdate();
            LOG.debug("Local user {} saved in the database", user.getUsername());
        } catch (SQLException e) {
            LOG.error("Unable to save local user {} in the database", user.getUsername(), e);
        }
    }

    /**
     * Loads the local user from the database.
     *
     * @return the local user, or an empty Optional if none has been saved yet
     */
    public Optional<User> loadLocalUser() {
        String selectLocalUser = "SELECT username, ipAddress FROM local_user WHERE id = 1;";
        try (PreparedStatement statement = this.connection.prepareStatement(selectLocalUser);
             ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                InetAddress ipAddress = InetAddress.getByName(resultSet.getString("ipAddress"));
                User user = new User(ipAddress);
                user.setUsername(resultSet.getString("username"));
                return Optional.of(user);
            }
        } catch (SQLException e) {
            LOG.error("Unable to load the local user from the database", e);
        } catch (UnknownHostException e) {
            LOG.error("Invalid IP address stored for the local user", e);
        }
        return Optional.empty();
    }
}