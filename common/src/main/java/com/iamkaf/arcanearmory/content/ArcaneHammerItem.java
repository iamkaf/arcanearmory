package com.iamkaf.arcanearmory.content;

//? if >=1.21.5 {
import net.minecraft.world.item.Item;
//?} else if >=1.21 {
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.Tier;
//?} else {
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Tier;
//?}

//? if >=1.21.5 {
public class ArcaneHammerItem extends Item {
    public ArcaneHammerItem(Properties properties) {
        super(properties);
    }
}
//?} else if >=1.21 {
public class ArcaneHammerItem extends DiggerItem {
    public ArcaneHammerItem(Tier tier, Properties properties) {
        super(tier, BlockTags.MINEABLE_WITH_PICKAXE, properties);
    }
}
//?} else {
public class ArcaneHammerItem extends PickaxeItem {
    public ArcaneHammerItem(Tier tier, int attackDamage, float attackSpeed, Properties properties) {
        super(tier, attackDamage, attackSpeed, properties);
    }
}
//?}
