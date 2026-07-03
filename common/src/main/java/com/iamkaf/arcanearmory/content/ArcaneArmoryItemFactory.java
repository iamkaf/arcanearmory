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
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.equipment.ArmorMaterials;
import net.minecraft.world.item.equipment.ArmorType;
//?} else if >=1.21 {
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
//?} else if >=1.20 {
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
//?} else if >=1.19.4 {
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
//?} else {
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
//?}
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
            return armor("helmet", properties);
        }
        if (id.endsWith("_chestplate")) {
            return armor("chestplate", properties);
        }
        if (id.endsWith("_leggings")) {
            return armor("leggings", properties);
        }
        if (id.endsWith("_boots")) {
            return armor("boots", properties);
        }
        if (id.endsWith("_hammer")) {
            return hammer(material, properties);
        }
        if (id.endsWith("_bow")) {
            return new ArcaneBowItem(material.bowDamageBonus(), properties);
        }
        if (id.endsWith("_shield")) {
            return shield(material, properties);
        }
        return new Item(properties);
    }

    private static Item sword(ArcaneMaterial material, Item.Properties properties) {
        //? if >=1.21.5 {
        return new Item(properties.sword(toolMaterial(material), 3.0F + material.attackDamageBonus(), -2.4F));
        //?} else if >=1.21 {
        return new SwordItem(Tiers.DIAMOND, properties.attributes(SwordItem.createAttributes(Tiers.DIAMOND, 3 + material.attackDamageBonus(), -2.4F)));
        //?} else {
        return new SwordItem(Tiers.DIAMOND, 3 + material.attackDamageBonus(), -2.4F, properties);
        //?}
    }

    private static Item shovel(ArcaneMaterial material, Item.Properties properties) {
        //? if >=1.21.5 {
        return new Item(properties.shovel(toolMaterial(material), 1.5F + material.attackDamageBonus(), -3.0F));
        //?} else if >=1.21 {
        return new ShovelItem(Tiers.DIAMOND, properties.attributes(DiggerItem.createAttributes(Tiers.DIAMOND, 1.5F + material.attackDamageBonus(), -3.0F)));
        //?} else {
        return new ShovelItem(Tiers.DIAMOND, 1.5F + material.attackDamageBonus(), -3.0F, properties);
        //?}
    }

    private static Item pickaxe(ArcaneMaterial material, Item.Properties properties) {
        //? if >=1.21.5 {
        return new Item(properties.pickaxe(toolMaterial(material), 1.0F + material.attackDamageBonus(), -2.8F));
        //?} else if >=1.21 {
        return new PickaxeItem(Tiers.DIAMOND, properties.attributes(DiggerItem.createAttributes(Tiers.DIAMOND, 1.0F + material.attackDamageBonus(), -2.8F)));
        //?} else {
        return new PublicPickaxeItem(Tiers.DIAMOND, 1 + material.attackDamageBonus(), -2.8F, properties);
        //?}
    }

    private static Item axe(ArcaneMaterial material, Item.Properties properties) {
        //? if >=1.21.5 {
        return new Item(properties.axe(toolMaterial(material), 5.0F + material.attackDamageBonus(), -3.0F));
        //?} else if >=1.21 {
        return new AxeItem(Tiers.DIAMOND, properties.attributes(DiggerItem.createAttributes(Tiers.DIAMOND, 5.0F + material.attackDamageBonus(), -3.0F)));
        //?} else {
        return new PublicAxeItem(Tiers.DIAMOND, 5.0F + material.attackDamageBonus(), -3.0F, properties);
        //?}
    }

    private static Item hoe(ArcaneMaterial material, Item.Properties properties) {
        //? if >=1.21.5 {
        return new Item(properties.hoe(toolMaterial(material), -3.0F + material.attackDamageBonus(), 0.0F));
        //?} else if >=1.21 {
        return new HoeItem(Tiers.DIAMOND, properties.attributes(DiggerItem.createAttributes(Tiers.DIAMOND, -3.0F + material.attackDamageBonus(), 0.0F)));
        //?} else {
        return new PublicHoeItem(Tiers.DIAMOND, -3 + material.attackDamageBonus(), 0.0F, properties);
        //?}
    }

    private static Item hammer(ArcaneMaterial material, Item.Properties properties) {
        //? if >=1.21.5 {
        return new ArcaneHammerItem(properties.pickaxe(toolMaterial(material), 4.0F + material.attackDamageBonus(), -3.2F));
        //?} else if >=1.21 {
        return new ArcaneHammerItem(Tiers.DIAMOND, properties.attributes(DiggerItem.createAttributes(Tiers.DIAMOND, 4.0F + material.attackDamageBonus(), -3.2F)));
        //?} else {
        return new ArcaneHammerItem(Tiers.DIAMOND, 4 + material.attackDamageBonus(), -3.2F, properties);
        //?}
    }

    private static Item armor(String slot, Item.Properties properties) {
        //? if >=1.21.5 {
        return switch (slot) {
            case "helmet" -> new Item(properties.humanoidArmor(ArmorMaterials.DIAMOND, ArmorType.HELMET));
            case "chestplate" -> new Item(properties.humanoidArmor(ArmorMaterials.DIAMOND, ArmorType.CHESTPLATE));
            case "leggings" -> new Item(properties.humanoidArmor(ArmorMaterials.DIAMOND, ArmorType.LEGGINGS));
            case "boots" -> new Item(properties.humanoidArmor(ArmorMaterials.DIAMOND, ArmorType.BOOTS));
            default -> new Item(properties);
        };
        //?} else if >=1.21 {
        return switch (slot) {
            case "helmet" -> new ArmorItem(ArmorMaterials.DIAMOND, ArmorItem.Type.HELMET, properties);
            case "chestplate" -> new ArmorItem(ArmorMaterials.DIAMOND, ArmorItem.Type.CHESTPLATE, properties);
            case "leggings" -> new ArmorItem(ArmorMaterials.DIAMOND, ArmorItem.Type.LEGGINGS, properties);
            case "boots" -> new ArmorItem(ArmorMaterials.DIAMOND, ArmorItem.Type.BOOTS, properties);
            default -> new Item(properties);
        };
        //?} else if >=1.20 {
        return switch (slot) {
            case "helmet" -> new ArmorItem(ArmorMaterials.DIAMOND, ArmorItem.Type.HELMET, properties);
            case "chestplate" -> new ArmorItem(ArmorMaterials.DIAMOND, ArmorItem.Type.CHESTPLATE, properties);
            case "leggings" -> new ArmorItem(ArmorMaterials.DIAMOND, ArmorItem.Type.LEGGINGS, properties);
            case "boots" -> new ArmorItem(ArmorMaterials.DIAMOND, ArmorItem.Type.BOOTS, properties);
            default -> new Item(properties);
        };
        //?} else if >=1.19.4 {
        return switch (slot) {
            case "helmet" -> new ArmorItem(ArmorMaterials.DIAMOND, ArmorItem.Type.HELMET, properties);
            case "chestplate" -> new ArmorItem(ArmorMaterials.DIAMOND, ArmorItem.Type.CHESTPLATE, properties);
            case "leggings" -> new ArmorItem(ArmorMaterials.DIAMOND, ArmorItem.Type.LEGGINGS, properties);
            case "boots" -> new ArmorItem(ArmorMaterials.DIAMOND, ArmorItem.Type.BOOTS, properties);
            default -> new Item(properties);
        };
        //?} else {
        return switch (slot) {
            case "helmet" -> new ArmorItem(ArmorMaterials.DIAMOND, EquipmentSlot.HEAD, properties);
            case "chestplate" -> new ArmorItem(ArmorMaterials.DIAMOND, EquipmentSlot.CHEST, properties);
            case "leggings" -> new ArmorItem(ArmorMaterials.DIAMOND, EquipmentSlot.LEGS, properties);
            case "boots" -> new ArmorItem(ArmorMaterials.DIAMOND, EquipmentSlot.FEET, properties);
            default -> new Item(properties);
        };
        //?}
    }

    private static Item shield(ArcaneMaterial material, Item.Properties properties) {
        //? if >=1.21.2 {
        properties
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
        return new ArcaneShieldItem(properties);
    }

    //? if >=1.21.5 {
    private static ToolMaterial toolMaterial(ArcaneMaterial material) {
        return material.toolDurability() > ToolMaterial.DIAMOND.durability() ? ToolMaterial.NETHERITE : ToolMaterial.DIAMOND;
    }
    //?}

    //? if <1.21 {
    private static final class PublicPickaxeItem extends PickaxeItem {
        private PublicPickaxeItem(Tiers tier, int attackDamage, float attackSpeed, Item.Properties properties) {
            super(tier, attackDamage, attackSpeed, properties);
        }
    }

    private static final class PublicAxeItem extends AxeItem {
        private PublicAxeItem(Tiers tier, float attackDamage, float attackSpeed, Item.Properties properties) {
            super(tier, attackDamage, attackSpeed, properties);
        }
    }

    private static final class PublicHoeItem extends HoeItem {
        private PublicHoeItem(Tiers tier, int attackDamage, float attackSpeed, Item.Properties properties) {
            super(tier, attackDamage, attackSpeed, properties);
        }
    }
    //?}
}
