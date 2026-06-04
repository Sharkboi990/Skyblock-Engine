package dev.shark.skyblock;

import dev.shark.skyblock.commands.*;
import dev.shark.skyblock.database.DatabaseManager;
import dev.shark.skyblock.economy.EconomyManager;
import dev.shark.skyblock.events.OceanEventManager;
import dev.shark.skyblock.island.IslandManager;
import dev.shark.skyblock.listeners.*;
import dev.shark.skyblock.quests.QuestManager;
import dev.shark.skyblock.skills.SkillManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class SharkSkyblock extends JavaPlugin {

    private static SharkSkyblock instance;
    private DatabaseManager databaseManager;
    private EconomyManager economyManager;
    private IslandManager islandManager;
    private SkillManager skillManager;
    private QuestManager questManager;
    private OceanEventManager oceanEventManager;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        // Init DB first
        this.databaseManager = new DatabaseManager(this);
        this.databaseManager.init();

        // Init managers
        this.economyManager = new EconomyManager(this);
        this.islandManager = new IslandManager(this);
        this.skillManager = new SkillManager(this);
        this.questManager = new QuestManager(this);
        this.oceanEventManager = new OceanEventManager(this);

        // Register commands
        IslandCommand islandCmd = new IslandCommand(this);
        Objects.requireNonNull(getCommand("island")).setExecutor(islandCmd);
        Objects.requireNonNull(getCommand("island")).setTabCompleter(islandCmd);
        Objects.requireNonNull(getCommand("balance")).setExecutor(new BalanceCommand(this));
        Objects.requireNonNull(getCommand("pay")).setExecutor(new PayCommand(this));
        Objects.requireNonNull(getCommand("skills")).setExecutor(new SkillsCommand(this));
        Objects.requireNonNull(getCommand("quests")).setExecutor(new QuestsCommand(this));
        Objects.requireNonNull(getCommand("sharksb")).setExecutor(new AdminCommand(this));

        // Register listeners
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        getServer().getPluginManager().registerEvents(new BlockBreakListener(this), this);
        getServer().getPluginManager().registerEvents(new FishingListener(this), this);
        getServer().getPluginManager().registerEvents(new IslandProtectionListener(this), this);
        getServer().getPluginManager().registerEvents(new TreasureChestListener(this), this);

        // Start ocean event
        this.oceanEventManager.startScheduler();

        getLogger().info("SharkSkyblock enabled successfully!");
    }

    @Override
    public void onDisable() {
        if (oceanEventManager != null) oceanEventManager.stopScheduler();
        if (databaseManager != null) databaseManager.close();
        getLogger().info("SharkSkyblock disabled.");
    }

    public static SharkSkyblock getInstance() { return instance; }
    public DatabaseManager getDatabaseManager() { return databaseManager; }
    public EconomyManager getEconomyManager() { return economyManager; }
    public IslandManager getIslandManager() { return islandManager; }
    public SkillManager getSkillManager() { return skillManager; }
    public QuestManager getQuestManager() { return questManager; }
    public OceanEventManager getOceanEventManager() { return oceanEventManager; }
}
