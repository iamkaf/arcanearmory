package com.iamkaf.arcanearmory.content;

//? if <1.21 {
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
//?} else if >=1.21.10 {
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
//?} else {
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
//?}
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.Item;
//? if <1.21.2
import net.minecraft.world.item.ItemStack;

public class ArcaneBowItem extends BowItem {
    private final double baseArrowDamage;
    private final int enchantmentValue;
    private final Item repairItem;

    public ArcaneBowItem(float configuredDamage, int enchantmentValue, Item repairItem, Properties properties) {
        super(properties);
        this.baseArrowDamage = Math.max(0.0D, configuredDamage / 3.0D);
        this.enchantmentValue = enchantmentValue;
        this.repairItem = repairItem;
    }

    //? if >=1.21 {
    @Override
    protected void shootProjectile(
            LivingEntity shooter,
            Projectile projectileEntity,
            int index,
            float power,
            float uncertainty,
            float angle,
            LivingEntity targetOverride
    ) {
        applyDamageBonus(projectileEntity);
        projectileEntity.shootFromRotation(shooter, shooter.getXRot(), shooter.getYRot() + angle, 0.0F, power, uncertainty);
    }
    //?} else {
    /*@Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int remainingUseTicks) {
        if (!(entity instanceof Player player)) {
            return;
        }

        boolean hasInfiniteArrows = player.getAbilities().instabuild
                || EnchantmentHelper.getItemEnchantmentLevel(Enchantments.INFINITY_ARROWS, stack) > 0;
        ItemStack projectile = player.getProjectile(stack);
        if (projectile.isEmpty() && !hasInfiniteArrows) {
            return;
        }
        if (projectile.isEmpty()) {
            projectile = new ItemStack(Items.ARROW);
        }

        int chargeTicks = this.getUseDuration(stack) - remainingUseTicks;
        float power = getPowerForTime(chargeTicks);
        if (power < 0.1F) {
            return;
        }

        boolean creativePickup = hasInfiniteArrows && projectile.is(Items.ARROW);
        if (!level.isClientSide) {
            ArrowItem arrowItem = projectile.getItem() instanceof ArrowItem ? (ArrowItem) projectile.getItem() : (ArrowItem) Items.ARROW;
            AbstractArrow arrow = arrowItem.createArrow(level, projectile, player);
            arrow.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, power * 3.0F, 1.0F);
            if (power == 1.0F) {
                arrow.setCritArrow(true);
            }

            arrow.setBaseDamage(this.baseArrowDamage);

            int powerLevel = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.POWER_ARROWS, stack);
            if (powerLevel > 0) {
                arrow.setBaseDamage(arrow.getBaseDamage() + (double) powerLevel * 0.5D + 0.5D);
            }

            int punchLevel = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.PUNCH_ARROWS, stack);
            if (punchLevel > 0) {
                arrow.setKnockback(punchLevel);
            }
            if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.FLAMING_ARROWS, stack) > 0) {
                arrow.setSecondsOnFire(100);
            }

            stack.hurtAndBreak(1, player, brokenPlayer -> brokenPlayer.broadcastBreakEvent(player.getUsedItemHand()));
            if (creativePickup || player.getAbilities().instabuild && (projectile.is(Items.SPECTRAL_ARROW) || projectile.is(Items.TIPPED_ARROW))) {
                arrow.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
            }
            level.addFreshEntity(arrow);
        }

        level.playSound(
                null,
                player.getX(),
                player.getY(),
                player.getZ(),
                SoundEvents.ARROW_SHOOT,
                SoundSource.PLAYERS,
                1.0F,
                1.0F / (level.getRandom().nextFloat() * 0.4F + 1.2F) + power * 0.5F
        );
        if (!creativePickup && !player.getAbilities().instabuild) {
            projectile.shrink(1);
            if (projectile.isEmpty()) {
                player.getInventory().removeItem(projectile);
            }
        }
        player.awardStat(Stats.ITEM_USED.get(this));
    }*/
    //?}

    private void applyDamageBonus(Object projectileEntity) {
        if (projectileEntity instanceof AbstractArrow arrow) {
            arrow.setBaseDamage(this.baseArrowDamage);
        }
    }

    //? if <1.21.2 {
    @Override
    public int getEnchantmentValue() {
        return this.enchantmentValue;
    }

    @Override
    public boolean isValidRepairItem(ItemStack stack, ItemStack ingredient) {
        return ingredient.is(this.repairItem);
    }
    //?}
}
