package com.iamkaf.arcanearmory.material;

import com.iamkaf.arcanearmory.ArcaneArmory;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.data.client.*;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.iamkaf.arcanearmory.material.ModMaterials.ALL_MATERIALS;

public abstract class AAMaterial {
    // TODO: move this somewhere else
    private static final Model HANDHELD_TWO_LAYERS = new Model(Optional.of(new Identifier("minecraft",
            "item/handheld"
    )), Optional.empty(), TextureKey.LAYER0, TextureKey.LAYER1);

    public final String name;
    //    public final ArmorMaterial ARMOR_MATERIAL;
//    public final AAMaterialConfiguration CONFIGURATION;
    public final Item MATERIAL;
    public final AAArmorItem HELMET;
    public final ArmorItem CHESTPLATE;
    public final ArmorItem LEGGINGS;
    public final ArmorItem BOOTS;
    public final SwordItem SWORD;
    public final ShovelItem SHOVEL;
    public final PickaxeItem PICKAXE;
    public final AxeItem AXE;
    //    public final BowItem BOW;
//    public final ShearsItem SHEARS;
    public final HoeItem HOE;

    public AAMaterial() {
        AAMaterialConfiguration config = this.getConfiguration();
        this.name = config.name;

        this.MATERIAL = registerItem(config.ingotName, config.ingot);

        var armorMaterial = createArmorMaterial(config);
        var armor = createArmorItems(config, armorMaterial);
        this.HELMET = (AAArmorItem) armor[0];
        this.CHESTPLATE = armor[1];
        this.LEGGINGS = armor[2];
        this.BOOTS = armor[3];

        // TODO: this.createToolItems();
        var toolMaterial = createToolMaterial(config);
        var tools = createToolItems(config, toolMaterial);
        this.SWORD = (SwordItem) tools[0];
        this.SHOVEL = (ShovelItem) tools[1];
        this.PICKAXE = (PickaxeItem) tools[2];
        this.AXE = (AxeItem) tools[3];
        this.HOE = (HoeItem) tools[4];

        // TODO: this.createWeaponItems();

        // item tints
        try {
            ColorProviderRegistry.ITEM.register(
                    (stack, tintIndex) -> tintIndex == 0 ? config.baseTint : config.decorationTint,
                    MATERIAL
            );
            ColorProviderRegistry.ITEM.register(
                    (stack, tintIndex) -> tintIndex == 0 ? config.baseTint : config.decorationTint,
                    HELMET
            );
            ColorProviderRegistry.ITEM.register(
                    (stack, tintIndex) -> tintIndex == 0 ? config.baseTint : config.decorationTint,
                    CHESTPLATE
            );
            ColorProviderRegistry.ITEM.register(
                    (stack, tintIndex) -> tintIndex == 0 ? config.baseTint : config.decorationTint,
                    LEGGINGS
            );
            ColorProviderRegistry.ITEM.register(
                    (stack, tintIndex) -> tintIndex == 0 ? config.baseTint : config.decorationTint,
                    BOOTS
            );
        } catch (RuntimeException e) {
            // this is fine for now I guess? haha :)
        }
    }

    // Datagen
    public static void generateItemModels(ItemModelGenerator generator) {
        for (AAMaterial material : ALL_MATERIALS) {
            generator.register(material.MATERIAL, Models.GENERATED);
            generator.registerArmor(material.HELMET);
            generator.registerArmor(material.CHESTPLATE);
            generator.registerArmor(material.LEGGINGS);
            generator.registerArmor(material.BOOTS);
            generator.register(material.SWORD, Models.HANDHELD);
            generator.register(material.SHOVEL, Models.HANDHELD);
//            generator.register(material.PICKAXE, Models.HANDHELD);
            generator.register(material.AXE, Models.HANDHELD);
            generator.register(material.HOE, Models.HANDHELD);

            // EXPERIMENTAL STUFF
            Identifier identifier = ModelIds.getItemModelId(material.PICKAXE);
            Identifier identifier2 = TextureMap.getId(material.PICKAXE);
            Identifier identifier3 = TextureMap.getSubId(material.PICKAXE, "_overlay");
            Models.GENERATED_TWO_LAYERS.upload(identifier,
                    TextureMap.layered(identifier2, identifier3),
                    generator.writer,
                    HANDHELD_TWO_LAYERS::createJson
            );
        }
    }

    protected abstract AAMaterialConfiguration getConfiguration();

    public List<Item> getItemsToAddToItemGroup() {
        ArrayList<Item> items = new ArrayList<>();

        // TODO: Check config to see if any of these should be omitted
        items.add(this.MATERIAL);
        items.add(this.HELMET);
        items.add(this.CHESTPLATE);
        items.add(this.LEGGINGS);
        items.add(this.BOOTS);
        items.add(this.SWORD);
        items.add(this.SHOVEL);
        items.add(this.PICKAXE);
        items.add(this.AXE);
        items.add(this.HOE);

        return items;
    }

    private <T extends Item> T registerItem(String name, T item) {
        return Registry.register(Registries.ITEM, new Identifier(ArcaneArmory.MOD_ID, name), item);
    }

    private AAArmorMaterial createArmorMaterial(AAMaterialConfiguration config) {
        return new AAArmorMaterial(config.name,
                config.armorDurability,
                config.protectionAmounts,
                config.enchantability,
                config.equipSound,
                config.toughness,
                config.knockbackResistance,
                config.repairIngredient
        );
    }

    private ArmorItem[] createArmorItems(
            AAMaterialConfiguration config, AAArmorMaterial armorMaterial
    ) {
        var HELMET = registerItem(config.name + "_helmet",
                new AAArmorItem(armorMaterial, ArmorItem.Type.HELMET, new FabricItemSettings())
        );
        var CHESTPLATE = registerItem(config.name + "_chestplate",
                new ArmorItem(armorMaterial, ArmorItem.Type.CHESTPLATE, new FabricItemSettings())
        );
        var LEGGINGS = registerItem(config.name + "_leggings",
                new ArmorItem(armorMaterial, ArmorItem.Type.LEGGINGS, new FabricItemSettings())
        );
        var BOOTS = registerItem(config.name + "_boots",
                new ArmorItem(armorMaterial, ArmorItem.Type.BOOTS, new FabricItemSettings())
        );

        return new ArmorItem[]{HELMET, CHESTPLATE, LEGGINGS, BOOTS};
    }

    private AAToolMaterial createToolMaterial(AAMaterialConfiguration config) {
        return new AAToolMaterial(config.name,
                config.toolDurability,
                config.miningSpeed,
                config.attackDamage,
                config.miningLevel,
                config.enchantability,
                config.repairIngredient
        );
    }

    private ToolItem[] createToolItems(
            AAMaterialConfiguration config, AAToolMaterial toolMaterial
    ) {
        var SWORD = registerItem(config.name + "_sword",
                new SwordItem(toolMaterial, (int) config.attackDamage, 2f, new FabricItemSettings())
        );
        var SHOVEL = registerItem(config.name + "_shovel",
                new ShovelItem(toolMaterial,
                        (int) config.attackDamage,
                        2f,
                        new FabricItemSettings()
                )
        );
        var PICKAXE = registerItem(config.name + "_pickaxe",
                new PickaxeItem(toolMaterial,
                        (int) config.attackDamage,
                        2f,
                        new FabricItemSettings()
                )
        );
        var AXE = registerItem(config.name + "_axe",
                new AxeItem(toolMaterial, (int) config.attackDamage, 2f, new FabricItemSettings())
        );
        var HOE = registerItem(config.name + "_hoe",
                new HoeItem(toolMaterial, (int) config.attackDamage, 2f, new FabricItemSettings())
        );

        return new ToolItem[]{SWORD, SHOVEL, PICKAXE, AXE, HOE};
    }
}
