package dev.shark.skyblock.listeners;

import dev.shark.skyblock.SharkSkyblock;
import dev.shark.skyblock.quests.QuestData;
import dev.shark.skyblock.skills.SkillType;
import dev.shark.skyblock.util.ChatUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.Set;

public class BlockBreakListener implements Listener {

    private final SharkSkyblock plugin;

    // Blocks that award mining XP
    private static final Set<Material> MINEABLE = Set.of(
            Material.STONE, Material.COBBLESTONE, Material.DEEPSLATE,
            Material.GRANITE, Material.DIORITE, Material.ANDESITE,
            Material.COAL_ORE, Material.IRON_ORE, Material.GOLD_ORE,
            Material.DIAMOND_ORE, Material.EMERALD_ORE, Material.LAPIS_ORE,
            Material.REDSTONE_ORE, Material.DEEPSLATE_COAL_ORE,
            Material.DEEPSLATE_IRON_ORE, Material.DEEPSLATE_GOLD_ORE,
            Material.DEEPSLATE_DIAMOND_ORE, Material.DEEPSLATE_EMERALD_ORE,
            Material.DEEPSLATE_LAPIS_ORE, Material.DEEPSLATE_REDSTONE_ORE,
            Material.NETHER_QUARTZ_ORE, Material.NETHER_GOLD_ORE,
            Material.SAND, Material.GRAVEL, Material.DIRT, Material.GRASS_BLOCK,
            Material.NETHERRACK, Material.END_STONE, Material.OBSIDIAN
    );

    public BlockBreakListener(SharkSkyblock plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Material mat = event.getBlock().getType();

        if (!MINEABLE.contains(mat)) return;

        String uuid = player.getUniqueId().toString();
        int xpGain = plugin.getConfig().getInt("skills.xp-per-block-mined", 10);

        // Award skill XP
        boolean leveledUp = plugin.getSkillManager().addXP(player, SkillType.MINING, xpGain);
        if (leveledUp) {
            int newLevel = plugin.getSkillManager().getLevel(uuid, SkillType.MINING);
            ChatUtil.send(player, "&b&l[Level Up!] &eMining is now level &b" + newLevel + "&e!");
            // Bonus reward on level up
            double bonus = newLevel * 5.0;
            plugin.getEconomyManager().deposit(uuid, bonus);
            ChatUtil.send(player, "&7Level-up bonus: &6+" + ChatUtil.formatCoins(bonus) + " 🪙");
        }

        // Quest tracking
        QuestData questData = plugin.getQuestManager().getQuestData(uuid);
        if (!questData.mineRewarded()) {
            plugin.getQuestManager().incrementBlocksMined(uuid);
            // Refresh after increment
            QuestData updated = plugin.getQuestManager().getQuestData(uuid);
            int required = plugin.getQuestManager().getBlocksRequired();
            if (updated.blocksMined() >= required) {
                plugin.getQuestManager().setMineRewarded(uuid);
                double reward = plugin.getConfig().getDouble("economy.quest-mine-reward", 100.0);
                plugin.getEconomyManager().deposit(uuid, reward);
                ChatUtil.send(player, "&a&l[Quest Complete!] &eMine " + required + " Blocks!");
                ChatUtil.send(player, "&7Reward: &6+" + ChatUtil.formatCoins(reward) + " 🪙");
            } else if (updated.blocksMined() % 25 == 0) {
                // Milestone message every 25 blocks
                ChatUtil.send(player, "&7Quest progress: &e" + updated.blocksMined() + "/" + required + " &7blocks mined.");
            }
        }
    }
}
