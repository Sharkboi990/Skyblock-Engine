package dev.shark.skyblock.skills;

public enum SkillType {
    MINING("Mining", "⛏", "Mine blocks to level up"),
    FISHING("Fishing", "🎣", "Catch fish to level up");

    private final String displayName;
    private final String icon;
    private final String description;

    SkillType(String displayName, String icon, String description) {
        this.displayName = displayName;
        this.icon = icon;
        this.description = description;
    }

    public String getDisplayName() { return displayName; }
    public String getIcon() { return icon; }
    public String getDescription() { return description; }
}
