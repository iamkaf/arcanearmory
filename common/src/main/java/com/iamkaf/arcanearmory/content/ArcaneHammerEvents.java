package com.iamkaf.arcanearmory.content;

import com.iamkaf.amber.api.event.v1.events.common.BlockEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public final class ArcaneHammerEvents {
    private static final ThreadLocal<Boolean> BREAKING_EXTRA_BLOCKS = ThreadLocal.withInitial(() -> false);

    private ArcaneHammerEvents() {
    }

    public static void init() {
        BlockEvents.BLOCK_BREAK_BEFORE.register(ArcaneHammerEvents::breakHammerPlane);
    }

    private static InteractionResult breakHammerPlane(
            Level level,
            Player player,
            BlockPos origin,
            BlockState originState,
            BlockEntity blockEntity
    ) {
        if (level.isClientSide() || BREAKING_EXTRA_BLOCKS.get()) {
            return InteractionResult.PASS;
        }

        ItemStack hammer = player.getMainHandItem();
        if (!isHammer(hammer)) {
            return InteractionResult.PASS;
        }

        Direction face = clickedFace(player, origin);
        BREAKING_EXTRA_BLOCKS.set(true);
        try {
            for (BlockPos target : planeAround(origin, face.getAxis())) {
                if (target.equals(origin)) {
                    continue;
                }
                breakExtraBlock(level, player, hammer, target);
            }
        } finally {
            BREAKING_EXTRA_BLOCKS.set(false);
        }
        return InteractionResult.PASS;
    }

    private static boolean isHammer(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() instanceof ArcaneHammerItem;
    }

    private static Direction clickedFace(Player player, BlockPos origin) {
        double dx = origin.getX() + 0.5D - player.getX();
        double dy = origin.getY() + 0.5D - player.getEyeY();
        double dz = origin.getZ() + 0.5D - player.getZ();
        double absX = Math.abs(dx);
        double absY = Math.abs(dy);
        double absZ = Math.abs(dz);

        if (absY > absX && absY > absZ) {
            return dy > 0.0D ? Direction.UP : Direction.DOWN;
        }
        if (absZ > absX) {
            return dz > 0.0D ? Direction.SOUTH : Direction.NORTH;
        }
        return dx > 0.0D ? Direction.EAST : Direction.WEST;
    }

    private static BlockPos[] planeAround(BlockPos origin, Direction.Axis axis) {
        BlockPos[] positions = new BlockPos[9];
        int index = 0;
        for (int a = -1; a <= 1; a++) {
            for (int b = -1; b <= 1; b++) {
                positions[index++] = switch (axis) {
                    case X -> origin.offset(0, a, b);
                    case Y -> origin.offset(a, 0, b);
                    case Z -> origin.offset(a, b, 0);
                };
            }
        }
        return positions;
    }

    private static void breakExtraBlock(Level level, Player player, ItemStack hammer, BlockPos target) {
        BlockState state = level.getBlockState(target);
        if (state.isAir() || state.getDestroySpeed(level, target) < 0.0F) {
            return;
        }

        if (player.isCreative()) {
            level.setBlockAndUpdate(target, Blocks.AIR.defaultBlockState());
            return;
        }

        if (hammer.isDamageableItem() && hammer.getMaxDamage() - hammer.getDamageValue() <= 1) {
            return;
        }

        boolean brokeBlock = level.destroyBlock(target, true, player, 512);
        if (brokeBlock && hammer.isDamageableItem() && state.getDestroySpeed(level, target) != 0.0F) {
            damageHammer(hammer, player);
        }
    }

    private static void damageHammer(ItemStack hammer, Player player) {
        //? if >=1.21 {
        hammer.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
        //?} else {
        hammer.hurtAndBreak(1, player, brokenPlayer -> brokenPlayer.broadcastBreakEvent(EquipmentSlot.MAINHAND));
        //?}
    }
}
