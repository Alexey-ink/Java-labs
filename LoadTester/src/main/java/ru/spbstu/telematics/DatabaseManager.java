package ru.spbstu.telematics;

import java.sql.*;

public class DatabaseManager {
    private final Connection connection;

    public DatabaseManager(String dbName) throws SQLException {
        String dbUrl = "jdbc:sqlite:" + dbName;
        this.connection = DriverManager.getConnection(dbUrl);
        initializeDatabase();
    }

    private void initializeDatabase() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS data (" +
                    "id TEXT PRIMARY KEY," +
                    "content TEXT NOT NULL)");
        }
    }

    public void saveData(String id, String json) throws SQLException {
        String sql = "INSERT OR REPLACE INTO data(id, content) VALUES(?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, id);
            pstmt.setString(2, json);
            pstmt.executeUpdate();
        }
    }

    public String getData(String id) throws SQLException {
        String sql = "SELECT content FROM data WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();
            return rs.getString("content");
        }
    }
}