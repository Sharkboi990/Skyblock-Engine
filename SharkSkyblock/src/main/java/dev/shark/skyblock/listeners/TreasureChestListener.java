package dev.shark.skyblock.listeners;

import dev.shark.skyblock.SharkSkyblock;
import dev.shark.skyblock.util.ChatUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class TreasureChestListener implements Listener {

    private final SharkSkyblock plugin;

    public TreasureChestListener(SharkSkyblock plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getClickedBlock() == null) return;
        if (event.getClickedBlock().getType() != org.bukkit.Material.CHEST) return;

        var oceanEvent = plugin.getOceanEventManager();
        if (!oceanEvent.isEventActive()) return;

        Location treasure = oceanEvent.getActiveTreasureLocation();
        if (treasure == null) return;

        Location clicked = event.getClickedBlock().getLocation();
        if (!sameBlock(clicked, treasure)) return;

        // Player found the treasure!
        Player player = event.getPlayer();
        double reward = plugin.getConfig().getDouble("ocean-event.chest-reward", 250.0);

        plugin.getEconomyManager().deposit(player.getUniqueId().toString(), reward);
        oceanEvent.claimTreasure();

        String broadcast = ChatUtil.color("&6&l[OCEAN EVENT] &b" + player.getName()
                + " &efound the treasure chest and won &6" + ChatUtil.formatCoins(reward) + " Shark Coins&e! 🦈");
        org.bukkit.Bukkit.broadcastMessage(broadcast);

        ChatUtil.send(player, "&6Congratulations! You found the ocean treasure! &e+" + ChatUtil.formatCoins(reward) + " 🪙");
    }

    private boolean sameBlock(Location a, Location b) {
        return a.getWorld() != null && b.getWorld() != null
                && a.getWorld().equals(b.getWorld())
                && a.getBlockX() == b.getBlockX()
                && a.getBlockY() == b.getBlockY()
                && a.getBlockZ() == b.getBlockZ();
    }
}
