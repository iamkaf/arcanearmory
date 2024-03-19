package com.iamkaf.arcanearmory.item;

import com.github.crimsondawn45.fabricshieldlib.lib.object.FabricShieldItem;
import com.iamkaf.arcanearmory.ArcaneArmory;
import com.iamkaf.arcanearmory.item.custom.AristeumHammer;
import com.iamkaf.arcanearmory.material.AAMaterial;
import com.iamkaf.arcanearmory.material.ModMaterials;
import net.fabric_extras.ranged_weapon.api.CustomBow;
import net.fabric_extras.ranged_weapon.api.RangedConfig;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {
    public static final Item RUBY_SHIELD = registerItem("ruby_shield", new FabricShieldItem(
            new FabricItemSettings().maxDamage((int) (ModMaterials.RUBY.toolConfiguration.durability * 0.9)),
            100,
            ModMaterials.RUBY.toolConfiguration.enchantability,
            ModMaterials.RUBY.MATERIAL
    ));
    public static final Item COOLPPER_SHIELD = registerItem("coolpper_shield", new FabricShieldItem(
            new FabricItemSettings().maxDamage((int) (ModMaterials.COOLPPER.toolConfiguration.durability * 0.9)),
            100,
            ModMaterials.COOLPPER.toolConfiguration.enchantability,
            ModMaterials.COOLPPER.MATERIAL
    ));
    public static final Item TITANIUM_SHIELD = registerItem("titanium_shield", new FabricShieldItem(
            new FabricItemSettings().maxDamage((int) (ModMaterials.TITANIUM.toolConfiguration.durability * 0.9)),
            100,
            ModMaterials.TITANIUM.toolConfiguration.enchantability,
            ModMaterials.TITANIUM.MATERIAL
    ));
    public static final Item ARISTEUM_SHIELD = registerItem("aristeum_shield", new FabricShieldItem(
            new FabricItemSettings().maxDamage((int) (ModMaterials.ARISTEUM.toolConfiguration.durability * 0.9)),
            100,
            ModMaterials.ARISTEUM.toolConfiguration.enchantability,
            ModMaterials.ARISTEUM.MATERIAL
    ));
    public static final Item VOIDIUM_SHIELD = registerItem("voidium_shield", new FabricShieldItem(
            new FabricItemSettings().maxDamage((int) (ModMaterials.VOIDIUM.toolConfiguration.durability * 0.9)),
            100,
            ModMaterials.VOIDIUM.toolConfiguration.enchantability,
            ModMaterials.VOIDIUM.MATERIAL
    ));

    public static final Item ARISTEUM_HAMMER = registerItem("aristeum_hammer",
            new AristeumHammer(new FabricItemSettings())
    );

    public static final Item ARISTEUM_BOW = registerBow("aristeum_bow", ModMaterials.ARISTEUM);

    public static final Item AMBER_INGOT = registerItem("amber_ingot",
            new Item(new FabricItemSettings())
    );

    private static Item registerBow(String name, AAMaterial config) {
        var bow = new CustomBow(
                new FabricItemSettings().maxDamage((int) (config.toolConfiguration.durability * 0.8)),
                () -> Ingredient.ofItems(config.MATERIAL)
        );
        bow.config(new RangedConfig(20, (int) config.toolConfiguration.axeDamage, null));

        registerItem(name, bow);
        return bow;
    }

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, new Identifier(ArcaneArmory.MOD_ID, name), item);
    }

    public static void registerModItems() {
        ArcaneArmory.LOGGER.info("Registering mod items for " + ArcaneArmory.MOD_ID);
    }
}
