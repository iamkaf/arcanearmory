package com.iamkaf.arcanearmory.material;

import com.iamkaf.arcanearmory.ArcaneArmory;
import com.iamkaf.arcanearmory.material.config.*;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class AAMaterialAutoload {
    public final String name;
    public final AAMaterialType type;
    public final Item MATERIAL;
    public final Item RAW_MATERIAL;
    @Nullable
    public final Item NUGGET;
    public final Block ORE;
    public final Block DEEPSLATE_ORE;
    public final Block BLOCK;
    public final Block RAW_BLOCK;
    @Nullable
    public final AAArmorItem HELMET;
    @Nullable
    public final AAArmorItem CHESTPLATE;
    @Nullable
    public final AAArmorItem LEGGINGS;
    @Nullable
    public final AAArmorItem BOOTS;
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
    public final AABlockConfiguration blockConfiguration;
    public final AAToolConfiguration toolConfiguration;
    public final AAGenerationConfiguration generate;
    private final AANamer namer;

    public AAMaterialAutoload(AAMaterialConfiguration config) {
        this.name = config.name;
        ArcaneArmory.LOGGER.info(config.name);
        this.type = config.type;
        namer = new AANamer(config);

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
        this.ORE = registerBlock(namer.oreBlock(),
                new Block(FabricBlockSettings.copyOf(Blocks.IRON_ORE))
        );
        this.DEEPSLATE_ORE = registerBlock(namer.deepslateOreBlock(),
                new Block(FabricBlockSettings.copyOf(Blocks.DEEPSLATE_IRON_ORE))
        );
        this.BLOCK = registerBlock(namer.block(),
                new Block(FabricBlockSettings.copyOf(Blocks.DIAMOND_BLOCK))
        );
        this.RAW_BLOCK = registerBlock(namer.rawBlock(),
                new Block(FabricBlockSettings.copyOf(Blocks.RAW_IRON_BLOCK))
        );

        var armor = createArmor(config);
        if (generate.armor) {
            this.HELMET = armor[0];
            this.CHESTPLATE = armor[1];
            this.LEGGINGS = armor[2];
            this.BOOTS = armor[3];
        } else {
            this.HELMET = null;
            this.CHESTPLATE = null;
            this.LEGGINGS = null;
            this.BOOTS = null;
        }
//        try {
//            AAItemRendererUtil.registerArmorTints(config, MATERIAL,
////                    RAW_MATERIAL,
//                    NUGGET, HELMET, CHESTPLATE, LEGGINGS, BOOTS
//            );
//        } catch (RuntimeException e) {
//            // this is fine for now I guess? haha :)
//            // if you know how to fix this pls open a PR <3
//        }

        var tools = createTools(config);
        if (generate.weapons) {
            this.SWORD = (SwordItem) tools[0];
        } else {
            this.SWORD = null;
        }
        if (generate.tools) {
            this.SHOVEL = (ShovelItem) tools[1];
            this.PICKAXE = (PickaxeItem) tools[2];
            this.AXE = (AxeItem) tools[3];
            this.HOE = (HoeItem) tools[4];
        } else {
            this.SHOVEL = null;
            this.PICKAXE = null;
            this.AXE = null;
            this.HOE = null;
        }
//        try {
//            AAItemRendererUtil.registerToolTints(config, SWORD, SHOVEL, PICKAXE, AXE, HOE);
//        } catch (RuntimeException e) {
//            // this is fine for now I guess? haha :)
//            // if you know how to fix this pls open a PR <3
//        }
    }

    public ItemConvertible[] getItemsToAddToItemGroup() {
        List<ItemConvertible> e = new ArrayList<>();

        e.add(MATERIAL);
        e.add(BLOCK);

        if (generate.ore) {
            e.add(ORE);
            e.add(DEEPSLATE_ORE);
            e.add(RAW_MATERIAL);
            e.add(RAW_BLOCK);
            if (type.equals(AAMaterialType.INGOT)) {
                e.add(NUGGET);
            }
        }

        if (generate.weapons) {
            e.add(SWORD);
        }

        if (generate.tools) {
            e.add(PICKAXE);
            e.add(SHOVEL);
            e.add(AXE);
            e.add(HOE);
        }

        if (generate.armor) {
            e.add(HELMET);
            e.add(CHESTPLATE);
            e.add(LEGGINGS);
            e.add(BOOTS);
        }

        return e.toArray(new ItemConvertible[0]);
    }

    private AAArmorItem[] createArmor(
            AAMaterialConfiguration config
    ) {
        var armorMaterial = new AAArmorMaterial(config.name,
                config.armorConfiguration.durability,
                config.armorConfiguration.protection,
                config.armorConfiguration.enchantability,
                config.armorConfiguration.equipSound,
                config.armorConfiguration.toughness,
                config.armorConfiguration.knockbackResistance,
                config.repairIngredient
        );
        var HELMET = registerItem(namer.helmet(),
                new AAArmorItem(armorMaterial, ArmorItem.Type.HELMET, new FabricItemSettings())
        );
        var CHESTPLATE = registerItem(namer.chestplate(),
                new AAArmorItem(armorMaterial, ArmorItem.Type.CHESTPLATE, new FabricItemSettings())
        );
        var LEGGINGS = registerItem(namer.leggings(),
                new AAArmorItem(armorMaterial, ArmorItem.Type.LEGGINGS, new FabricItemSettings())
        );
        var BOOTS = registerItem(namer.boots(),
                new AAArmorItem(armorMaterial, ArmorItem.Type.BOOTS, new FabricItemSettings())
        );

        return new AAArmorItem[]{HELMET, CHESTPLATE, LEGGINGS, BOOTS};
    }

    private ToolItem[] createTools(
            AAMaterialConfiguration config
    ) {
        var toolMaterial = new AAToolMaterial(config.name,
                config.toolConfiguration.durability,
                config.toolConfiguration.miningSpeed,
                config.toolConfiguration.bonusDamage,
                config.toolConfiguration.miningLevel,
                config.toolConfiguration.enchantability,
                config.repairIngredient
        );
        var SWORD = registerItem(namer.sword(), new SwordItem(toolMaterial,
                (int) config.toolConfiguration.swordDamage,
                config.toolConfiguration.swordSwingSpeed,
                new FabricItemSettings()
        ));
        var SHOVEL = registerItem(namer.shovel(), new ShovelItem(toolMaterial,
                0,
                config.toolConfiguration.axeSwingSpeed * 0.8f,
                new FabricItemSettings()
        ));
        var PICKAXE = registerItem(namer.pickaxe(), new PickaxeItem(toolMaterial,
                0,
                config.toolConfiguration.axeSwingSpeed * 0.8f,
                new FabricItemSettings()
        ));
        var AXE = registerItem(namer.axe(), new AxeItem(toolMaterial,
                (int) config.toolConfiguration.axeDamage,
                config.toolConfiguration.axeSwingSpeed,
                new FabricItemSettings()
        ));
        var HOE = registerItem(namer.hoe(), new HoeItem(toolMaterial,
                0,
                config.toolConfiguration.axeSwingSpeed * 0.8f,
                new FabricItemSettings()
        ));

        return new ToolItem[]{SWORD, SHOVEL, PICKAXE, AXE, HOE};
    }

    private <T extends Item> T registerItem(String name, T item) {
//        ArcaneArmory.LOGGER.info("Registering item: " + name);
        return Registry.register(Registries.ITEM, new Identifier(ArcaneArmory.MOD_ID, name), item);
    }

    private <T extends Block> T registerBlock(String name, T block) {
//        ArcaneArmory.LOGGER.info("Registering block: " + name);
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
