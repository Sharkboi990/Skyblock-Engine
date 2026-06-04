package dev.shark.skyblock.listeners;

import dev.shark.skyblock.SharkSkyblock;
import dev.shark.skyblock.island.Island;
import dev.shark.skyblock.util.ChatUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.block.*;
import org.bukkit.event.player.PlayerInteractEvent;

public class IslandProtectionListener implements Listener {

    private final SharkSkyblock plugin;

    public IslandProtectionListener(SharkSkyblock plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        if (!isProtectedWorld(event.getBlock().getWorld().getName())) return;
        Player player = event.getPlayer();
        if (player.hasPermission("sharksb.admin")) return;

        Island island = plugin.getIslandManager().getIslandAt(event.getBlock().getLocation());
        if (island == null) {
            // Not on any island - deny
            event.setCancelled(true);
            ChatUtil.send(player, "&cYou cannot break blocks here.");
            return;
        }
        if (!island.getOwnerUUID().equals(player.getUniqueId().toString())) {
            event.setCancelled(true);
            ChatUtil.send(player, "&cYou cannot break blocks on another player's island!");
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (!isProtectedWorld(event.getBlock().getWorld().getName())) return;
        Player player = event.getPlayer();
        if (player.hasPermission("sharksb.admin")) return;

        Island island = plugin.getIslandManager().getIslandAt(event.getBlock().getLocation());
        if (island == null) {
            event.setCancelled(true);
            ChatUtil.send(player, "&cYou cannot place blocks here.");
            return;
        }
        if (!island.getOwnerUUID().equals(player.getUniqueId().toString())) {
            event.setCancelled(true);
            ChatUtil.send(player, "&cYou cannot place blocks on another player's island!");
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) return;
        if (!isProtectedWorld(event.getClickedBlock().getWorld().getName())) return;
        Player player = event.getPlayer();
        if (player.hasPermission("sharksb.admin")) return;

        org.bukkit.Material type = event.getClickedBlock().getType();
        // Only protect interactive blocks: chests, furnaces, crafting tables, etc.
        if (!isInteractiveBlock(type)) return;

        Island island = plugin.getIslandManager().getIslandAt(event.getClickedBlock().getLocation());
        if (island == null) {
            event.setCancelled(true);
            return;
        }
        if (!island.getOwnerUUID().equals(player.getUniqueId().toString())) {
            event.setCancelled(true);
            ChatUtil.send(player, "&cYou cannot interact with another player's island!");
        }
    }

    private boolean isProtectedWorld(String worldName) {
        return worldName.equals("sharkskyblock");
    }

    private boolean isInteractiveBlock(org.bukkit.Material mat) {
        String name = mat.name();
        return name.contains("CHEST") || name.contains("FURNACE") || name.contains("CRAFTING")
                || name.contains("BARREL") || name.contains("HOPPER") || name.contains("DISPENSER")
                || name.contains("DROPPER") || name.contains("SHULKER") || name.contains("BREWING")
                || mat == org.bukkit.Material.ENCHANTING_TABLE || mat == org.bukkit.Material.ANVIL
                || mat == org.bukkit.Material.GRINDSTONE || mat == org.bukkit.Material.SMITHING_TABLE;
    }
}
