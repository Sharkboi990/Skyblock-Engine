package dev.shark.skyblock.island;

import dev.shark.skyblock.SharkSkyblock;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.*;
import java.util.logging.Level;
import java.util.Objects;

public class IslandManager {

    private final SharkSkyblock plugin;
    private final Map<String, Island> islandCache = new HashMap<>();

    // Spiral layout so islands fan out evenly
    private static final int[][] SPIRAL_DIRS = {{1,0},{0,1},{-1,0},{0,-1}};

    public IslandManager(SharkSkyblock plugin) {
        this.plugin = plugin;
        loadAllIslands();
    }

    private void loadAllIslands() {
        try (Statement stmt = plugin.getDatabaseManager().getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM islands")) {
            while (rs.next()) {
                Island island = new Island(
                        rs.getString("uuid"),
                        rs.getString("world"),
                        rs.getInt("center_x"),
                        rs.getInt("center_y"),
                        rs.getInt("center_z"),
                        rs.getInt("island_index"),
                        rs.getString("biome"),
                        rs.getLong("created_at")
                );
                islandCache.put(rs.getString("uuid"), island);
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to load islands", e);
        }
    }

    public Island getIsland(String uuid) {
        return islandCache.get(uuid);
    }

    public boolean hasIsland(String uuid) {
        return islandCache.containsKey(uuid);
    }

    public Island createIsland(Player player) {
        int index = plugin.getDatabaseManager().getNextIslandIndex();
        int spacing = plugin.getConfig().getInt("island.spacing", 500);

        // Calculate position using a grid spiral
        int[] pos = indexToGridPosition(index, spacing);
        int cx = pos[0];
        int cy = plugin.getConfig().getInt("island.schematic-height", 64);
        int cz = pos[1];

        World world = getOrCreateSkyblockWorld();
        String biome = plugin.getConfig().getString("island.default-biome", "PLAINS");

        // Generate the island platform
        generateIsland(world, cx, cy, cz);

        Island island = new Island(
                player.getUniqueId().toString(),
                world.getName(),
                cx, cy, cz,
                index, biome,
                System.currentTimeMillis()
        );

        // Save to DB
        try (PreparedStatement ps = plugin.getDatabaseManager().getConnection().prepareStatement(
                "INSERT INTO islands (uuid, world, center_x, center_y, center_z, island_index, biome, created_at) VALUES (?,?,?,?,?,?,?,?)")) {
            ps.setString(1, island.getOwnerUUID());
            ps.setString(2, island.getWorldName());
            ps.setInt(3, cx);
            ps.setInt(4, cy);
            ps.setInt(5, cz);
            ps.setInt(6, index);
            ps.setString(7, biome);
            ps.setLong(8, island.getCreatedAt());
            ps.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to save island for " + player.getName(), e);
        }

        islandCache.put(player.getUniqueId().toString(), island);
        return island;
    }

    public void deleteIsland(String uuid) {
        Island island = islandCache.remove(uuid);
        if (island == null) return;

        // Clear the island blocks
        World world = Bukkit.getWorld(island.getWorldName());
        if (world != null) {
            clearIsland(world, island.getCenterX(), island.getCenterY(), island.getCenterZ());
        }

        try (PreparedStatement ps = plugin.getDatabaseManager().getConnection().prepareStatement(
                "DELETE FROM islands WHERE uuid = ?")) {
            ps.setString(1, uuid);
            ps.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to delete island for " + uuid, e);
        }
    }

    public void updateBiome(String uuid, String biome) {
        Island island = islandCache.get(uuid);
        if (island == null) return;
        island.setBiome(biome);
        try (PreparedStatement ps = plugin.getDatabaseManager().getConnection().prepareStatement(
                "UPDATE islands SET biome = ? WHERE uuid = ?")) {
            ps.setString(1, biome);
            ps.setString(2, uuid);
            ps.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to update biome for " + uuid, e);
        }
    }

    /** Convert a 1-based island index into a spiral grid coordinate */
    private int[] indexToGridPosition(int index, int spacing) {
        if (index == 1) return new int[]{0, 0};
        // Simple row-based layout: each row holds increasing islands
        // Use a simple grid: row = sqrt(index), adjust
        int gridX = 0, gridZ = 0;
        int step = 1, dir = 0, count = 0, turns = 0;
        for (int i = 1; i < index; i++) {
            int[] d = SPIRAL_DIRS[dir % 4];
            gridX += d[0];
            gridZ += d[1];
            count++;
            if (count == step) {
                count = 0;
                dir++;
                turns++;
                if (turns % 2 == 0) step++;
            }
        }
        return new int[]{gridX * spacing, gridZ * spacing};
    }

    private void generateIsland(World world, int cx, int cy, int cz) {
        // Classic skyblock: dirt platform with a tree and a chest
        // Base platform: 7x7 dirt with grass on top, stone below
        for (int x = -3; x <= 3; x++) {
            for (int z = -3; z <= 3; z++) {
                // Make it roughly circular
                if (x * x + z * z <= 12) {
                    world.getBlockAt(cx + x, cy, cz + z).setType(Material.GRASS_BLOCK);
                    world.getBlockAt(cx + x, cy - 1, cz + z).setType(Material.DIRT);
                    world.getBlockAt(cx + x, cy - 2, cz + z).setType(Material.DIRT);
                    world.getBlockAt(cx + x, cy - 3, cz + z).setType(Material.STONE);
                }
            }
        }

        // Sand extension on the side (classic skyblock)
        world.getBlockAt(cx + 4, cy, cz).setType(Material.SAND);
        world.getBlockAt(cx + 5, cy, cz).setType(Material.SAND);
        world.getBlockAt(cx + 4, cy - 1, cz).setType(Material.SAND);

        // Tree: trunk
        for (int y = 1; y <= 4; y++) {
            world.getBlockAt(cx, cy + y, cz).setType(Material.OAK_LOG);
        }

        // Tree: leaves
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                for (int y = 3; y <= 5; y++) {
                    Block b = world.getBlockAt(cx + x, cy + y, cz + z);
                    if (b.getType() == Material.AIR) {
                        if (Math.abs(x) + Math.abs(z) + Math.abs(y - 4) <= 3) {
                            b.setType(Material.OAK_LEAVES);
                        }
                    }
                }
            }
        }

        // Chest with starter items
        world.getBlockAt(cx + 1, cy + 1, cz).setType(Material.CHEST);
        org.bukkit.block.Chest chest = (org.bukkit.block.Chest) world.getBlockAt(cx + 1, cy + 1, cz).getState();
        chest.getInventory().addItem(
                new org.bukkit.inventory.ItemStack(Material.ICE, 1),
                new org.bukkit.inventory.ItemStack(Material.LAVA_BUCKET, 1),
                new org.bukkit.inventory.ItemStack(Material.BONE_MEAL, 16),
                new org.bukkit.inventory.ItemStack(Material.BREAD, 8),
                new org.bukkit.inventory.ItemStack(Material.OAK_SAPLING, 3),
                new org.bukkit.inventory.ItemStack(Material.SUGAR_CANE, 2),
                new org.bukkit.inventory.ItemStack(Material.PUMPKIN_SEEDS, 2),
                new org.bukkit.inventory.ItemStack(Material.FISHING_ROD, 1)
        );
        chest.update();

        // Lava source next to water potential spot (classic)
        world.getBlockAt(cx - 4, cy + 1, cz).setType(Material.LAVA);
    }

    private void clearIsland(World world, int cx, int cy, int cz) {
        int range = 60;
        for (int x = -range; x <= range; x++) {
            for (int z = -range; z <= range; z++) {
                for (int y = -10; y <= 20; y++) {
                    world.getBlockAt(cx + x, cy + y, cz + z).setType(Material.AIR);
                }
            }
        }
    }

    private World getOrCreateSkyblockWorld() {
        World world = Bukkit.getWorld("sharkskyblock");
        if (world == null) {
            WorldCreator creator = new WorldCreator("sharkskyblock");
            creator.environment(World.Environment.NORMAL);
            creator.type(WorldType.FLAT);
            creator.generatorSettings("{\"layers\":[{\"block\":\"air\",\"height\":1}],\"biome\":\"the_void\"}");
            world = Bukkit.createWorld(creator);
            Objects.requireNonNull(world).setSpawnFlags(false, false);
            world.setDifficulty(Difficulty.NORMAL);
            world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
            world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
        }
        return world;
    }

    /** Find which island (if any) owns the given location */
    public Island getIslandAt(Location loc) {
        for (Island island : islandCache.values()) {
            if (island.getWorldName().equals(loc.getWorld().getName()) &&
                    island.isWithinBounds(loc.getBlockX(), loc.getBlockZ())) {
                return island;
            }
        }
        return null;
    }

    public Collection<Island> getAllIslands() {
        return islandCache.values();
    }
}
