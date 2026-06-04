package dev.shark.skyblock.listeners;

import dev.shark.skyblock.SharkSkyblock;
import dev.shark.skyblock.quests.QuestData;
import dev.shark.skyblock.skills.SkillType;
import dev.shark.skyblock.util.ChatUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.PlayerFishEvent;

public class FishingListener implements Listener {

    private final SharkSkyblock plugin;

    public FishingListener(SharkSkyblock plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onFish(PlayerFishEvent event) {
        if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH) return;
        if (event.getCaught() == null) return;

        Player player = event.getPlayer();
        String uuid = player.getUniqueId().toString();
        int xpGain = plugin.getConfig().getInt("skills.xp-per-fish-caught", 25);

        // Award skill XP
        boolean leveledUp = plugin.getSkillManager().addXP(player, SkillType.FISHING, xpGain);
        if (leveledUp) {
            int newLevel = plugin.getSkillManager().getLevel(uuid, SkillType.FISHING);
            ChatUtil.send(player, "&b&l[Level Up!] &eFishing is now level &b" + newLevel + "&e!");
            double bonus = newLevel * 7.5;
            plugin.getEconomyManager().deposit(uuid, bonus);
            ChatUtil.send(player, "&7Level-up bonus: &6+" + ChatUtil.formatCoins(bonus) + " 🪙");
        }

        // Quest tracking
        QuestData questData = plugin.getQuestManager().getQuestData(uuid);
        if (!questData.fishRewarded()) {
            plugin.getQuestManager().incrementFishCaught(uuid);
            QuestData updated = plugin.getQuestManager().getQuestData(uuid);
            int required = plugin.getQuestManager().getFishRequired();
            if (updated.fishCaught() >= required) {
                plugin.getQuestManager().setFishRewarded(uuid);
                double reward = plugin.getConfig().getDouble("economy.quest-fish-reward", 75.0);
                plugin.getEconomyManager().deposit(uuid, reward);
                ChatUtil.send(player, "&a&l[Quest Complete!] &eCatch " + required + " Fish!");
                ChatUtil.send(player, "&7Reward: &6+" + ChatUtil.formatCoins(reward) + " 🪙");
            } else if (updated.fishCaught() % 5 == 0) {
                ChatUtil.send(player, "&7Quest progress: &e" + updated.fishCaught() + "/" + required + " &7fish caught.");
            }
        }
    }
}
