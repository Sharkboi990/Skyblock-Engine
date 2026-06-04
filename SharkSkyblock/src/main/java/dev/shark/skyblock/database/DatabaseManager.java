package dev.shark.skyblock.database;

import dev.shark.skyblock.SharkSkyblock;

import java.io.File;
import java.sql.*;
import java.util.logging.Level;

public class DatabaseManager {

    private final SharkSkyblock plugin;
    private Connection connection;

    public DatabaseManager(SharkSkyblock plugin) {
        this.plugin = plugin;
    }

    public void init() {
        try {
            File dbFile = new File(plugin.getDataFolder(), "sharkskyblock.db");
            if (!plugin.getDataFolder().exists()) plugin.getDataFolder().mkdirs();
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + dbFile.getAbsolutePath());
            createTables();
            plugin.getLogger().info("Database connected.");
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to connect to database", e);
        }
    }

    private void createTables() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            // Economy table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS economy (
                    uuid TEXT PRIMARY KEY,
                    username TEXT NOT NULL,
                    balance REAL NOT NULL DEFAULT 500.0
                )
            """);

            // Islands table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS islands (
                    uuid TEXT PRIMARY KEY,
                    world TEXT NOT NULL,
                    center_x INTEGER NOT NULL,
                    center_y INTEGER NOT NULL,
                    center_z INTEGER NOT NULL,
                    island_index INTEGER NOT NULL,
                    biome TEXT NOT NULL DEFAULT 'PLAINS',
                    created_at INTEGER NOT NULL
                )
            """);

            // Skills table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS skills (
                    uuid TEXT NOT NULL,
                    skill_name TEXT NOT NULL,
                    level INTEGER NOT NULL DEFAULT 1,
                    xp INTEGER NOT NULL DEFAULT 0,
                    PRIMARY KEY (uuid, skill_name)
                )
            """);

            // Quests table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS quests (
                    uuid TEXT PRIMARY KEY,
                    quest_date TEXT NOT NULL,
                    blocks_mined INTEGER NOT NULL DEFAULT 0,
                    fish_caught INTEGER NOT NULL DEFAULT 0,
                    mine_rewarded INTEGER NOT NULL DEFAULT 0,
                    fish_rewarded INTEGER NOT NULL DEFAULT 0
                )
            """);

            // Island counter table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS meta (
                    key TEXT PRIMARY KEY,
                    value TEXT NOT NULL
                )
            """);
            stmt.execute("INSERT OR IGNORE INTO meta (key, value) VALUES ('island_count', '0')");
        }
    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                init();
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Database connection check failed", e);
        }
        return connection;
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.WARNING, "Error closing database", e);
        }
    }

    public int getNextIslandIndex() {
        try (PreparedStatement ps = getConnection().prepareStatement(
                "SELECT value FROM meta WHERE key = 'island_count'")) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int count = Integer.parseInt(rs.getString("value"));
                int next = count + 1;
                try (PreparedStatement update = getConnection().prepareStatement(
                        "UPDATE meta SET value = ? WHERE key = 'island_count'")) {
                    update.setString(1, String.valueOf(next));
                    update.executeUpdate();
                }
                return next;
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to get island index", e);
        }
        return 1;
    }
}
