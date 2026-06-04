package dev.shark.skyblock.skills;

import dev.shark.skyblock.SharkSkyblock;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.logging.Level;

public class SkillManager {

    private final SharkSkyblock plugin;
    private final int maxLevel;

    public SkillManager(SharkSkyblock plugin) {
        this.plugin = plugin;
        this.maxLevel = plugin.getConfig().getInt("skills.max-level", 50);
    }

    public void ensureSkills(Player player) {
        for (SkillType skill : SkillType.values()) {
            try (PreparedStatement ps = plugin.getDatabaseManager().getConnection().prepareStatement(
                    "INSERT OR IGNORE INTO skills (uuid, skill_name, level, xp) VALUES (?, ?, 1, 0)")) {
                ps.setString(1, player.getUniqueId().toString());
                ps.setString(2, skill.name());
                ps.executeUpdate();
            } catch (SQLException e) {
                plugin.getLogger().log(Level.SEVERE, "Failed to ensure skills for " + player.getName(), e);
            }
        }
    }

    public int getLevel(String uuid, SkillType skill) {
        try (PreparedStatement ps = plugin.getDatabaseManager().getConnection().prepareStatement(
                "SELECT level FROM skills WHERE uuid = ? AND skill_name = ?")) {
            ps.setString(1, uuid);
            ps.setString(2, skill.name());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("level");
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to get skill level", e);
        }
        return 1;
    }

    public int getXP(String uuid, SkillType skill) {
        try (PreparedStatement ps = plugin.getDatabaseManager().getConnection().prepareStatement(
                "SELECT xp FROM skills WHERE uuid = ? AND skill_name = ?")) {
            ps.setString(1, uuid);
            ps.setString(2, skill.name());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("xp");
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to get skill xp", e);
        }
        return 0;
    }

    /**
     * Add XP to a skill and level up if threshold reached.
     * Returns true if leveled up.
     */
    public boolean addXP(Player player, SkillType skill, int amount) {
        String uuid = player.getUniqueId().toString();
        int currentLevel = getLevel(uuid, skill);
        if (currentLevel >= maxLevel) return false;

        int currentXP = getXP(uuid, skill);
        int newXP = currentXP + amount;
        int required = getXPRequired(currentLevel);

        boolean leveledUp = false;
        while (newXP >= required && currentLevel < maxLevel) {
            newXP -= required;
            currentLevel++;
            leveledUp = true;
            required = getXPRequired(currentLevel);
        }
        if (currentLevel >= maxLevel) newXP = 0;

        try (PreparedStatement ps = plugin.getDatabaseManager().getConnection().prepareStatement(
                "UPDATE skills SET level = ?, xp = ? WHERE uuid = ? AND skill_name = ?")) {
            ps.setInt(1, currentLevel);
            ps.setInt(2, newXP);
            ps.setString(3, uuid);
            ps.setString(4, skill.name());
            ps.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to update skill", e);
        }

        return leveledUp;
    }

    /** XP required for a given level: base 100, grows by 50 per level */
    public int getXPRequired(int level) {
        return 100 + (level - 1) * 50;
    }

    public void setLevel(String uuid, SkillType skill, int level) {
        try (PreparedStatement ps = plugin.getDatabaseManager().getConnection().prepareStatement(
                "INSERT OR REPLACE INTO skills (uuid, skill_name, level, xp) VALUES (?, ?, ?, 0)")) {
            ps.setString(1, uuid);
            ps.setString(2, skill.name());
            ps.setInt(3, Math.min(level, maxLevel));
            ps.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to set skill level", e);
        }
    }

    public int getMaxLevel() { return maxLevel; }
}
