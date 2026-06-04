package dev.shark.skyblock.assistant;

import dev.shark.skyblock.SharkSkyblock;
import dev.shark.skyblock.economy.EconomyManager;
import dev.shark.skyblock.events.OceanEventManager;
import dev.shark.skyblock.island.IslandManager;
import dev.shark.skyblock.quests.QuestData;
import dev.shark.skyblock.quests.QuestManager;
import dev.shark.skyblock.skills.SkillManager;
import dev.shark.skyblock.skills.SkillType;
import dev.shark.skyblock.util.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

/**
 * AssistantManager — rule-based intelligent assistant for SharkSkyblock.
 * No external AI services. All logic is driven by player data and rule evaluation.
 */
public class AssistantManager {

    // ── Constants ──────────────────────────────────────────────────────────────
    private static final String PREFIX       = "&8[&b&l🦈 Shark&3Assistant&8] &r";
    private static final String WARN_PREFIX  = "&8[&c&l🦈 Shark&4Assistant&8] &r";
    private static final double ISLAND_UPGRADE_COST = 2500.0;
    private static final double SAVE_GOAL_COINS     = 5000.0;
    private static final int    NEAR_DEATH_HP_PCT   = 30;   // % of max health
    private static final long   NEAR_DEATH_COOLDOWN = 30L;  // seconds per player
    private static final int    QUEST_NEAR_FINISH_PCT = 80;  // % complete = "nearly done"
    private static final int    DAILY_RESET_WARN_HOUR = 23; // hour to warn about reset

    // ── State ──────────────────────────────────────────────────────────────────
    private final SharkSkyblock plugin;
    private final Map<UUID, Long>    nearDeathCooldown = new HashMap<>();
    private final Map<UUID, Boolean> newPlayerTracked  = new HashMap<>();
    private BukkitTask               heartbeatTask;

    public AssistantManager(SharkSkyblock plugin) {
        this.plugin = plugin;
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Lifecycle
    // ─────────────────────────────────────────────────────────────────────────

    public void start() {
        // Every 5 seconds: check health warnings; every 60 s: quest reminders
        heartbeatTask = Bukkit.getScheduler().runTaskTimer(plugin, this::heartbeat, 100L, 100L);
    }

    public void stop() {
        if (heartbeatTask != null && !heartbeatTask.isCancelled()) heartbeatTask.cancel();
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Heartbeat — fires every 5 seconds
    // ─────────────────────────────────────────────────────────────────────────

    private long heartbeatTick = 0;

    private void heartbeat() {
        heartbeatTick++;

        for (Player player : Bukkit.getOnlinePlayers()) {
            checkNearDeath(player);

            // Quest reminders every ~2 minutes (24 ticks * 5 s = 120 s)
            if (heartbeatTick % 24 == 0) {
                checkQuestReminders(player);
            }
        }

        // Event notifications every ~60 seconds (12 ticks)
        if (heartbeatTick % 12 == 0) {
            checkEventNotifications();
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  1. Near-Death Assistance
    // ─────────────────────────────────────────────────────────────────────────

    public void checkNearDeath(Player player) {
        var maxHpAttr = player.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH);
        if (maxHpAttr == null) return;
        double maxHp = maxHpAttr.getValue();
        double curHp  = player.getHealth();
        double pct    = (curHp / maxHp) * 100.0;

        if (pct > NEAR_DEATH_HP_PCT) return;

        UUID  uuid = player.getUniqueId();
        long  now  = System.currentTimeMillis() / 1000L;
        long  last = nearDeathCooldown.getOrDefault(uuid, 0L);

        if (now - last < NEAR_DEATH_COOLDOWN) return;
        nearDeathCooldown.put(uuid, now);

        sendWarn(player, "&cWarning: Critical health detected! Retreat or consume food immediately.");

        // Scan hotbar (slots 0–8) for food
        for (int slot = 0; slot < 9; slot++) {
            var item = player.getInventory().getItem(slot);
            if (item != null && item.getType().isEdible()) {
                sendWarn(player, "&eFood detected in hotbar slot &f" + (slot + 1) + " &e(&f"
                        + formatItemName(item.getType().name()) + "&e). Press it to eat!");
                return;
            }
        }
        // Full inventory scan if not in hotbar
        for (var item : player.getInventory().getContents()) {
            if (item != null && item.getType().isEdible()) {
                sendWarn(player, "&eFood found in your inventory. Move it to your hotbar to eat quickly.");
                return;
            }
        }
        sendWarn(player, "&cNo food detected. Find food or retreat immediately!");
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  2. New Player Guidance
    // ─────────────────────────────────────────────────────────────────────────

    public void greetNewPlayer(Player player) {
        newPlayerTracked.put(player.getUniqueId(), true);

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            send(player, "&b&lWelcome to SharkSkyblock!");
            send(player, "&7The Shark Assistant will guide you through your journey.");
            send(player, "");
            send(player, "&e&lSuggested Goals:");
            send(player, "  &8➊ &7Create your island &8— &e/island create");
            send(player, "  &8➋ &7Reach &eMining Level 5");
            send(player, "  &8➌ &7Reach &eFishing Level 5");
            send(player, "  &8➍ &7Complete your first daily quest &8— &e/quests");
            send(player, "");
            send(player, "&7Type &e/assistant &7at any time for personalized tips.");
        }, 60L); // 3 seconds after join
    }

    public void greetReturningPlayer(Player player) {
        // Check if they have urgent goals on login
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            QuestData quest = plugin.getQuestManager().getQuestData(player.getUniqueId().toString());
            int blocksReq   = plugin.getQuestManager().getBlocksRequired();
            int fishReq     = plugin.getQuestManager().getFishRequired();

            boolean mineNearDone = !quest.mineRewarded() && quest.blocksMined() >= blocksReq * QUEST_NEAR_FINISH_PCT / 100;
            boolean fishNearDone = !quest.fishRewarded() && quest.fishCaught() >= fishReq * QUEST_NEAR_FINISH_PCT / 100;

            if (mineNearDone || fishNearDone) {
                send(player, "&aWelcome back! You're close to finishing a quest today.");
            }
        }, 40L);
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  3. Quest Reminders
    // ─────────────────────────────────────────────────────────────────────────

    private void checkQuestReminders(Player player) {
        String    uuid      = player.getUniqueId().toString();
        QuestData quest     = plugin.getQuestManager().getQuestData(uuid);
        int       blocksReq = plugin.getQuestManager().getBlocksRequired();
        int       fishReq   = plugin.getQuestManager().getFishRequired();

        // Reward available — nag gently
        if (!quest.mineRewarded() && quest.blocksMined() >= blocksReq) {
            send(player, "&a✔ Mining quest complete! Use &e/quests &ato claim your reward.");
        }
        if (!quest.fishRewarded() && quest.fishCaught() >= fishReq) {
            send(player, "&a✔ Fishing quest complete! Use &e/quests &ato claim your reward.");
        }

        // Nearly complete
        if (!quest.mineRewarded() && quest.blocksMined() < blocksReq) {
            int remaining = blocksReq - quest.blocksMined();
            int pct = quest.blocksMined() * 100 / blocksReq;
            if (pct >= QUEST_NEAR_FINISH_PCT) {
                send(player, "&eMining quest: &f" + quest.blocksMined() + "/" + blocksReq
                        + " &7— only &e" + remaining + " blocks &7to go!");
            }
        }
        if (!quest.fishRewarded() && quest.fishCaught() < fishReq) {
            int remaining = fishReq - quest.fishCaught();
            int pct = quest.fishCaught() * 100 / fishReq;
            if (pct >= QUEST_NEAR_FINISH_PCT) {
                send(player, "&eFishing quest: &f" + quest.fishCaught() + "/" + fishReq
                        + " &7— catch &e" + remaining + " more fish &7for a reward!");
            }
        }

        // Approaching midnight reset warning (once per session would be ideal — simplified here)
        java.time.LocalTime time = java.time.LocalTime.now();
        if (time.getHour() == DAILY_RESET_WARN_HOUR && time.getMinute() < 5) {
            send(player, "&7⚠ Daily quests reset at midnight. Make the most of today's progress!");
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  4. Event Notifications
    // ─────────────────────────────────────────────────────────────────────────

    private void checkEventNotifications() {
        OceanEventManager events = plugin.getOceanEventManager();
        if (!events.isEventActive()) return;

        var loc = events.getActiveTreasureLocation();
        if (loc == null) return;

        // Broadcast reminder to all online players
        for (Player player : Bukkit.getOnlinePlayers()) {
            send(player, "&6⚠ &eActive Ocean Event! Treasure chest at &b"
                    + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ()
                    + " &e— find it before someone else does!");
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  5. Command Handlers
    // ─────────────────────────────────────────────────────────────────────────

    /** /assistant — overview of current situation */
    public void handleOverview(Player player) {
        String  uuid    = player.getUniqueId().toString();
        EconomyManager  eco     = plugin.getEconomyManager();
        SkillManager    skills  = plugin.getSkillManager();
        QuestManager    quests  = plugin.getQuestManager();
        IslandManager   islands = plugin.getIslandManager();

        double  balance  = eco.getBalance(uuid);
        int     mineLevel = skills.getLevel(uuid, SkillType.MINING);
        int     fishLevel = skills.getLevel(uuid, SkillType.FISHING);
        QuestData quest   = quests.getQuestData(uuid);
        boolean hasIsland = islands.hasIsland(uuid);
        int     blocksReq = quests.getBlocksRequired();
        int     fishReq   = quests.getFishRequired();

        sendRaw(player, "&8&m                                                  ");
        sendRaw(player, "  " + PREFIX + "&b&lYour Overview");
        sendRaw(player, "&8&m                                                  ");
        sendRaw(player, "  &7Island:      " + (hasIsland ? "&aOwned" : "&cNone — type &e/island create"));
        sendRaw(player, "  &7Shark Coins: &6" + ChatUtil.formatCoins(balance));
        sendRaw(player, "  &7Mining:      &fLv " + mineLevel + "  &8| &7Fishing: &fLv " + fishLevel);
        sendRaw(player, "  &7Mining Quest:  " + barLine(quest.blocksMined(), blocksReq, 15)
                + " &f" + quest.blocksMined() + "/" + blocksReq + (quest.mineRewarded() ? " &a✔" : ""));
        sendRaw(player, "  &7Fishing Quest: " + barLine(quest.fishCaught(), fishReq, 15)
                + " &f" + quest.fishCaught() + "/" + fishReq + (quest.fishRewarded() ? " &a✔" : ""));
        sendRaw(player, "&8&m                                                  ");
        sendRaw(player, "  &7Use &e/assistant next &7for personalised advice.");
        sendRaw(player, "&8&m                                                  ");
    }

    /** /assistant next — personalised recommendation list */
    public void handleNext(Player player) {
        List<String> tips = generateTips(player);

        sendRaw(player, "&8&m                                                  ");
        sendRaw(player, "  " + PREFIX + "&b&lRecommended Goals");
        sendRaw(player, "&8&m                                                  ");
        int i = 1;
        for (String tip : tips) {
            sendRaw(player, "  &8" + i + ". &7" + tip);
            i++;
        }
        sendRaw(player, "&8&m                                                  ");
    }

    /** /assistant stats — detailed snapshot */
    public void handleStats(Player player) {
        String         uuid      = player.getUniqueId().toString();
        EconomyManager eco       = plugin.getEconomyManager();
        SkillManager   skills    = plugin.getSkillManager();
        QuestManager   quests    = plugin.getQuestManager();
        IslandManager  islands   = plugin.getIslandManager();

        double    balance   = eco.getBalance(uuid);
        int       mineLevel = skills.getLevel(uuid, SkillType.MINING);
        int       mineXP    = skills.getXP(uuid, SkillType.MINING);
        int       mineReq   = skills.getXPRequired(mineLevel);
        int       fishLevel = skills.getLevel(uuid, SkillType.FISHING);
        int       fishXP    = skills.getXP(uuid, SkillType.FISHING);
        int       fishReq   = skills.getXPRequired(fishLevel);
        QuestData quest     = quests.getQuestData(uuid);
        int       blocksReq = quests.getBlocksRequired();
        int       fishQReq  = quests.getFishRequired();
        boolean   hasIsland = islands.hasIsland(uuid);

        sendRaw(player, "&8&m                                                  ");
        sendRaw(player, "  " + PREFIX + "&b&lDetailed Stats");
        sendRaw(player, "&8&m                                                  ");
        sendRaw(player, "  &7Island       &8» " + (hasIsland ? "&aOwned" : "&cNot created"));
        sendRaw(player, "  &7Shark Coins  &8» &6" + ChatUtil.formatCoins(balance) + " 🪙");
        sendRaw(player, "  &7Mining       &8» &fLv " + mineLevel + "  &8[" + barLine(mineXP, mineReq, 12) + "&8]  &7" + mineXP + "/" + mineReq + " XP");
        sendRaw(player, "  &7Fishing      &8» &fLv " + fishLevel + "  &8[" + barLine(fishXP, fishReq, 12) + "&8]  &7" + fishXP + "/" + fishReq + " XP");
        sendRaw(player, "  &7Mine Quest   &8» &f" + quest.blocksMined() + "/" + blocksReq + " blocks"
                + (quest.mineRewarded() ? " &a(Rewarded)" : ""));
        sendRaw(player, "  &7Fish Quest   &8» &f" + quest.fishCaught() + "/" + fishQReq + " fish"
                + (quest.fishRewarded() ? " &a(Rewarded)" : ""));
        OceanEventManager events = plugin.getOceanEventManager();
        sendRaw(player, "  &7Ocean Event  &8» " + (events.isEventActive() ? "&a&lACTIVE" : "&7Inactive"));
        sendRaw(player, "&8&m                                                  ");
    }

    /** /assistant help */
    public void handleHelp(Player player) {
        sendRaw(player, "&8&m                                                  ");
        sendRaw(player, "  " + PREFIX + "&b&lHelp");
        sendRaw(player, "&8&m                                                  ");
        sendRaw(player, "  &e/assistant         &7— Personal overview");
        sendRaw(player, "  &e/assistant next    &7— Recommended goals");
        sendRaw(player, "  &e/assistant stats   &7— Detailed skill & quest stats");
        sendRaw(player, "  &e/assistant help    &7— This help menu");
        sendRaw(player, "&8&m                                                  ");
        sendRaw(player, "  &7The assistant also sends &bautomatic tips &7for:");
        sendRaw(player, "  &8• &7Critical health warnings");
        sendRaw(player, "  &8• &7Quest near-completion reminders");
        sendRaw(player, "  &8• &7Ocean event notifications");
        sendRaw(player, "&8&m                                                  ");
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Rule Engine — generates personalised tips
    // ─────────────────────────────────────────────────────────────────────────

    private List<String> generateTips(Player player) {
        String         uuid      = player.getUniqueId().toString();
        EconomyManager eco       = plugin.getEconomyManager();
        SkillManager   skills    = plugin.getSkillManager();
        QuestManager   quests    = plugin.getQuestManager();
        IslandManager  islands   = plugin.getIslandManager();

        double    balance   = eco.getBalance(uuid);
        int       mineLevel = skills.getLevel(uuid, SkillType.MINING);
        int       fishLevel = skills.getLevel(uuid, SkillType.FISHING);
        QuestData quest     = quests.getQuestData(uuid);
        int       blocksReq = quests.getBlocksRequired();
        int       fishReq   = quests.getFishRequired();
        boolean   hasIsland = islands.hasIsland(uuid);
        OceanEventManager events = plugin.getOceanEventManager();

        List<String> tips = new ArrayList<>();

        // ── Island ────────────────────────────────────────────────────────────
        if (!hasIsland) {
            tips.add("Create your island to begin your Skyblock journey. &e/island create");
        }

        // ── Quest tips ────────────────────────────────────────────────────────
        if (!quest.mineRewarded() && quest.blocksMined() >= blocksReq) {
            tips.add("&aMining quest complete! &7Claim your reward: &e/quests");
        } else if (!quest.mineRewarded()) {
            int rem = blocksReq - quest.blocksMined();
            tips.add("Mining quest: &f" + quest.blocksMined() + "/" + blocksReq
                    + " &7— mine &f" + rem + " more blocks &7for a reward.");
        }

        if (!quest.fishRewarded() && quest.fishCaught() >= fishReq) {
            tips.add("&aFishing quest complete! &7Claim your reward: &e/quests");
        } else if (!quest.fishRewarded()) {
            int rem = fishReq - quest.fishCaught();
            tips.add("Fishing quest: &f" + quest.fishCaught() + "/" + fishReq
                    + " &7— catch &f" + rem + " more fish &7for a reward.");
        }

        // ── Skill balance ─────────────────────────────────────────────────────
        int diff = mineLevel - fishLevel;
        if (diff >= 3) {
            tips.add("Your Mining level (&f" + mineLevel + "&7) is much higher than Fishing (&f"
                    + fishLevel + "&7). Try fishing to balance progression.");
        } else if (diff <= -3) {
            tips.add("Your Fishing level (&f" + fishLevel + "&7) is much higher than Mining (&f"
                    + mineLevel + "&7). Consider mining to balance progression.");
        }

        // ── Milestone goals ───────────────────────────────────────────────────
        int[] milestonesLow = {5, 10, 15, 20, 25, 50};
        for (int milestone : milestonesLow) {
            if (mineLevel < milestone) {
                tips.add("Reach Mining Level &f" + milestone + " &7(currently Lv &f" + mineLevel + "&7).");
                break;
            }
        }
        for (int milestone : milestonesLow) {
            if (fishLevel < milestone) {
                tips.add("Reach Fishing Level &f" + milestone + " &7(currently Lv &f" + fishLevel + "&7).");
                break;
            }
        }

        // ── Economy tips ──────────────────────────────────────────────────────
        if (balance >= ISLAND_UPGRADE_COST) {
            tips.add("You have &6" + ChatUtil.formatCoins(balance) + " 🪙 &7— enough for an island upgrade!");
        } else {
            double needed = SAVE_GOAL_COINS - balance;
            if (needed > 0) {
                tips.add("Save up to &6" + ChatUtil.formatCoins(SAVE_GOAL_COINS) + " 🪙 &7(need &f"
                        + ChatUtil.formatCoins(needed) + " more&7).");
            }
        }

        // ── Event tip ─────────────────────────────────────────────────────────
        if (events.isEventActive() && events.getActiveTreasureLocation() != null) {
            var loc = events.getActiveTreasureLocation();
            tips.add("&6Ocean Event active! &7Treasure at &b"
                    + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ() + "&7.");
        }

        // ── Fallback ──────────────────────────────────────────────────────────
        if (tips.isEmpty()) {
            tips.add("You're doing great! Keep mining and fishing for XP rewards.");
            tips.add("Check for ocean events periodically for bonus coins.");
        }

        // Cap to 5 most relevant
        if (tips.size() > 5) tips = tips.subList(0, 5);
        return tips;
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Helpers
    // ─────────────────────────────────────────────────────────────────────────

    private void send(Player player, String message) {
        player.sendMessage(ChatUtil.color(PREFIX + message));
    }

    private void sendWarn(Player player, String message) {
        player.sendMessage(ChatUtil.color(WARN_PREFIX + message));
    }

    private void sendRaw(Player player, String message) {
        player.sendMessage(ChatUtil.color(message));
    }

    private String barLine(int current, int max, int length) {
        if (max <= 0) return "&7" + "|".repeat(length);
        int filled = Math.min((int) ((double) current / max * length), length);
        return "&a" + "|".repeat(filled) + "&7" + "|".repeat(length - filled);
    }

    private String formatItemName(String materialName) {
        return materialName.replace('_', ' ').toLowerCase();
    }
}
