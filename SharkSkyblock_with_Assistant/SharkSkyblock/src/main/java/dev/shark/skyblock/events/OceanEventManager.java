package dev.shark.skyblock.events;

import dev.shark.skyblock.SharkSkyblock;
import dev.shark.skyblock.util.ChatUtil;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.Random;

public class OceanEventManager {

    private final SharkSkyblock plugin;
    private BukkitTask task;
    private Location activeTreasureLocation;
    private boolean eventActive = false;
    private final Random random = new Random();

    public OceanEventManager(SharkSkyblock plugin) {
        this.plugin = plugin;
    }

    public void startScheduler() {
        long intervalTicks = plugin.getConfig().getLong("ocean-event.interval-minutes", 30) * 60 * 20L;
        task = Bukkit.getScheduler().runTaskTimer(plugin, this::triggerOceanEvent, intervalTicks, intervalTicks);
    }

    public void stopScheduler() {
        if (task != null && !task.isCancelled()) task.cancel();
    }

    public void triggerOceanEvent() {
        // Find a suitable ocean world (default world)
        World world = Bukkit.getWorlds().get(0);
        int radius = plugin.getConfig().getInt("ocean-event.search-radius", 2000);

        Location loc = findOceanLocation(world, radius);
        if (loc == null) {
            plugin.getLogger().warning("OceanEvent: Could not find a valid ocean location.");
            return;
        }

        // Place treasure chest
        loc.getBlock().setType(Material.CHEST);
        Chest chest = (Chest) loc.getBlock().getState();
        fillTreasureChest(chest);

        activeTreasureLocation = loc;
        eventActive = true;

        double reward = plugin.getConfig().getDouble("ocean-event.chest-reward", 250.0);
        String msg = ChatUtil.color("&6&l[OCEAN EVENT] &eA treasure chest has appeared at &b"
                + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ()
                + " &ein the ocean! First to open it wins &6" + reward + " Shark Coins&e!");
        Bukkit.broadcastMessage(msg);
    }

    private Location findOceanLocation(World world, int radius) {
        for (int attempt = 0; attempt < 50; attempt++) {
            int x = random.nextInt(radius * 2) - radius;
            int z = random.nextInt(radius * 2) - radius;
            Biome biome = world.getBiome(x, 64, z);
            if (isOceanBiome(biome)) {
                // Find the surface y
                int y = world.getHighestBlockYAt(x, z);
                Block block = world.getBlockAt(x, y, z);
                // Must be water surface
                if (block.getType() == Material.WATER) {
                    // Place chest on seafloor nearby
                    for (int dy = y; dy > 30; dy--) {
                        Block b = world.getBlockAt(x, dy, z);
                        if (b.getType() != Material.WATER && b.getType() != Material.AIR) {
                            world.getChunkAt(b.getLocation()).load();
                            return b.getLocation().add(0, 1, 0);
                        }
                    }
                }
            }
        }
        return null;
    }

    private boolean isOceanBiome(Biome biome) {
        String name = biome.name();
        return name.contains("OCEAN");
    }

    private void fillTreasureChest(Chest chest) {
        chest.getInventory().addItem(
                new ItemStack(Material.DIAMOND, random.nextInt(5) + 1),
                new ItemStack(Material.GOLD_INGOT, random.nextInt(10) + 5),
                new ItemStack(Material.EMERALD, random.nextInt(3) + 1),
                new ItemStack(Material.PRISMARINE_CRYSTALS, random.nextInt(8) + 4),
                new ItemStack(Material.SEA_LANTERN, random.nextInt(3) + 1),
                new ItemStack(Material.NAUTILUS_SHELL, 1),
                new ItemStack(Material.COOKED_COD, random.nextInt(8) + 4)
        );
        chest.update();
    }

    public Location getActiveTreasureLocation() { return activeTreasureLocation; }
    public boolean isEventActive() { return eventActive; }

    public void claimTreasure() {
        eventActive = false;
        activeTreasureLocation = null;
    }
}
