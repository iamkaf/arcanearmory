package com.iamkaf.arcanearmory.content;

//? if >=1.21.2 {
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.component.BlocksAttacks;
import net.minecraft.world.level.block.entity.BannerPatternLayers;

import java.util.List;
import java.util.Optional;
//?}
//? if >=1.21.5 {
import net.minecraft.world.item.equipment.ArmorType;
//?} else if >=1.21 {
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.SwordItem;
//?} else if >=1.20 {
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.SwordItem;
//?} else if >=1.19.4 {
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.SwordItem;
//?} else {
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.SwordItem;
//?}
//? if <1.21
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Item;

final class ArcaneArmoryItemFactory {
    private ArcaneArmoryItemFactory() {
    }

    static Item create(ArcaneMaterial material, String id, Item.Properties properties) {
        if (id.endsWith("_sword")) {
            return sword(material, properties);
        }
        if (id.endsWith("_shovel")) {
            return shovel(material, properties);
        }
        if (id.endsWith("_pickaxe")) {
            return pickaxe(material, properties);
        }
        if (id.endsWith("_axe")) {
            return axe(material, properties);
        }
        if (id.endsWith("_hoe")) {
            return hoe(material, properties);
        }
        if (id.endsWith("_helmet")) {
            return armor(material, ArcaneMaterial.ArmorPiece.HELMET, properties);
        }
        if (id.endsWith("_chestplate")) {
            return armor(material, ArcaneMaterial.ArmorPiece.CHESTPLATE, properties);
        }
        if (id.endsWith("_leggings")) {
            return armor(material, ArcaneMaterial.ArmorPiece.LEGGINGS, properties);
        }
        if (id.endsWith("_boots")) {
            return armor(material, ArcaneMaterial.ArmorPiece.BOOTS, properties);
        }
        if (id.endsWith("_hammer")) {
            return hammer(material, properties);
        }
        if (id.endsWith("_bow")) {
            return bow(material, properties);
        }
        if (id.endsWith("_shield")) {
            return shield(material, properties);
        }
        return new Item(properties);
    }

    private static Item sword(ArcaneMaterial material, Item.Properties properties) {
        ArcaneMaterial.ToolStats stats = material.requireToolStats();
        //? if >=1.21.5 {
        return new Item(properties
                .sword(ArcaneEquipmentMaterials.tool(material), stats.swordDamage(), -2.4F)
                .repairable(repairItem(material)));
        //?} else if >=1.21 {
        var tier = ArcaneEquipmentMaterials.tool(material, repairItem(material));
        return new SwordItem(tier, properties.attributes(SwordItem.createAttributes(tier, (int) stats.swordDamage(), -2.4F)));
        //?} else {
        return new SwordItem(ArcaneEquipmentMaterials.tool(material, repairItem(material)),
                (int) stats.swordDamage(), -2.4F, properties);
        //?}
    }

    private static Item shovel(ArcaneMaterial material, Item.Properties properties) {
        //? if >=1.21.5 {
        return new Item(properties
                .shovel(ArcaneEquipmentMaterials.tool(material), 0.0F, -3.0F)
                .repairable(repairItem(material)));
        //?} else if >=1.21 {
        var tier = ArcaneEquipmentMaterials.tool(material, repairItem(material));
        return new ShovelItem(tier, properties.attributes(DiggerItem.createAttributes(tier, 0.0F, -3.0F)));
        //?} else {
        return new ShovelItem(ArcaneEquipmentMaterials.tool(material, repairItem(material)), 0.0F, -3.0F, properties);
        //?}
    }

    private static Item pickaxe(ArcaneMaterial material, Item.Properties properties) {
        //? if >=1.21.5 {
        return new Item(properties
                .pickaxe(ArcaneEquipmentMaterials.tool(material), 0.0F, -3.0F)
                .repairable(repairItem(material)));
        //?} else if >=1.21 {
        var tier = ArcaneEquipmentMaterials.tool(material, repairItem(material));
        return new PickaxeItem(tier, properties.attributes(DiggerItem.createAttributes(tier, 0.0F, -3.0F)));
        //?} else {
        return new PublicPickaxeItem(ArcaneEquipmentMaterials.tool(material, repairItem(material)), 0, -3.0F, properties);
        //?}
    }

    private static Item axe(ArcaneMaterial material, Item.Properties properties) {
        ArcaneMaterial.ToolStats stats = material.requireToolStats();
        //? if >=1.21.5 {
        return new Item(properties
                .axe(ArcaneEquipmentMaterials.tool(material), stats.axeDamage(), -3.0F)
                .repairable(repairItem(material)));
        //?} else if >=1.21 {
        var tier = ArcaneEquipmentMaterials.tool(material, repairItem(material));
        return new AxeItem(tier, properties.attributes(DiggerItem.createAttributes(tier, stats.axeDamage(), -3.0F)));
        //?} else {
        return new PublicAxeItem(ArcaneEquipmentMaterials.tool(material, repairItem(material)),
                stats.axeDamage(), -3.0F, properties);
        //?}
    }

    private static Item hoe(ArcaneMaterial material, Item.Properties properties) {
        //? if >=1.21.5 {
        return new Item(properties
                .hoe(ArcaneEquipmentMaterials.tool(material), 0.0F, -3.0F)
                .repairable(repairItem(material)));
        //?} else if >=1.21 {
        var tier = ArcaneEquipmentMaterials.tool(material, repairItem(material));
        return new HoeItem(tier, properties.attributes(DiggerItem.createAttributes(tier, 0.0F, -3.0F)));
        //?} else {
        return new PublicHoeItem(ArcaneEquipmentMaterials.tool(material, repairItem(material)), 0, -3.0F, properties);
        //?}
    }

    private static Item hammer(ArcaneMaterial material, Item.Properties properties) {
        ArcaneMaterial.ToolStats stats = material.requireToolStats();
        //? if >=1.21.5 {
        return new ArcaneHammerItem(properties
                .pickaxe(ArcaneEquipmentMaterials.tool(material), stats.swordDamage(), -3.0F)
                .repairable(repairItem(material)));
        //?} else if >=1.21 {
        var tier = ArcaneEquipmentMaterials.tool(material, repairItem(material));
        return new ArcaneHammerItem(tier,
                properties.attributes(DiggerItem.createAttributes(tier, stats.swordDamage(), -3.0F)));
        //?} else {
        return new ArcaneHammerItem(ArcaneEquipmentMaterials.tool(material, repairItem(material)),
                (int) stats.swordDamage(), -3.0F, properties);
        //?}
    }

    private static Item armor(ArcaneMaterial material, ArcaneMaterial.ArmorPiece piece, Item.Properties properties) {
        int durability = material.requireArmorStats().durability(piece);
        //? if >=1.21.5 {
        return new Item(properties
                .humanoidArmor(ArcaneEquipmentMaterials.armor(material), armorType(piece))
                .durability(durability)
                .repairable(repairItem(material)));
        //?} else if >=1.21 {
        properties.durability(durability);
        return new ArmorItem(ArcaneEquipmentMaterials.armor(material, repairItem(material)), armorType(piece), properties);
        //?} else if >=1.19.4 {
        return new ArmorItem(ArcaneEquipmentMaterials.armor(material, repairItem(material)), armorType(piece), properties);
        //?} else {
        return new ArmorItem(ArcaneEquipmentMaterials.armor(material, repairItem(material)), equipmentSlot(piece), properties);
        //?}
    }

    private static Item shield(ArcaneMaterial material, Item.Properties properties) {
        ArcaneMaterial.ToolStats stats = material.requireToolStats();
        //? if >=1.21.2 {
        properties
                .enchantable(stats.enchantmentValue())
                .repairable(repairItem(material))
                .component(DataComponents.BANNER_PATTERNS, BannerPatternLayers.EMPTY)
                .equippableUnswappable(EquipmentSlot.OFFHAND)
                //? if >=26 {
                .delayedComponent(
                        DataComponents.BLOCKS_ATTACKS,
                        context -> new BlocksAttacks(
                                0.25F,
                                1.0F,
                                List.of(new BlocksAttacks.DamageReduction(90.0F, Optional.empty(), 0.0F, 1.0F)),
                                new BlocksAttacks.ItemDamageFunction(3.0F, 1.0F, 1.0F),
                                Optional.of(context.getOrThrow(DamageTypeTags.BYPASSES_SHIELD)),
                                Optional.of(SoundEvents.SHIELD_BLOCK),
                                Optional.of(SoundEvents.SHIELD_BREAK)
                        )
                )
                //?} else {
                /*.component(
                        DataComponents.BLOCKS_ATTACKS,
                        new BlocksAttacks(
                                0.25F,
                                1.0F,
                                List.of(new BlocksAttacks.DamageReduction(90.0F, Optional.empty(), 0.0F, 1.0F)),
                                new BlocksAttacks.ItemDamageFunction(3.0F, 1.0F, 1.0F),
                                Optional.of(DamageTypeTags.BYPASSES_SHIELD),
                                Optional.of(SoundEvents.SHIELD_BLOCK),
                                Optional.of(SoundEvents.SHIELD_BREAK)
                        )
                )*/
                //?}
                .component(DataComponents.BREAK_SOUND, SoundEvents.SHIELD_BREAK);
        //?}
        return new ArcaneShieldItem(stats.enchantmentValue(), repairItem(material), properties);
    }

    private static Item bow(ArcaneMaterial material, Item.Properties properties) {
        ArcaneMaterial.ToolStats stats = material.requireToolStats();
        //? if >=1.21.2 {
        properties.enchantable(stats.enchantmentValue()).repairable(repairItem(material));
        //?}
        return new ArcaneBowItem(material.bowDamage(), stats.enchantmentValue(), repairItem(material), properties);
    }

    private static Item repairItem(ArcaneMaterial material) {
        return ArcaneArmoryContent.item(material.materialItemId())
                .orElseThrow(() -> new IllegalStateException("Missing repair item for " + material.id()))
                .get();
    }

    //? if >=1.19.4 && <1.21.5 {
    private static ArmorItem.Type armorType(ArcaneMaterial.ArmorPiece piece) {
        return switch (piece) {
            case HELMET -> ArmorItem.Type.HELMET;
            case CHESTPLATE -> ArmorItem.Type.CHESTPLATE;
            case LEGGINGS -> ArmorItem.Type.LEGGINGS;
            case BOOTS -> ArmorItem.Type.BOOTS;
        };
    }
    //?}

    //? if >=1.21.5 {
    private static ArmorType armorType(ArcaneMaterial.ArmorPiece piece) {
        return switch (piece) {
            case HELMET -> ArmorType.HELMET;
            case CHESTPLATE -> ArmorType.CHESTPLATE;
            case LEGGINGS -> ArmorType.LEGGINGS;
            case BOOTS -> ArmorType.BOOTS;
        };
    }
    //?}

    //? if <1.19.4 {
    private static EquipmentSlot equipmentSlot(ArcaneMaterial.ArmorPiece piece) {
        return switch (piece) {
            case HELMET -> EquipmentSlot.HEAD;
            case CHESTPLATE -> EquipmentSlot.CHEST;
            case LEGGINGS -> EquipmentSlot.LEGS;
            case BOOTS -> EquipmentSlot.FEET;
        };
    }
    //?}

    //? if <1.21 {
    private static final class PublicPickaxeItem extends PickaxeItem {
        private PublicPickaxeItem(Tier tier, int attackDamage, float attackSpeed, Item.Properties properties) {
            super(tier, attackDamage, attackSpeed, properties);
        }
    }

    private static final class PublicAxeItem extends AxeItem {
        private PublicAxeItem(Tier tier, float attackDamage, float attackSpeed, Item.Properties properties) {
            super(tier, attackDamage, attackSpeed, properties);
        }
    }

    private static final class PublicHoeItem extends HoeItem {
        private PublicHoeItem(Tier tier, int attackDamage, float attackSpeed, Item.Properties properties) {
            super(tier, attackDamage, attackSpeed, properties);
        }
    }
    //?}
}
