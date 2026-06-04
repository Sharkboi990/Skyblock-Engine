package dev.shark.skyblock.economy;

import dev.shark.skyblock.SharkSkyblock;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.logging.Level;

public class EconomyManager {

    private final SharkSkyblock plugin;

    public EconomyManager(SharkSkyblock plugin) {
        this.plugin = plugin;
    }

    public void ensureAccount(Player player) {
        try (PreparedStatement ps = plugin.getDatabaseManager().getConnection().prepareStatement(
                "INSERT OR IGNORE INTO economy (uuid, username, balance) VALUES (?, ?, ?)")) {
            ps.setString(1, player.getUniqueId().toString());
            ps.setString(2, player.getName());
            ps.setDouble(3, plugin.getConfig().getDouble("economy.starting-balance", 500.0));
            ps.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to ensure account for " + player.getName(), e);
        }
        // Update username in case it changed
        try (PreparedStatement ps = plugin.getDatabaseManager().getConnection().prepareStatement(
                "UPDATE economy SET username = ? WHERE uuid = ?")) {
            ps.setString(1, player.getName());
            ps.setString(2, player.getUniqueId().toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to update username for " + player.getName(), e);
        }
    }

    public double getBalance(String uuid) {
        try (PreparedStatement ps = plugin.getDatabaseManager().getConnection().prepareStatement(
                "SELECT balance FROM economy WHERE uuid = ?")) {
            ps.setString(1, uuid);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getDouble("balance");
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to get balance for " + uuid, e);
        }
        return 0.0;
    }

    public boolean hasBalance(String uuid, double amount) {
        return getBalance(uuid) >= amount;
    }

    public void deposit(String uuid, double amount) {
        try (PreparedStatement ps = plugin.getDatabaseManager().getConnection().prepareStatement(
                "UPDATE economy SET balance = balance + ? WHERE uuid = ?")) {
            ps.setDouble(1, amount);
            ps.setString(2, uuid);
            ps.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to deposit for " + uuid, e);
        }
    }

    public boolean withdraw(String uuid, double amount) {
        if (!hasBalance(uuid, amount)) return false;
        try (PreparedStatement ps = plugin.getDatabaseManager().getConnection().prepareStatement(
                "UPDATE economy SET balance = balance - ? WHERE uuid = ?")) {
            ps.setDouble(1, amount);
            ps.setString(2, uuid);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to withdraw for " + uuid, e);
            return false;
        }
    }

    public String getUsername(String uuid) {
        try (PreparedStatement ps = plugin.getDatabaseManager().getConnection().prepareStatement(
                "SELECT username FROM economy WHERE uuid = ?")) {
            ps.setString(1, uuid);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getString("username");
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to get username for " + uuid, e);
        }
        return null;
    }

    public String getUUIDByName(String name) {
        try (PreparedStatement ps = plugin.getDatabaseManager().getConnection().prepareStatement(
                "SELECT uuid FROM economy WHERE LOWER(username) = LOWER(?)")) {
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getString("uuid");
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to get UUID for " + name, e);
        }
        return null;
    }
}
