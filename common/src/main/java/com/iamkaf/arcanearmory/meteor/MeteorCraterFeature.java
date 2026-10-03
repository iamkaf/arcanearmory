package com.iamkaf.arcanearmory.meteor;

import com.iamkaf.amber.api.registry.v1.RegistrySupplier;
import com.iamkaf.arcanearmory.content.ArcaneArmoryContent;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
//? if >=1.19 {
import net.minecraft.util.RandomSource;
//?} else {
/*import java.util.Random;
*///?}
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
//? if >=26.3 {
/*import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.chunk.ChunkGenerator;
*///?} else {
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
//?}

/**
 * A small crater with a Meteorite boulder in its bowl. The origin is the first open block above dry
 * ground, as the surface heightmap placement or {@code /place feature} supplies it.
 */
//? if >=26.3 {
/*public record MeteorCraterFeature() implements Feature {
    public static final MapCodec<MeteorCraterFeature> CODEC = MapCodec.unit(MeteorCraterFeature::new);

    @Override
    public MapCodec<MeteorCraterFeature> codec() {
        return CODEC;
    }

    @Override
    public boolean place(WorldGenLevel level, ChunkGenerator chunkGenerator, RandomSource random, BlockPos origin) {
        return placeCrater(level, random, origin);
    }
*///?} else {
public final class MeteorCraterFeature extends Feature<NoneFeatureConfiguration> {
    public MeteorCraterFeature() {
        super(NoneFeatureConfiguration.CODEC);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        return placeCrater(context.level(), context.random(), context.origin());
    }
//?}

    //? if >=1.19 {
    private static boolean placeCrater(WorldGenLevel level, RandomSource random, BlockPos origin) {
    //?} else {
    /*private static boolean placeCrater(WorldGenLevel level, Random random, BlockPos origin) {
    *///?}
        int radius = 4 + random.nextInt(2);
        int depth = radius - 2;
        BlockPos ground = origin.below();
        if (level.isEmptyBlock(ground) || !isDry(level, ground, radius + 1, depth)) {
            return false;
        }

        BlockState meteorite = ArcaneArmoryContent.block("meteorite")
                .map(RegistrySupplier::get)
                .orElseThrow(() -> new IllegalStateException("Missing Arcane Armory block meteorite"))
                .defaultBlockState();
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int dx = -radius - 1; dx <= radius + 1; dx++) {
            for (int dz = -radius - 1; dz <= radius + 1; dz++) {
                int distance = dx * dx + dz * dz;
                if (distance <= radius * radius) {
                    // The bowl is deepest in the middle and shallows to nothing at the edge.
                    int columnDepth = Math.round(depth * (1.0F - distance / (float) (radius * radius)));
                    for (int dy = 1 - columnDepth; dy <= 3; dy++) {
                        carve(level, pos.setWithOffset(ground, dx, dy, dz));
                    }
                    // Scorched fragments litter the floor.
                    pos.setWithOffset(ground, dx, -columnDepth, dz);
                    if (!level.isEmptyBlock(pos) && random.nextInt(4) == 0 && replaceable(level.getBlockState(pos))) {
                        level.setBlock(pos, meteorite, Block.UPDATE_CLIENTS);
                    }
                } else if (distance <= (radius + 1) * (radius + 1) && random.nextInt(4) != 0) {
                    // Thrown-out ground piles up into a low rim.
                    BlockState rim = level.getBlockState(pos.setWithOffset(ground, dx, 0, dz));
                    if (!rim.isAir() && replaceable(rim) && level.isEmptyBlock(pos.move(0, 1, 0))) {
                        level.setBlock(pos, rim, Block.UPDATE_CLIENTS);
                    }
                }
            }
        }

        MeteorBoulder.place(level, ground.below(depth - 1), random, MeteorCraterFeature::replaceable);
        return true;
    }

    // Lakes, rivers, and oceans would flood the bowl, so craters only form on dry land.
    private static boolean isDry(WorldGenLevel level, BlockPos ground, int reach, int depth) {
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int dx = -reach; dx <= reach; dx++) {
            for (int dz = -reach; dz <= reach; dz++) {
                for (int dy = -depth; dy <= 1; dy++) {
                    if (!level.getFluidState(pos.setWithOffset(ground, dx, dy, dz)).isEmpty()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private static void carve(WorldGenLevel level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (!state.isAir() && replaceable(state)) {
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_CLIENTS);
        }
    }

    private static boolean replaceable(BlockState state) {
        return !state.is(BlockTags.FEATURES_CANNOT_REPLACE);
    }
}
