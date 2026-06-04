package dev.shark.skyblock.commands;

import dev.shark.skyblock.SharkSkyblock;
import dev.shark.skyblock.util.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class BalanceCommand implements CommandExecutor {

    private final SharkSkyblock plugin;

    public BalanceCommand(SharkSkyblock plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("Usage: /balance <player>");
                return true;
            }
            double balance = plugin.getEconomyManager().getBalance(player.getUniqueId().toString());
            ChatUtil.send(player, "&6Your Shark Coin balance: &e" + ChatUtil.formatCoins(balance) + " 🪙");
        } else {
            // Admin check or self-lookup by name
            String targetName = args[0];
            Player online = Bukkit.getPlayerExact(targetName);
            String uuid;
            String displayName;

            if (online != null) {
                uuid = online.getUniqueId().toString();
                displayName = online.getName();
            } else {
                uuid = plugin.getEconomyManager().getUUIDByName(targetName);
                displayName = targetName;
            }

            if (uuid == null) {
                ChatUtil.send(sender, plugin.getConfig().getString("messages.player-not-found",
                        "&cPlayer not found or never played before."));
                return true;
            }

            if (!sender.hasPermission("sharksb.admin") && sender instanceof Player p
                    && !p.getUniqueId().toString().equals(uuid)) {
                ChatUtil.send(sender, "&cYou can only check your own balance.");
                return true;
            }

            double balance = plugin.getEconomyManager().getBalance(uuid);
            ChatUtil.send(sender, "&e" + displayName + "&6's Shark Coin balance: &e" + ChatUtil.formatCoins(balance) + " 🪙");
        }
        return true;
    }
}
