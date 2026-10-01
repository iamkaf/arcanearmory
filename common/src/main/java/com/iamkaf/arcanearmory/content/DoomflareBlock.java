package com.iamkaf.arcanearmory.content;

import net.minecraft.core.BlockPos;
//? if >=1.21.2
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

/** Explodes with ten times the force of TNT when another explosion destroys it. */
public class DoomflareBlock extends Block {
    public static final float EXPLOSION_POWER = 40.0F;

    public DoomflareBlock(Properties properties) {
        super(properties);
    }

    //? if >=1.21.2 {
    @Override
    public void wasExploded(ServerLevel level, BlockPos pos, Explosion explosion) {
        super.wasExploded(level, pos, explosion);
        explode(level, pos);
    }
    //?} else {
    /*@Override
    public void wasExploded(Level level, BlockPos pos, Explosion explosion) {
        super.wasExploded(level, pos, explosion);
        if (!level.isClientSide()) {
            explode(level, pos);
        }
    }
    *///?}

    private static void explode(Level level, BlockPos pos) {
        double x = pos.getX() + 0.5D;
        double y = pos.getY() + 0.5D;
        double z = pos.getZ() + 0.5D;
        //? if >=1.19.3 {
        level.explode(null, x, y, z, EXPLOSION_POWER, Level.ExplosionInteraction.BLOCK);
        //?} else {
        /*level.explode(null, x, y, z, EXPLOSION_POWER, Explosion.BlockInteraction.DESTROY);
        *///?}
    }
}
