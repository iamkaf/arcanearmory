package com.iamkaf.arcanearmory.item.factory;

import com.iamkaf.arcanearmory.item.ModItems;
import com.iamkaf.arcanearmory.material.ModToolMaterial;
import com.iamkaf.arcanearmory.material.config.MaterialNamingUtil;
import dev.draylar.magna.item.HammerItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.*;

public class ToolFactory {
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
                createHammer(namer.hammer(), material)
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
                1.6f,
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
        var item = new ShovelItem(material, 0, 1f, new Item.Settings());
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
        var item = new PickaxeItem(material, 0, 1f, new Item.Settings());
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
        var item = new AxeItem(material, material.getAxeDamage(), 1f, new Item.Settings());
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
        var item = new HoeItem(material, 0, 1f, new Item.Settings());
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
        var item = new HammerItem(material, 0, 1f, new Item.Settings());
        ModItems.registerItem(name, item); // system not ready yet
        return item;
    }

    public record ToolSet(SwordItem sword, ShovelItem shovel, PickaxeItem pickaxe, AxeItem axe,
                          HoeItem hoe, HammerItem hammer) {
    }
}
