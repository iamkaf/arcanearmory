package com.iamkaf.arcanearmory.content;

import com.iamkaf.arcanearmory.ArcaneArmoryConstants;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;

//? if >=1.21.5 {
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;
//?} else if >=1.21 {
import net.minecraft.core.Holder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Tier;
//?} else {
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Tier;
//? if >=1.19.3
import net.minecraft.world.item.ArmorItem;
//? if <1.19.3
import net.minecraft.world.entity.EquipmentSlot;
//?}

import java.util.List;
import java.util.Map;

final class ArcaneEquipmentMaterials {
    private ArcaneEquipmentMaterials() {
    }

    //? if >=1.21.5 {
    static ToolMaterial tool(ArcaneMaterial material) {
        ArcaneMaterial.ToolStats stats = material.requireToolStats();
        return new ToolMaterial(
                incorrectBlocksForDrops(stats.miningLevel()),
                stats.durability(),
                stats.miningSpeed(),
                stats.attackDamageBonus(),
                stats.enchantmentValue(),
                ItemTags.DIAMOND_TOOL_MATERIALS
        );
    }

    static ArmorMaterial armor(ArcaneMaterial material) {
        ArcaneMaterial.ArmorStats stats = material.requireArmorStats();
        ResourceKey<EquipmentAsset> asset = ResourceKey.create(
                EquipmentAssets.ROOT_ID,
                ArcaneArmoryConstants.resource(material.id())
        );
        return new ArmorMaterial(
                Math.max(1, stats.baseDurability() / 55),
                Map.of(
                        ArmorType.HELMET, stats.helmetProtection(),
                        ArmorType.CHESTPLATE, stats.chestplateProtection(),
                        ArmorType.LEGGINGS, stats.leggingsProtection(),
                        ArmorType.BOOTS, stats.bootsProtection()
                ),
                stats.enchantmentValue(),
                SoundEvents.ARMOR_EQUIP_IRON,
                stats.toughness(),
                stats.knockbackResistance(),
                ItemTags.DIAMOND_TOOL_MATERIALS,
                asset
        );
    }
    //?} else if >=1.21 {
    static Tier tool(ArcaneMaterial material, Item repairItem) {
        return new MaterialTier(material.requireToolStats(), repairItem);
    }

    static Holder<ArmorMaterial> armor(ArcaneMaterial material, Item repairItem) {
        ArcaneMaterial.ArmorStats stats = material.requireArmorStats();
        return Holder.direct(new ArmorMaterial(
                Map.of(
                        ArmorItem.Type.HELMET, stats.helmetProtection(),
                        ArmorItem.Type.CHESTPLATE, stats.chestplateProtection(),
                        ArmorItem.Type.LEGGINGS, stats.leggingsProtection(),
                        ArmorItem.Type.BOOTS, stats.bootsProtection()
                ),
                stats.enchantmentValue(),
                SoundEvents.ARMOR_EQUIP_IRON,
                () -> Ingredient.of(repairItem),
                List.of(new ArmorMaterial.Layer(ArcaneArmoryConstants.resource(material.id()))),
                stats.toughness(),
                stats.knockbackResistance()
        ));
    }
    //?} else {
    static Tier tool(ArcaneMaterial material, Item repairItem) {
        return new MaterialTier(material.requireToolStats(), repairItem);
    }

    static ArmorMaterial armor(ArcaneMaterial material, Item repairItem) {
        return new MaterialArmor(material, repairItem);
    }
    //?}

    //? if <1.21.5 {
    private static final class MaterialTier implements Tier {
        private final ArcaneMaterial.ToolStats stats;
        private final Item repairItem;

        private MaterialTier(ArcaneMaterial.ToolStats stats, Item repairItem) {
            this.stats = stats;
            this.repairItem = repairItem;
        }

        @Override
        public int getUses() {
            return stats.durability();
        }

        @Override
        public float getSpeed() {
            return stats.miningSpeed();
        }

        @Override
        public float getAttackDamageBonus() {
            return stats.attackDamageBonus();
        }

        //? if >=1.21 {
        @Override
        public TagKey<Block> getIncorrectBlocksForDrops() {
            return incorrectBlocksForDrops(stats.miningLevel());
        }
        //?} else {
        @Override
        public int getLevel() {
            return stats.miningLevel();
        }
        //?}

        @Override
        public int getEnchantmentValue() {
            return stats.enchantmentValue();
        }

        @Override
        public Ingredient getRepairIngredient() {
            return Ingredient.of(repairItem);
        }
    }
    //?}

    //? if <1.21 {
    private static final class MaterialArmor implements ArmorMaterial {
        private final ArcaneMaterial material;
        private final Item repairItem;

        private MaterialArmor(ArcaneMaterial material, Item repairItem) {
            this.material = material;
            this.repairItem = repairItem;
        }

        //? if >=1.19.3 {
        @Override
        public int getDurabilityForType(ArmorItem.Type type) {
            return material.requireArmorStats().durability(piece(type));
        }

        @Override
        public int getDefenseForType(ArmorItem.Type type) {
            return material.requireArmorStats().protection(piece(type));
        }
        //?} else {
        @Override
        public int getDurabilityForSlot(EquipmentSlot slot) {
            return material.requireArmorStats().durability(piece(slot));
        }

        @Override
        public int getDefenseForSlot(EquipmentSlot slot) {
            return material.requireArmorStats().protection(piece(slot));
        }
        //?}

        @Override
        public int getEnchantmentValue() {
            return material.requireArmorStats().enchantmentValue();
        }

        @Override
        public net.minecraft.sounds.SoundEvent getEquipSound() {
            return SoundEvents.ARMOR_EQUIP_IRON;
        }

        @Override
        public Ingredient getRepairIngredient() {
            return Ingredient.of(repairItem);
        }

        @Override
        public String getName() {
            return ArcaneArmoryConstants.MOD_ID + ":" + material.id();
        }

        @Override
        public float getToughness() {
            return material.requireArmorStats().toughness();
        }

        @Override
        public float getKnockbackResistance() {
            return material.requireArmorStats().knockbackResistance();
        }
    }
    //?}

    //? if >=1.21 {
    private static TagKey<Block> incorrectBlocksForDrops(int miningLevel) {
        if (miningLevel <= 0) {
            return BlockTags.INCORRECT_FOR_WOODEN_TOOL;
        }
        if (miningLevel == 1) {
            return BlockTags.INCORRECT_FOR_STONE_TOOL;
        }
        if (miningLevel == 2) {
            return BlockTags.INCORRECT_FOR_IRON_TOOL;
        }
        if (miningLevel == 3) {
            return BlockTags.INCORRECT_FOR_DIAMOND_TOOL;
        }
        return BlockTags.INCORRECT_FOR_NETHERITE_TOOL;
    }
    //?}

    //? if <1.21 && >=1.19.3 {
    private static ArcaneMaterial.ArmorPiece piece(ArmorItem.Type type) {
        return switch (type) {
            case HELMET -> ArcaneMaterial.ArmorPiece.HELMET;
            case CHESTPLATE -> ArcaneMaterial.ArmorPiece.CHESTPLATE;
            case LEGGINGS -> ArcaneMaterial.ArmorPiece.LEGGINGS;
            case BOOTS -> ArcaneMaterial.ArmorPiece.BOOTS;
        };
    }
    //?} else if <1.19.3 {
    private static ArcaneMaterial.ArmorPiece piece(EquipmentSlot slot) {
        return switch (slot) {
            case HEAD -> ArcaneMaterial.ArmorPiece.HELMET;
            case CHEST -> ArcaneMaterial.ArmorPiece.CHESTPLATE;
            case LEGS -> ArcaneMaterial.ArmorPiece.LEGGINGS;
            case FEET -> ArcaneMaterial.ArmorPiece.BOOTS;
            default -> throw new IllegalArgumentException("Unsupported armor slot: " + slot);
        };
    }
    //?}
}
