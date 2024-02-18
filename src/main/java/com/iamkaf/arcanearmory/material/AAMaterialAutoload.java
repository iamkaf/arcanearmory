package com.iamkaf.arcanearmory.material;

import com.iamkaf.arcanearmory.ArcaneArmory;
import com.iamkaf.arcanearmory.material.config.AAMaterialConfiguration;
import com.iamkaf.arcanearmory.material.config.AANamer;
import com.iamkaf.arcanearmory.material.rendering.AAItemRendererUtil;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public abstract class AAMaterialAutoload {
    public final String name;
    public final Item MATERIAL;
    public final Item RAW_MATERIAL;
    public final Item NUGGET;
    public final Block ORE;
    public final Block BLOCK;
    public final Block RAW_BLOCK;
    public final AAArmorItem HELMET;
    public final AAArmorItem CHESTPLATE;
    public final AAArmorItem LEGGINGS;
    public final AAArmorItem BOOTS;
    public final SwordItem SWORD;
    public final ShovelItem SHOVEL;
    public final PickaxeItem PICKAXE;
    public final AxeItem AXE;
    //    public final BowItem BOW;
//    public final ShearsItem SHEARS;
    public final HoeItem HOE;
    private final AANamer namer;

    public AAMaterialAutoload() {
        AAMaterialConfiguration config = this.getConfiguration();
        this.name = config.name;
        namer = new AANamer(config);

        this.MATERIAL = registerItem(namer.ingot(), config.ingot);
        this.RAW_MATERIAL = registerItem(namer.rawMaterial(), new Item(new FabricItemSettings()));
        this.NUGGET = registerItem(namer.nugget(), new Item(new FabricItemSettings()));
        this.ORE = registerBlock(namer.oreBlock(), new Block(FabricBlockSettings.create()));
        this.BLOCK = registerBlock(namer.block(), new Block(FabricBlockSettings.create()));
        this.RAW_BLOCK = registerBlock(namer.rawBlock(), new Block(FabricBlockSettings.create()));

        var armor = createArmor(config);
        this.HELMET = armor[0];
        this.CHESTPLATE = armor[1];
        this.LEGGINGS = armor[2];
        this.BOOTS = armor[3];
        try {
            AAItemRendererUtil.registerArmorTints(config, MATERIAL,
//                    RAW_MATERIAL,
                    NUGGET, HELMET, CHESTPLATE, LEGGINGS, BOOTS
            );
        } catch (RuntimeException e) {
            // this is fine for now I guess? haha :)
            // if you know how to fix this pls open a PR <3
        }

        var tools = createTools(config);
        this.SWORD = (SwordItem) tools[0];
        this.SHOVEL = (ShovelItem) tools[1];
        this.PICKAXE = (PickaxeItem) tools[2];
        this.AXE = (AxeItem) tools[3];
        this.HOE = (HoeItem) tools[4];
        try {
            AAItemRendererUtil.registerToolTints(config, SWORD, SHOVEL, PICKAXE, AXE, HOE);
        } catch (RuntimeException e) {
            // this is fine for now I guess? haha :)
            // if you know how to fix this pls open a PR <3
        }

    }

    protected abstract AAMaterialConfiguration getConfiguration();

    public ItemConvertible[] getItemsToAddToItemGroup() {
        return new ItemConvertible[]{
                this.ORE, this.BLOCK, this.RAW_BLOCK, this.MATERIAL, this.RAW_MATERIAL, this.NUGGET,
                this.HELMET, this.CHESTPLATE, this.LEGGINGS, this.BOOTS, this.SWORD, this.SHOVEL,
                this.PICKAXE, this.AXE, this.HOE
        };
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
