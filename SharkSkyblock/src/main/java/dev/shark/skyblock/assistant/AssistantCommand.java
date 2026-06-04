package dev.shark.skyblock.assistant;

import dev.shark.skyblock.SharkSkyblock;
import dev.shark.skyblock.util.ChatUtil;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

/**
 * Handles the /assistant command and its sub-commands:
 *   /assistant          — overview
 *   /assistant help     — help menu
 *   /assistant next     — personalised recommendations
 *   /assistant stats    — detailed skill & quest stats
 */
public class AssistantCommand implements CommandExecutor, TabCompleter {

    private final SharkSkyblock  plugin;
    private final AssistantManager manager;

    private static final List<String> SUB_COMMANDS = Arrays.asList("help", "next", "stats");

    public AssistantCommand(SharkSkyblock plugin) {
        this.plugin  = plugin;
        this.manager = plugin.getAssistantManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatUtil.color("&cOnly players can use the Shark Assistant."));
            return true;
        }

        if (args.length == 0) {
            manager.handleOverview(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "help"  -> manager.handleHelp(player);
            case "next"  -> manager.handleNext(player);
            case "stats" -> manager.handleStats(player);
            default      -> {
                ChatUtil.send(player, "&cUnknown sub-command. Try &e/assistant help&c.");
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            String prefix = args[0].toLowerCase();
            return SUB_COMMANDS.stream()
                    .filter(s -> s.startsWith(prefix))
                    .toList();
        }
        return List.of();
    }
}
