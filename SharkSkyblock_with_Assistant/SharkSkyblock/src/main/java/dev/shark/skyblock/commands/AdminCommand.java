package dev.shark.skyblock.commands;

import dev.shark.skyblock.SharkSkyblock;
import dev.shark.skyblock.skills.SkillType;
import dev.shark.skyblock.util.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class AdminCommand implements CommandExecutor {

    private final SharkSkyblock plugin;

    public AdminCommand(SharkSkyblock plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("sharksb.admin")) {
            ChatUtil.send(sender, "&cYou do not have permission.");
            return true;
        }

        if (args.length == 0) {
            sendAdminHelp(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "reload" -> handleReload(sender);
            case "givecoin" -> handleGiveCoin(sender, args);
            case "setskill" -> handleSetSkill(sender, args);
            case "oceanevent" -> handleOceanEvent(sender);
            default -> sendAdminHelp(sender);
        }
        return true;
    }

    private void handleReload(CommandSender sender) {
        plugin.reloadConfig();
        ChatUtil.send(sender, "&aConfiguration reloaded.");
    }

    private void handleGiveCoin(CommandSender sender, String[] args) {
        if (args.length < 3) {
            ChatUtil.send(sender, "&cUsage: /sharksb givecoin <player> <amount>");
            return;
        }
        Player target = Bukkit.getPlayerExact(args[1]);
        String uuid, name;
        if (target != null) {
            uuid = target.getUniqueId().toString();
            name = target.getName();
        } else {
            uuid = plugin.getEconomyManager().getUUIDByName(args[1]);
            name = args[1];
        }
        if (uuid == null) {
            ChatUtil.send(sender, "&cPlayer not found.");
            return;
        }
        double amount;
        try {
            amount = Double.parseDouble(args[2]);
        } catch (NumberFormatException e) {
            ChatUtil.send(sender, "&cInvalid amount.");
            return;
        }
        if (amount <= 0) {
            ChatUtil.send(sender, "&cAmount must be positive.");
            return;
        }
        plugin.getEconomyManager().deposit(uuid, amount);
        ChatUtil.send(sender, "&aGave &e" + ChatUtil.formatCoins(amount) + " 🪙 &ato &b" + name + "&a.");
        if (target != null) {
            ChatUtil.send(target, "&aYou received &e" + ChatUtil.formatCoins(amount) + " 🪙 &afrom an admin.");
        }
    }

    private void handleSetSkill(CommandSender sender, String[] args) {
        if (args.length < 4) {
            ChatUtil.send(sender, "&cUsage: /sharksb setskill <player> <skill> <level>");
            ChatUtil.send(sender, "&7Skills: MINING, FISHING");
            return;
        }
        Player target = Bukkit.getPlayerExact(args[1]);
        String uuid, name;
        if (target != null) {
            uuid = target.getUniqueId().toString();
            name = target.getName();
        } else {
            uuid = plugin.getEconomyManager().getUUIDByName(args[1]);
            name = args[1];
        }
        if (uuid == null) {
            ChatUtil.send(sender, "&cPlayer not found.");
            return;
        }
        SkillType skill;
        try {
            skill = SkillType.valueOf(args[2].toUpperCase());
        } catch (IllegalArgumentException e) {
            ChatUtil.send(sender, "&cInvalid skill. Use: MINING, FISHING");
            return;
        }
        int level;
        try {
            level = Integer.parseInt(args[3]);
        } catch (NumberFormatException e) {
            ChatUtil.send(sender, "&cInvalid level.");
            return;
        }
        plugin.getSkillManager().setLevel(uuid, skill, level);
        ChatUtil.send(sender, "&aSet &b" + name + "&a's &e" + skill.getDisplayName() + " &ato level &b" + level + "&a.");
        if (target != null) {
            ChatUtil.send(target, "&aYour &e" + skill.getDisplayName() + " &askill was set to level &b" + level + "&a by an admin.");
        }
    }

    private void handleOceanEvent(CommandSender sender) {
        plugin.getOceanEventManager().triggerOceanEvent();
        ChatUtil.send(sender, "&aManually triggered an ocean event.");
    }

    private void sendAdminHelp(CommandSender sender) {
        ChatUtil.sendRaw(sender, "&8&m--------------------------");
        ChatUtil.sendRaw(sender, " &c&lSharkSkyblock Admin");
        ChatUtil.sendRaw(sender, " &e/sharksb reload &7- Reload config");
        ChatUtil.sendRaw(sender, " &e/sharksb givecoin <player> <amount> &7- Give coins");
        ChatUtil.sendRaw(sender, " &e/sharksb setskill <player> <skill> <level> &7- Set skill level");
        ChatUtil.sendRaw(sender, " &e/sharksb oceanevent &7- Trigger ocean event now");
        ChatUtil.sendRaw(sender, "&8&m--------------------------");
    }
}
