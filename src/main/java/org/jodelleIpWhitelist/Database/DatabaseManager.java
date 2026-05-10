package org.jodelleIpWhitelist.database;

import java.nio.file.Path;
import java.sql.*;

/**
 * This class handles all the heavy lifting for our SQLite database.
 * I built it to keep the login logs organized so we don't have to
 * dig through messy text files later.
 */
public class DatabaseManager {

    private Connection connection;

    public DatabaseManager(Path dataDirectory) {
        try {
            // First, we need to make sure the SQLite driver is actually loaded.
            // Without this, the JDBC bridge won't know how to talk to the .db file.
            Class.forName("org.sqlite.JDBC");

            // We're pointing the database to the plugin's data folder.
            // Using Path.resolve is safer than strings because it handles file separators correctly.
            String url = "jdbc:sqlite:" + dataDirectory.resolve("auth_logs.db").toString();

            // Open the connection. We keep this open while the plugin is running
            // so we aren't constantly opening/closing files (which is slow).
            connection = DriverManager.getConnection(url);

            // Run the initial setup to make sure our table exists.
            setupTable();

        } catch (Exception e) {
            // If the database fails, we definitely need to know why in the console.
            e.printStackTrace();
        }
    }

    /**
     * This just creates the table if it's a fresh install.
     * I added an 'id' column so every entry is unique, and 'timestamp'
     * defaults to the current time so we don't have to pass it manually in Java.
     */
    private void setupTable() throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS login_attempts (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                timestamp DATETIME DEFAULT CURRENT_TIMESTAMP,
                username TEXT,
                ip TEXT,
                status TEXT,
                reason TEXT
            );
            """;

        // Using a try-with-resources here for the Statement to avoid memory leaks.
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        }
    }

    /**
     * Logs a login attempt (success or fail) to the database.
     * * @param user The name they tried to join with
     * @param ip Their IP address
     * @param status Usually 'ALLOWED' or 'DENIED'
     * @param reason Why they were denied (or "Success")
     */
    public void logAttempt(String user, String ip, String status, String reason) {
        // We use '?' placeholders and a PreparedStatement.
        // This is crucial to prevent SQL Injection—never trust user input in a raw query!
        String sql = "INSERT INTO login_attempts(username, ip, status, reason) VALUES(?,?,?,?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            // Mapping our variables to those '?' placeholders.
            pstmt.setString(1, user);
            pstmt.setString(2, ip);
            pstmt.setString(3, status);
            pstmt.setString(4, reason);

            // Send it to the database.
            pstmt.executeUpdate();

        } catch (SQLException e) {
            // Log the error if the write fails (e.g., if the file is locked).
            e.printStackTrace();
        }
    }
}