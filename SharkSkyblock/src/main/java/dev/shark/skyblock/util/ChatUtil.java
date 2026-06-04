package dev.shark.skyblock.util;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import dev.shark.skyblock.SharkSkyblock;

public final class ChatUtil {

    private ChatUtil() {}

    public static String color(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static void send(CommandSender sender, String message) {
        sender.sendMessage(color(prefix() + message));
    }

    public static void sendRaw(CommandSender sender, String message) {
        sender.sendMessage(color(message));
    }

    private static String prefix() {
        String raw = SharkSkyblock.getInstance().getConfig().getString(
                "messages.prefix", "&8[&b&lShark&3SkyBlock&8] &r");
        return raw;
    }

    public static String formatCoins(double amount) {
        if (amount == Math.floor(amount)) {
            return String.format("%,.0f", amount);
        }
        return String.format("%,.2f", amount);
    }

    public static String progressBar(int current, int max, int length) {
        int filled = (int) ((double) current / max * length);
        StringBuilder sb = new StringBuilder("&a");
        for (int i = 0; i < length; i++) {
            if (i == filled) sb.append("&7");
            sb.append("|");
        }
        return sb.toString();
    }
}
