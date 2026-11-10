package com.iamkaf.arcanearmory.content;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
//? if >=1.21.5 {
import net.minecraft.world.item.Item;
//?} else if >=1.21 {
/*import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.Tier;
*///?} else {
/*import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Tier;
*///?}

/**
 * Mines the 3x3 plane facing the player, like the original Magna hammers: only blocks the hammer can
 * harvest break, sneaking mines a single block, and every extra block is a normal player break, so
 * drops, enchantments, durability, and protection checks all behave as if mined one by one.
 */
//? if >=1.21.5 {
public class ArcaneHammerItem extends Item {
    public ArcaneHammerItem(Properties properties) {
        super(properties);
    }
//?} else if >=1.21 {
/*public class ArcaneHammerItem extends DiggerItem {
    public ArcaneHammerItem(Tier tier, Properties properties) {
        super(tier, BlockTags.MINEABLE_WITH_PICKAXE, properties);
    }
*///?} else {
/*public class ArcaneHammerItem extends PickaxeItem {
    public ArcaneHammerItem(Tier tier, int attackDamage, float attackSpeed, Properties properties) {
        super(tier, attackDamage, attackSpeed, properties);
    }
*///?}

    private static final double FACE_TRACE_DISTANCE = 8.0D;
    private static final ThreadLocal<Boolean> MINING_PLANE = ThreadLocal.withInitial(() -> false);

    // Vanilla calls this after the origin block is removed and before its drops spawn.
    @Override
    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity miner) {
        boolean mined = super.mineBlock(stack, level, state, pos, miner);
        if (miner instanceof ServerPlayer player && !player.isShiftKeyDown() && !MINING_PLANE.get()) {
            MINING_PLANE.set(true);
            try {
                minePlane(stack, level, player, pos);
            } finally {
                MINING_PLANE.set(false);
            }
        }
        return mined;
    }

    private static void minePlane(ItemStack hammer, Level level, ServerPlayer player, BlockPos origin) {
        Direction.Axis axis = minedFace(player, origin).getAxis();
        for (BlockPos target : planeAround(origin, axis)) {
            if (hammer.isEmpty() || player.getMainHandItem() != hammer) {
                return;
            }
            BlockState state = level.getBlockState(target);
            if (!state.isAir() && hammer.isCorrectToolForDrops(state)) {
                player.gameMode.destroyBlock(target);
            }
        }
    }

    private static Direction minedFace(ServerPlayer player, BlockPos origin) {
        Vec3 eye = player.getEyePosition();
        Vec3 look = player.getViewVector(1.0F);
        BlockHitResult hit = Shapes.block().clip(eye, eye.add(look.scale(FACE_TRACE_DISTANCE)), origin);
        if (hit != null) {
            return hit.getDirection();
        }
        //? if >=1.21.11 {
        return Direction.getApproximateNearest(look.x, look.y, look.z).getOpposite();
        //?} else {
        /*return Direction.getNearest(look.x, look.y, look.z).getOpposite();
        *///?}
    }

    private static BlockPos[] planeAround(BlockPos origin, Direction.Axis axis) {
        BlockPos[] positions = new BlockPos[8];
        int index = 0;
        for (int a = -1; a <= 1; a++) {
            for (int b = -1; b <= 1; b++) {
                if (a == 0 && b == 0) {
                    continue;
                }
                positions[index++] = switch (axis) {
                    case X -> origin.offset(0, a, b);
                    case Y -> origin.offset(a, 0, b);
                    case Z -> origin.offset(a, b, 0);
                };
            }
        }
        return positions;
    }
}
