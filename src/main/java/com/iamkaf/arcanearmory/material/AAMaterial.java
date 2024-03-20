package com.iamkaf.arcanearmory.material;

import com.iamkaf.arcanearmory.ArcaneArmory;
import com.iamkaf.arcanearmory.item.factory.ArmorFactory;
import com.iamkaf.arcanearmory.item.factory.ToolFactory;
import com.iamkaf.arcanearmory.material.config.*;
import dev.draylar.magna.item.HammerItem;
import net.fabric_extras.ranged_weapon.api.CustomBow;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class AAMaterial {
    public final String name;
    public final AAMaterialType type;
    public final Item MATERIAL;
    @Nullable
    public final Item RAW_MATERIAL;
    @Nullable
    public final Item NUGGET;
    @Nullable
    public final Block ORE;
    @Nullable
    public final Block DEEPSLATE_ORE;
    public final Block BLOCK;
    @Nullable
    public final Block RAW_BLOCK;
    @Nullable
    public final ArmorItem HELMET;
    @Nullable
    public final ArmorItem CHESTPLATE;
    @Nullable
    public final ArmorItem LEGGINGS;
    @Nullable
    public final ArmorItem BOOTS;
    @Nullable
    public final SwordItem SWORD;
    @Nullable
    public final ShovelItem SHOVEL;
    @Nullable
    public final PickaxeItem PICKAXE;
    @Nullable
    public final AxeItem AXE;
    //    @Nullable  public final BowItem BOW;
//    @Nullable  public final ShearsItem SHEARS;
    @Nullable
    public final HoeItem HOE;
    @Nullable
    public final HammerItem HAMMER;
    @Nullable
    public final CustomBow BOW;
    public final AABlockConfiguration blockConfiguration;
    public final AAToolConfiguration toolConfiguration;
    public final AAGenerationConfiguration generate;
    private final MaterialNamingUtil namer;

    public AAMaterial(AAMaterialConfiguration config) {
        this.name = config.name;
        this.type = config.type;
        namer = new MaterialNamingUtil(config);

        this.blockConfiguration = config.oreConfiguration;
        this.toolConfiguration = config.toolConfiguration;
        this.generate = config.generationConfiguration;

        this.MATERIAL = registerItem(namer.ingot(), config.ingot);
        this.RAW_MATERIAL = registerItem(namer.rawMaterial(), new Item(new FabricItemSettings()));
        if (config.type.equals(AAMaterialType.INGOT)) {
            this.NUGGET = registerItem(namer.nugget(), new Item(new FabricItemSettings()));
        } else {
            this.NUGGET = null;
        }

        this.BLOCK = registerBlock(namer.block(),
                new Block(FabricBlockSettings.copyOf(Blocks.DIAMOND_BLOCK))
        );

        if (generate.ore) {
            this.ORE = registerBlock(namer.oreBlock(),
                    new Block(FabricBlockSettings.copyOf(Blocks.IRON_ORE))
            );
            this.DEEPSLATE_ORE = registerBlock(namer.deepslateOreBlock(),
                    new Block(FabricBlockSettings.copyOf(Blocks.DEEPSLATE_IRON_ORE))
            );
            this.RAW_BLOCK = registerBlock(namer.rawBlock(),
                    new Block(FabricBlockSettings.copyOf(Blocks.RAW_IRON_BLOCK))
            );
        } else {
            this.ORE = null;
            this.DEEPSLATE_ORE = null;
            this.RAW_BLOCK = null;
        }

        if (generate.armor) {
            var armorMaterial = new ModArmorMaterial(config.name,
                    config.armorConfiguration.durability,
                    config.armorConfiguration.protection,
                    config.armorConfiguration.enchantability,
                    config.armorConfiguration.equipSound,
                    config.armorConfiguration.toughness,
                    config.armorConfiguration.knockbackResistance,
                    config.repairIngredient
            );
            var armor = ArmorFactory.createFullSet(armorMaterial, namer);
            HELMET = armor.helmet();
            CHESTPLATE = armor.chestplate();
            LEGGINGS = armor.leggings();
            BOOTS = armor.boots();
        } else {
            this.HELMET = null;
            this.CHESTPLATE = null;
            this.LEGGINGS = null;
            this.BOOTS = null;
        }

        if (generate.tools) {
            var toolMaterial = new ModToolMaterial(config.name,
                    (int) config.toolConfiguration.swordDamage,
                    (int) config.toolConfiguration.axeDamage,
                    config.toolConfiguration.durability,
                    config.toolConfiguration.miningSpeed,
                    config.toolConfiguration.bonusDamage,
                    config.toolConfiguration.miningLevel,
                    config.toolConfiguration.enchantability,
                    config.repairIngredient
            );
            var tools = ToolFactory.createToolSet(toolMaterial, namer);
            SWORD = tools.sword();
            SHOVEL = tools.shovel();
            PICKAXE = tools.pickaxe();
            AXE = tools.axe();
            HOE = tools.hoe();
            HAMMER = tools.hammer();
            BOW = tools.bow();
        } else {
            this.SWORD = null;
            this.SHOVEL = null;
            this.PICKAXE = null;
            this.AXE = null;
            this.HOE = null;
            this.HAMMER = null;
            this.BOW = null;
        }

        addItemsToItemGroup();
    }

    private void addItemsToItemGroup() {
        List<ItemConvertible> items = new ArrayList<>();

        items.add(MATERIAL);
        items.add(BLOCK);

        if (generate.ore) {
            items.add(ORE);
            items.add(DEEPSLATE_ORE);
            items.add(RAW_MATERIAL);
            items.add(RAW_BLOCK);
            if (type.equals(AAMaterialType.INGOT)) {
                items.add(NUGGET);
            }
        }

        if (generate.weapons) {
            items.add(SWORD);
        }

        if (generate.tools) {
            items.add(PICKAXE);
            items.add(SHOVEL);
            items.add(AXE);
            items.add(HOE);
            items.add(HAMMER);
            items.add(BOW);
        }

        if (generate.armor) {
            items.add(HELMET);
            items.add(CHESTPLATE);
            items.add(LEGGINGS);
            items.add(BOOTS);
        }

        ItemGroupEvents
                .modifyEntriesEvent(RegistryKey.of(RegistryKeys.ITEM_GROUP,
                        ArcaneArmory.ID("arcanearmory")
                ))
                .register(content -> {
                    for (ItemConvertible item : items) {
                        content.add(item);
                    }
                });
    }

    private <T extends Item> T registerItem(String name, T item) {
        return Registry.register(Registries.ITEM, new Identifier(ArcaneArmory.MOD_ID, name), item);
    }

    private <T extends Block> T registerBlock(String name, T block) {
        Registry.register(Registries.ITEM,
                new Identifier(ArcaneArmory.MOD_ID, name),
                new BlockItem(block, new FabricItemSettings())
        );
        return Registry.register(Registries.BLOCK,
                new Identifier(ArcaneArmory.MOD_ID, name),
                block
        );
    }
}
