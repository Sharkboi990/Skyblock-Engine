package dev.shark.skyblock.listeners;

import dev.shark.skyblock.SharkSkyblock;
import dev.shark.skyblock.util.ChatUtil;
import org.bukkit.event.*;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    private final SharkSkyblock plugin;

    public PlayerJoinListener(SharkSkyblock plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        var player = event.getPlayer();
        // Ensure all DB records exist
        plugin.getEconomyManager().ensureAccount(player);
        plugin.getSkillManager().ensureSkills(player);
        plugin.getQuestManager().ensureQuestEntry(player);

        if (!player.hasPlayedBefore()) {
            ChatUtil.send(player, "&b&lWelcome to SharkSkyblock!");
            ChatUtil.send(player, "&7Use &e/island create &7to start your adventure.");
            ChatUtil.send(player, "&7You start with &e" + ChatUtil.formatCoins(
                    plugin.getConfig().getDouble("economy.starting-balance", 500.0)) + " 🪙 &7Shark Coins.");
        } else {
            ChatUtil.send(player, "&bWelcome back! Use &e/quests &bto check today's quests.");
        }
    }
}
