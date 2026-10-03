package com.iamkaf.arcanearmory.meteor;

import com.iamkaf.amber.api.registry.v1.RegistrySupplier;
import com.iamkaf.arcanearmory.content.ArcaneArmoryContent;
import net.minecraft.core.BlockPos;
//? if >=1.19 {
import net.minecraft.util.RandomSource;
//?} else {
/*import java.util.Random;
*///?}
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Predicate;

/** A rough ball of Meteorite around a core of Star Corundum ore, left by crater and falling meteors alike. */
public final class MeteorBoulder {
    public static final int RADIUS = 2;

    private MeteorBoulder() {
    }

    /** Places the boulder around {@code center}, writing only where {@code canReplace} accepts the existing block. */
    //? if >=1.19 {
    public static void place(WorldGenLevel level, BlockPos center, RandomSource random, Predicate<BlockState> canReplace) {
    //?} else {
    /*public static void place(WorldGenLevel level, BlockPos center, Random random, Predicate<BlockState> canReplace) {
    *///?}
        BlockState shell = block("meteorite");
        BlockState core = block("star_corundum_ore");
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int dx = -RADIUS; dx <= RADIUS; dx++) {
            for (int dy = -RADIUS; dy <= RADIUS; dy++) {
                for (int dz = -RADIUS; dz <= RADIUS; dz++) {
                    int distance = dx * dx + dy * dy + dz * dz;
                    // A jittered edge keeps every boulder a little different.
                    if (distance > RADIUS * RADIUS + random.nextInt(3)) {
                        continue;
                    }
                    boolean isCore = distance == 0 || distance == 1 && random.nextInt(5) < 2;
                    pos.setWithOffset(center, dx, dy, dz);
                    if (canReplace.test(level.getBlockState(pos))) {
                        level.setBlock(pos, isCore ? core : shell, Block.UPDATE_CLIENTS);
                    }
                }
            }
        }
    }

    private static BlockState block(String id) {
        return ArcaneArmoryContent.block(id)
                .map(RegistrySupplier::get)
                .orElseThrow(() -> new IllegalStateException("Missing Arcane Armory block " + id))
                .defaultBlockState();
    }
}
