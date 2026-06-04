package dev.shark.skyblock.commands;

import dev.shark.skyblock.SharkSkyblock;
import dev.shark.skyblock.util.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class PayCommand implements CommandExecutor {

    private final SharkSkyblock plugin;

    public PayCommand(SharkSkyblock plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        if (args.length < 2) {
            ChatUtil.send(player, "&cUsage: /pay <player> <amount>");
            return true;
        }

        String targetName = args[0];
        double amount;
        try {
            amount = Double.parseDouble(args[1]);
        } catch (NumberFormatException e) {
            ChatUtil.send(player, "&cInvalid amount.");
            return true;
        }

        if (amount <= 0) {
            ChatUtil.send(player, "&cAmount must be positive.");
            return true;
        }

        if (amount < 0.01) {
            ChatUtil.send(player, "&cMinimum transfer amount is &e0.01&c.");
            return true;
        }

        // Prevent self-pay
        if (targetName.equalsIgnoreCase(player.getName())) {
            ChatUtil.send(player, "&cYou cannot pay yourself.");
            return true;
        }

        Player targetOnline = Bukkit.getPlayerExact(targetName);
        String targetUUID;
        String displayName;

        if (targetOnline != null) {
            targetUUID = targetOnline.getUniqueId().toString();
            displayName = targetOnline.getName();
        } else {
            targetUUID = plugin.getEconomyManager().getUUIDByName(targetName);
            displayName = targetName;
        }

        if (targetUUID == null) {
            ChatUtil.send(player, plugin.getConfig().getString("messages.player-not-found",
                    "&cPlayer not found or never played before."));
            return true;
        }

        String senderUUID = player.getUniqueId().toString();
        if (!plugin.getEconomyManager().hasBalance(senderUUID, amount)) {
            ChatUtil.send(player, plugin.getConfig().getString("messages.insufficient-funds",
                    "&cInsufficient Shark Coins!"));
            double bal = plugin.getEconomyManager().getBalance(senderUUID);
            ChatUtil.send(player, "&7Your balance: &e" + ChatUtil.formatCoins(bal) + " 🪙");
            return true;
        }

        plugin.getEconomyManager().withdraw(senderUUID, amount);
        plugin.getEconomyManager().deposit(targetUUID, amount);

        ChatUtil.send(player, "&aSent &e" + ChatUtil.formatCoins(amount) + " 🪙 &ato &b" + displayName + "&a!");
        if (targetOnline != null) {
            ChatUtil.send(targetOnline, "&b" + player.getName() + " &asent you &e" + ChatUtil.formatCoins(amount) + " 🪙&a!");
        }
        return true;
    }
}
