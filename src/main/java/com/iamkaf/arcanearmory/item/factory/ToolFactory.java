package com.iamkaf.arcanearmory.item.factory;

import com.iamkaf.arcanearmory.item.ModItems;
import com.iamkaf.arcanearmory.material.ModToolMaterial;
import com.iamkaf.arcanearmory.material.config.MaterialNamingUtil;
import dev.draylar.magna.item.HammerItem;
import net.fabric_extras.ranged_weapon.api.CustomBow;
import net.fabric_extras.ranged_weapon.api.RangedConfig;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.*;
import net.minecraft.recipe.Ingredient;

public class ToolFactory {
    private static final float ATTACK_SPEED_BASELINE = -4f;

    /**
     * Creates and registers a full set of tools based on the provided material and namer.
     *
     * @param material The material to use for the armor.
     * @param namer    A namer to generate the ids of the items.
     * @return The armor set.
     */
    public static ToolSet createToolSet(
            ModToolMaterial material, MaterialNamingUtil namer
    ) {

        return new ToolSet(createSword(namer.sword(), material),
                createShovel(namer.shovel(), material),
                createPickaxe(namer.pickaxe(), material),
                createAxe(namer.axe(), material),
                createHoe(namer.hoe(), material),
                createHammer(namer.hammer(), material),
                createBow(namer.bow(), material)
        );
    }

    /**
     * Creates a sword item and registers it.
     *
     * @param name     The namespace of the item.
     * @param material
     * @return The sword created.
     */
    public static SwordItem createSword(String name, ModToolMaterial material) {
        var item = new SwordItem(material,
                material.getSwordDamage(),
                1.6f + ATTACK_SPEED_BASELINE,
                new FabricItemSettings()
        );
        ModItems.registerItem(name, item);
        return item;
    }

    /**
     * Creates a shovel item and registers it.
     *
     * @param name     The namespace of the item.
     * @param material
     * @return The shovel created.
     */
    public static ShovelItem createShovel(String name, ModToolMaterial material) {
        var item = new ShovelItem(material, 0, 1f + ATTACK_SPEED_BASELINE, new Item.Settings());
        ModItems.registerItem(name, item);
        return item;
    }

    /**
     * Creates a pickaxe item and registers it.
     *
     * @param name     The namespace of the item.
     * @param material
     * @return The pickaxe created.
     */
    public static PickaxeItem createPickaxe(String name, ModToolMaterial material) {
        var item = new PickaxeItem(material, 0, 1f + ATTACK_SPEED_BASELINE, new Item.Settings());
        ModItems.registerItem(name, item);
        return item;
    }

    /**
     * Creates an axe item and registers it.
     *
     * @param name     The namespace of the item.
     * @param material
     * @return The axe created.
     */
    public static AxeItem createAxe(String name, ModToolMaterial material) {
        var item = new AxeItem(material,
                material.getAxeDamage(),
                1f + ATTACK_SPEED_BASELINE,
                new Item.Settings()
        );
        ModItems.registerItem(name, item);
        return item;
    }

    /**
     * Creates a hoe item and registers it.
     *
     * @param name     The namespace of the item.
     * @param material
     * @return The hoe created.
     */
    public static HoeItem createHoe(String name, ModToolMaterial material) {
        var item = new HoeItem(material, 0, 1f + ATTACK_SPEED_BASELINE, new Item.Settings());
        ModItems.registerItem(name, item);
        return item;
    }

    /**
     * Creates a hammer item and registers it.
     *
     * @param name     The namespace of the item.
     * @param material
     * @return The hammer created.
     */
    public static HammerItem createHammer(String name, ModToolMaterial material) {
        var item = new HammerItem(material, material.getSwordDamage(), // less damage than an axe
                1f + ATTACK_SPEED_BASELINE, // slower than a sword
                new Item.Settings()
        );
        ModItems.registerItem(name, item);
        return item;
    }

    /**
     * Creates a bow item and registers it.
     *
     * @param name     The namespace of the item.
     * @param material
     * @return The bow created.
     */
    public static CustomBow createBow(String name, ModToolMaterial material) {
        var item = new CustomBow(new FabricItemSettings().maxDamage((int) (material.getDurability() * 0.8)),
                () -> Ingredient.ofItems(material
                        .getRepairIngredient()
                        .getMatchingStacks()[0].getItem())
        );
        item.config(new RangedConfig(
                20,
                material.getAxeDamage() + material.getAttackDamage(),
                null
        ));
        ModItems.registerItem(name, item);
        return item;
    }

    public record ToolSet(SwordItem sword, ShovelItem shovel, PickaxeItem pickaxe, AxeItem axe,
                          HoeItem hoe, HammerItem hammer, CustomBow bow) {
    }
}
