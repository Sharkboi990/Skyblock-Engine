package dev.shark.skyblock.commands;

import dev.shark.skyblock.SharkSkyblock;
import dev.shark.skyblock.skills.SkillType;
import dev.shark.skyblock.util.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class SkillsCommand implements CommandExecutor {

    private final SharkSkyblock plugin;

    public SkillsCommand(SharkSkyblock plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String uuid;
        String displayName;

        if (args.length > 0) {
            if (!sender.hasPermission("sharksb.admin") && !(sender instanceof Player p
                    && p.getName().equalsIgnoreCase(args[0]))) {
                ChatUtil.send(sender, "&cYou can only view your own skills.");
                return true;
            }
            Player target = Bukkit.getPlayerExact(args[0]);
            if (target == null) {
                String foundUUID = plugin.getEconomyManager().getUUIDByName(args[0]);
                if (foundUUID == null) {
                    ChatUtil.send(sender, "&cPlayer not found.");
                    return true;
                }
                uuid = foundUUID;
                displayName = args[0];
            } else {
                uuid = target.getUniqueId().toString();
                displayName = target.getName();
            }
        } else {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("Usage: /skills <player>");
                return true;
            }
            uuid = ((Player) sender).getUniqueId().toString();
            displayName = ((Player) sender).getName();
        }

        int maxLevel = plugin.getSkillManager().getMaxLevel();

        ChatUtil.sendRaw(sender, "&8&m------------------------");
        ChatUtil.sendRaw(sender, "  &b&l" + displayName + "'s Skills");
        ChatUtil.sendRaw(sender, "&8&m------------------------");

        for (SkillType skill : SkillType.values()) {
            int level = plugin.getSkillManager().getLevel(uuid, skill);
            int xp = plugin.getSkillManager().getXP(uuid, skill);
            int required = plugin.getSkillManager().getXPRequired(level);

            String bar = ChatUtil.progressBar(xp, required, 20);

            ChatUtil.sendRaw(sender, " &e" + skill.getDisplayName() + " &7[&b" + level + "/" + maxLevel + "&7]");
            if (level < maxLevel) {
                ChatUtil.sendRaw(sender, "  " + bar + " &7" + xp + "/" + required + " XP");
            } else {
                ChatUtil.sendRaw(sender, "  &6&lMAX LEVEL!");
            }
        }
        ChatUtil.sendRaw(sender, "&8&m------------------------");
        return true;
    }
}
