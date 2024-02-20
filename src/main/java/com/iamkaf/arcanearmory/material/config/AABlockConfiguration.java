package com.iamkaf.arcanearmory.material.config;

/**
 * Configures how the blocks of a Material are created.
 */
public class AABlockConfiguration {
    public final int miningLevelRequired;
    public final int veinsPerChunk;
    public final int veinSize;
    public final int minYOffset;
    public final int maxYOffset;
    public final float minDrops;
    public final float maxDrops;
    public final boolean spawnInOverworld;
    public final boolean spawnInTheNether;
    public final boolean spawnInTheEnd;

    public AABlockConfiguration(
            int miningLevelRequired,
            int veinsPerChunk,
            int veinSize, int minYOffset, int maxYOffset,
            float minDrops,
            float maxDrops,
            boolean spawnInOverworld,
            boolean spawnInTheNether,
            boolean spawnInTheEnd
    ) {
        this.miningLevelRequired = miningLevelRequired;
        this.veinsPerChunk = veinsPerChunk;
        this.veinSize = veinSize;
        this.minYOffset = minYOffset;
        this.maxYOffset = maxYOffset;
        this.minDrops = minDrops;
        this.maxDrops = maxDrops;
        this.spawnInOverworld = spawnInOverworld;
        this.spawnInTheNether = spawnInTheNether;
        this.spawnInTheEnd = spawnInTheEnd;
    }
}
