package dev.shark.skyblock.commands;

import dev.shark.skyblock.SharkSkyblock;
import dev.shark.skyblock.island.Island;
import dev.shark.skyblock.util.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class IslandCommand implements CommandExecutor, TabCompleter {

    private final SharkSkyblock plugin;

    public IslandCommand(SharkSkyblock plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        if (args.length == 0) {
            sendHelp(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "create" -> handleCreate(player);
            case "home"   -> handleHome(player);
            case "delete" -> handleDelete(player);
            case "info"   -> handleInfo(player);
            case "setbiome" -> handleSetBiome(player, args);
            default -> sendHelp(player);
        }
        return true;
    }

    private void handleCreate(Player player) {
        if (plugin.getIslandManager().hasIsland(player.getUniqueId().toString())) {
            ChatUtil.send(player, plugin.getConfig().getString("messages.island-exists",
                    "&cYou already have an island!"));
            return;
        }
        ChatUtil.send(player, "&eCreating your island, please wait...");
        // Run async generation synchronously on main thread but defer a tick
        Bukkit.getScheduler().runTask(plugin, () -> {
            Island island = plugin.getIslandManager().createIsland(player);
            World world = Bukkit.getWorld(island.getWorldName());
            if (world == null) {
                ChatUtil.send(player, "&cFailed to create island world.");
                return;
            }
            Location home = island.getHomeLocation(world);
            player.teleport(home);
            ChatUtil.send(player, plugin.getConfig().getString("messages.island-created",
                    "&aYour island has been created! Teleporting..."));
            ChatUtil.send(player, "&7Your island is at &e" + home.getBlockX() + ", " + home.getBlockY() + ", " + home.getBlockZ());
        });
    }

    private void handleHome(Player player) {
        Island island = plugin.getIslandManager().getIsland(player.getUniqueId().toString());
        if (island == null) {
            ChatUtil.send(player, plugin.getConfig().getString("messages.no-island",
                    "&cYou do not have an island. Use /island create"));
            return;
        }
        World world = Bukkit.getWorld(island.getWorldName());
        if (world == null) {
            ChatUtil.send(player, "&cYour island world could not be found.");
            return;
        }
        player.teleport(island.getHomeLocation(world));
        ChatUtil.send(player, plugin.getConfig().getString("messages.island-teleported",
                "&aTeleporting to your island..."));
    }

    private void handleDelete(Player player) {
        Island island = plugin.getIslandManager().getIsland(player.getUniqueId().toString());
        if (island == null) {
            ChatUtil.send(player, plugin.getConfig().getString("messages.no-island",
                    "&cYou do not have an island."));
            return;
        }
        // Teleport player out first
        World overworld = Bukkit.getWorlds().get(0);
        player.teleport(overworld.getSpawnLocation());
        plugin.getIslandManager().deleteIsland(player.getUniqueId().toString());
        ChatUtil.send(player, plugin.getConfig().getString("messages.island-deleted",
                "&cYour island has been deleted."));
    }

    private void handleInfo(Player player) {
        Island island = plugin.getIslandManager().getIsland(player.getUniqueId().toString());
        if (island == null) {
            ChatUtil.send(player, "&cYou don't have an island.");
            return;
        }
        ChatUtil.sendRaw(player, "&8&m--------------------");
        ChatUtil.sendRaw(player, " &b&lYour Island Info");
        ChatUtil.sendRaw(player, " &7Location: &e" + island.getCenterX() + ", " + island.getCenterY() + ", " + island.getCenterZ());
        ChatUtil.sendRaw(player, " &7World: &e" + island.getWorldName());
        ChatUtil.sendRaw(player, " &7Biome: &e" + island.getBiome());
        ChatUtil.sendRaw(player, " &7Island #: &e" + island.getIslandIndex());
        ChatUtil.sendRaw(player, "&8&m--------------------");
    }

    private void handleSetBiome(Player player, String[] args) {
        if (args.length < 2) {
            ChatUtil.send(player, "&cUsage: /island setbiome <biome>");
            ChatUtil.send(player, "&7Available: PLAINS, DESERT, FOREST, SNOWY_PLAINS, JUNGLE, SAVANNA");
            return;
        }
        Island island = plugin.getIslandManager().getIsland(player.getUniqueId().toString());
        if (island == null) {
            ChatUtil.send(player, "&cYou don't have an island.");
            return;
        }
        String biome = args[1].toUpperCase();
        List<String> allowed = List.of("PLAINS","DESERT","FOREST","SNOWY_PLAINS","JUNGLE","SAVANNA","OCEAN","MUSHROOM_FIELDS");
        if (!allowed.contains(biome)) {
            ChatUtil.send(player, "&cInvalid biome. Choose from: " + String.join(", ", allowed));
            return;
        }
        plugin.getIslandManager().updateBiome(player.getUniqueId().toString(), biome);
        ChatUtil.send(player, "&aIsland biome set to &e" + biome + "&a!");
    }

    private void sendHelp(Player player) {
        ChatUtil.sendRaw(player, "&8&m--------------------");
        ChatUtil.sendRaw(player, " &b&lSharkSkyblock &7- Island Commands");
        ChatUtil.sendRaw(player, " &e/island create &7- Create your island");
        ChatUtil.sendRaw(player, " &e/island home &7- Teleport to your island");
        ChatUtil.sendRaw(player, " &e/island delete &7- Delete your island");
        ChatUtil.sendRaw(player, " &e/island info &7- View island info");
        ChatUtil.sendRaw(player, " &e/island setbiome <biome> &7- Set island biome");
        ChatUtil.sendRaw(player, "&8&m--------------------");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("create","home","delete","info","setbiome").stream()
                    .filter(s -> s.startsWith(args[0].toLowerCase())).toList();
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("setbiome")) {
            return List.of("PLAINS","DESERT","FOREST","SNOWY_PLAINS","JUNGLE","SAVANNA","OCEAN","MUSHROOM_FIELDS")
                    .stream().filter(s -> s.startsWith(args[1].toUpperCase())).toList();
        }
        return List.of();
    }
}
