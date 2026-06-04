package dev.shark.skyblock.quests;

import dev.shark.skyblock.SharkSkyblock;
import org.bukkit.entity.Player;

import java.sql.*;
import java.time.LocalDate;
import java.util.logging.Level;

public class QuestManager {

    private final SharkSkyblock plugin;

    public QuestManager(SharkSkyblock plugin) {
        this.plugin = plugin;
    }

    private String today() {
        return LocalDate.now().toString();
    }

    public void ensureQuestEntry(Player player) {
        String uuid = player.getUniqueId().toString();
        String today = today();

        try {
            // Check if entry exists for today
            try (PreparedStatement ps = plugin.getDatabaseManager().getConnection().prepareStatement(
                    "SELECT quest_date FROM quests WHERE uuid = ?")) {
                ps.setString(1, uuid);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    String savedDate = rs.getString("quest_date");
                    if (!savedDate.equals(today)) {
                        // New day - reset quests
                        try (PreparedStatement update = plugin.getDatabaseManager().getConnection().prepareStatement(
                                "UPDATE quests SET quest_date=?, blocks_mined=0, fish_caught=0, mine_rewarded=0, fish_rewarded=0 WHERE uuid=?")) {
                            update.setString(1, today);
                            update.setString(2, uuid);
                            update.executeUpdate();
                        }
                    }
                    return;
                }
            }
            // Insert new
            try (PreparedStatement ps = plugin.getDatabaseManager().getConnection().prepareStatement(
                    "INSERT INTO quests (uuid, quest_date, blocks_mined, fish_caught, mine_rewarded, fish_rewarded) VALUES (?,?,0,0,0,0)")) {
                ps.setString(1, uuid);
                ps.setString(2, today);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to ensure quest entry for " + player.getName(), e);
        }
    }

    public QuestData getQuestData(String uuid) {
        ensureQuestEntryByUUID(uuid);
        try (PreparedStatement ps = plugin.getDatabaseManager().getConnection().prepareStatement(
                "SELECT * FROM quests WHERE uuid = ?")) {
            ps.setString(1, uuid);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new QuestData(
                        rs.getInt("blocks_mined"),
                        rs.getInt("fish_caught"),
                        rs.getInt("mine_rewarded") == 1,
                        rs.getInt("fish_rewarded") == 1
                );
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to get quest data for " + uuid, e);
        }
        return new QuestData(0, 0, false, false);
    }

    private void ensureQuestEntryByUUID(String uuid) {
        String today = today();
        try {
            try (PreparedStatement ps = plugin.getDatabaseManager().getConnection().prepareStatement(
                    "SELECT quest_date FROM quests WHERE uuid = ?")) {
                ps.setString(1, uuid);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    if (!rs.getString("quest_date").equals(today)) {
                        try (PreparedStatement update = plugin.getDatabaseManager().getConnection().prepareStatement(
                                "UPDATE quests SET quest_date=?, blocks_mined=0, fish_caught=0, mine_rewarded=0, fish_rewarded=0 WHERE uuid=?")) {
                            update.setString(1, today);
                            update.setString(2, uuid);
                            update.executeUpdate();
                        }
                    }
                    return;
                }
            }
            try (PreparedStatement ps = plugin.getDatabaseManager().getConnection().prepareStatement(
                    "INSERT INTO quests (uuid, quest_date, blocks_mined, fish_caught, mine_rewarded, fish_rewarded) VALUES (?,?,0,0,0,0)")) {
                ps.setString(1, uuid);
                ps.setString(2, today);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to ensure quest by uuid: " + uuid, e);
        }
    }

    public void incrementBlocksMined(String uuid) {
        ensureQuestEntryByUUID(uuid);
        try (PreparedStatement ps = plugin.getDatabaseManager().getConnection().prepareStatement(
                "UPDATE quests SET blocks_mined = blocks_mined + 1 WHERE uuid = ?")) {
            ps.setString(1, uuid);
            ps.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to increment blocks mined for " + uuid, e);
        }
    }

    public void incrementFishCaught(String uuid) {
        ensureQuestEntryByUUID(uuid);
        try (PreparedStatement ps = plugin.getDatabaseManager().getConnection().prepareStatement(
                "UPDATE quests SET fish_caught = fish_caught + 1 WHERE uuid = ?")) {
            ps.setString(1, uuid);
            ps.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to increment fish caught for " + uuid, e);
        }
    }

    public void setMineRewarded(String uuid) {
        try (PreparedStatement ps = plugin.getDatabaseManager().getConnection().prepareStatement(
                "UPDATE quests SET mine_rewarded = 1 WHERE uuid = ?")) {
            ps.setString(1, uuid);
            ps.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to set mine rewarded for " + uuid, e);
        }
    }

    public void setFishRewarded(String uuid) {
        try (PreparedStatement ps = plugin.getDatabaseManager().getConnection().prepareStatement(
                "UPDATE quests SET fish_rewarded = 1 WHERE uuid = ?")) {
            ps.setString(1, uuid);
            ps.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to set fish rewarded for " + uuid, e);
        }
    }

    public int getBlocksRequired() {
        return plugin.getConfig().getInt("quests.blocks-required", 100);
    }

    public int getFishRequired() {
        return plugin.getConfig().getInt("quests.fish-required", 20);
    }
}
