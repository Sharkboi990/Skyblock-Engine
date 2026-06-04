package dev.shark.skyblock.commands;

import dev.shark.skyblock.SharkSkyblock;
import dev.shark.skyblock.quests.QuestData;
import dev.shark.skyblock.util.ChatUtil;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class QuestsCommand implements CommandExecutor {

    private final SharkSkyblock plugin;

    public QuestsCommand(SharkSkyblock plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        String uuid = player.getUniqueId().toString();
        QuestData data = plugin.getQuestManager().getQuestData(uuid);
        int blocksRequired = plugin.getQuestManager().getBlocksRequired();
        int fishRequired = plugin.getQuestManager().getFishRequired();
        double mineReward = plugin.getConfig().getDouble("economy.quest-mine-reward", 100.0);
        double fishReward = plugin.getConfig().getDouble("economy.quest-fish-reward", 75.0);

        // Time until reset
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime midnight = now.toLocalDate().plusDays(1).atStartOfDay();
        long hoursLeft = ChronoUnit.HOURS.between(now, midnight);
        long minutesLeft = ChronoUnit.MINUTES.between(now, midnight) % 60;

        ChatUtil.sendRaw(player, "&8&m----------------------------");
        ChatUtil.sendRaw(player, "    &b&lDaily Quests &7- Resets in &e" + hoursLeft + "h " + minutesLeft + "m");
        ChatUtil.sendRaw(player, "&8&m----------------------------");

        // Mining quest
        String mineStatus = data.mineRewarded() ? "&a[COMPLETE]" : "&e[" + data.blocksMined() + "/" + blocksRequired + "]";
        String mineBar = ChatUtil.progressBar(
                Math.min(data.blocksMined(), blocksRequired), blocksRequired, 15);
        ChatUtil.sendRaw(player, " &e⛏ Mine &b" + blocksRequired + " Blocks " + mineStatus);
        ChatUtil.sendRaw(player, "   " + mineBar + " &6+" + ChatUtil.formatCoins(mineReward) + " 🪙");

        ChatUtil.sendRaw(player, "");

        // Fishing quest
        String fishStatus = data.fishRewarded() ? "&a[COMPLETE]" : "&e[" + data.fishCaught() + "/" + fishRequired + "]";
        String fishBar = ChatUtil.progressBar(
                Math.min(data.fishCaught(), fishRequired), fishRequired, 15);
        ChatUtil.sendRaw(player, " &b🎣 Catch &b" + fishRequired + " Fish " + fishStatus);
        ChatUtil.sendRaw(player, "   " + fishBar + " &6+" + ChatUtil.formatCoins(fishReward) + " 🪙");

        // Total earnings
        double earned = 0;
        if (data.mineRewarded()) earned += mineReward;
        if (data.fishRewarded()) earned += fishReward;
        ChatUtil.sendRaw(player, "");
        ChatUtil.sendRaw(player, " &7Today's earnings: &6" + ChatUtil.formatCoins(earned) + " / " + ChatUtil.formatCoins(mineReward + fishReward) + " 🪙");
        ChatUtil.sendRaw(player, "&8&m----------------------------");

        return true;
    }
}
