package com.iamkaf.arcanearmory.block.custom;

import net.minecraft.block.Block;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DoomflareBlock extends Block {
    public DoomflareBlock(Settings settings) {
        super(settings);
    }

    @Override
    public void appendTooltip(
            ItemStack stack, @Nullable BlockView world, List<Text> tooltip, TooltipContext options
    ) {
        tooltip.add(Text
                .literal(
                        "If destroyed by an explosion, this block will explode with the force of 10 TNT.")
                .formatted(Formatting.GOLD));
    }

    @Override
    public void onDestroyedByExplosion(World world, BlockPos pos, Explosion explosion) {
        super.onDestroyedByExplosion(world, pos, explosion);

        if (!world.isClient()) {
            world.createExplosion(
                    null,
                    pos.getX(),
                    pos.getY(),
                    pos.getZ(),
                    40f,
                    false,
                    World.ExplosionSourceType.BLOCK
            );
        }
    }
}
