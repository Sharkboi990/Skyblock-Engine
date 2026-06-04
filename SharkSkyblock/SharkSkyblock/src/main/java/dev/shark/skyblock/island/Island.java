package dev.shark.skyblock.island;

import org.bukkit.Location;
import org.bukkit.World;

public class Island {

    private final String ownerUUID;
    private final String worldName;
    private final int centerX;
    private final int centerY;
    private final int centerZ;
    private final int islandIndex;
    private String biome;
    private final long createdAt;

    public Island(String ownerUUID, String worldName, int centerX, int centerY, int centerZ,
                  int islandIndex, String biome, long createdAt) {
        this.ownerUUID = ownerUUID;
        this.worldName = worldName;
        this.centerX = centerX;
        this.centerY = centerY;
        this.centerZ = centerZ;
        this.islandIndex = islandIndex;
        this.biome = biome;
        this.createdAt = createdAt;
    }

    public String getOwnerUUID() { return ownerUUID; }
    public String getWorldName() { return worldName; }
    public int getCenterX() { return centerX; }
    public int getCenterY() { return centerY; }
    public int getCenterZ() { return centerZ; }
    public int getIslandIndex() { return islandIndex; }
    public String getBiome() { return biome; }
    public void setBiome(String biome) { this.biome = biome; }
    public long getCreatedAt() { return createdAt; }

    public Location getHomeLocation(World world) {
        return new Location(world, centerX + 0.5, centerY + 1, centerZ + 0.5);
    }

    /** Returns the bounds of the island protection zone (250 block radius) */
    public boolean isWithinBounds(int x, int z) {
        int halfSpacing = 250;
        return Math.abs(x - centerX) <= halfSpacing && Math.abs(z - centerZ) <= halfSpacing;
    }
}
