package dev.shark.skyblock.assistant;

import dev.shark.skyblock.SharkSkyblock;
import org.bukkit.event.*;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.entity.Player;

/**
 * AssistantListener — hooks into Bukkit events to drive the Shark Assistant:
 *  • PlayerJoinEvent   → new-player welcome or returning-player nudge
 *  • EntityDamageEvent → near-death health check after damage is applied
 *  • PlayerRespawnEvent → clear cooldown so warnings can resume after respawn
 */
public class AssistantListener implements Listener {

    private final SharkSkyblock    plugin;
    private final AssistantManager manager;

    public AssistantListener(SharkSkyblock plugin) {
        this.plugin  = plugin;
        this.manager = plugin.getAssistantManager();
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Join — greet new players with goal list; remind returning players
    // ─────────────────────────────────────────────────────────────────────────

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!player.hasPlayedBefore()) {
            manager.greetNewPlayer(player);
        } else {
            manager.greetReturningPlayer(player);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Damage — fire near-death check after the event resolves
    // ─────────────────────────────────────────────────────────────────────────

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        // Schedule 1-tick later so Player#getHealth() reflects post-damage value
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            if (player.isOnline() && player.isValid() && !player.isDead()) {
                manager.checkNearDeath(player);
            }
        });
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Respawn — nothing to reset currently; hook present for future use
    // ─────────────────────────────────────────────────────────────────────────

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        // cooldown map persists intentionally — player was dead, no warning needed
        // immediately after respawn; it will expire naturally in 30 s.
    }
}
