package com.iamkaf.arcanearmory.content;

//? if >=1.20 {
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
//? if >=1.21.11 {
import net.minecraft.resources.Identifier;
//?} else {
/*import net.minecraft.resources.ResourceLocation;
*///?}
import net.minecraft.world.item.Item;
//? if >=1.21.2
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SmithingTemplateItem;
//?}

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Voidium is the netherite-style top tier: a smithing table turns each Black Diamond item into its
 * Voidium counterpart. From 1.20 the upgrade takes the Voidium Upgrade Smithing Template; older lines
 * use legacy two-slot smithing.
 */
public final class VoidiumUpgrade {
    public static final String TEMPLATE_ID = "voidium_upgrade_smithing_template";
    /** The material whose tools and armor come only from upgrading Black Diamond ones. */
    public static final String RESULT = "voidium";
    private static final String BASE = "black_diamond";

    private VoidiumUpgrade() {
    }

    /** Each Black Diamond item id that has a Voidium counterpart, mapped to that counterpart. */
    public static Map<String, String> upgrades() {
        ArcaneMaterial base = material(BASE);
        ArcaneMaterial result = material(RESULT);
        Map<String, String> upgrades = new LinkedHashMap<>();
        for (String itemId : base.itemIds()) {
            if (!itemId.startsWith(BASE + "_")) {
                continue;
            }
            String upgraded = RESULT + itemId.substring(BASE.length());
            if (result.itemIds().contains(upgraded)) {
                upgrades.put(itemId, upgraded);
            }
        }
        return upgrades;
    }

    private static ArcaneMaterial material(String id) {
        return ArcaneMaterials.ALL.stream()
                .filter(material -> material.id().equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Unknown Arcane material " + id));
    }

    //? if >=1.20 {
    // Text and slot hints mirror vanilla's netherite upgrade template.
    private static final Component APPLIES_TO = description("applies_to").withStyle(ChatFormatting.BLUE);
    private static final Component INGREDIENTS = description("ingredients").withStyle(ChatFormatting.BLUE);
    private static final Component BASE_SLOT_DESCRIPTION = description("base_slot_description");
    private static final Component ADDITIONS_SLOT_DESCRIPTION = description("additions_slot_description");

    public static Item template(Item.Properties properties) {
        //? if >=1.21.2 {
        return new SmithingTemplateItem(APPLIES_TO, INGREDIENTS, BASE_SLOT_DESCRIPTION, ADDITIONS_SLOT_DESCRIPTION,
                baseSlotIcons(), additionSlotIcons(), properties.rarity(Rarity.UNCOMMON));
        //?} else {
        /*Component upgrade = Component.translatable("upgrade.arcanearmory.voidium_upgrade").withStyle(ChatFormatting.GRAY);
        return new SmithingTemplateItem(APPLIES_TO, INGREDIENTS, upgrade, BASE_SLOT_DESCRIPTION, ADDITIONS_SLOT_DESCRIPTION,
                baseSlotIcons(), additionSlotIcons());
        *///?}
    }

    private static MutableComponent description(String key) {
        return Component.translatable("item.arcanearmory.smithing_template.voidium_upgrade." + key);
    }

    //? if >=1.21.11 {
    private static List<Identifier> baseSlotIcons() {
        return List.of("helmet", "sword", "chestplate", "pickaxe", "leggings", "axe", "boots", "hoe", "shovel").stream()
                .map(slot -> Identifier.withDefaultNamespace("container/slot/" + slot))
                .toList();
    }

    private static List<Identifier> additionSlotIcons() {
        return List.of(Identifier.withDefaultNamespace("container/slot/ingot"));
    }
    //?} else {
    /*private static List<ResourceLocation> baseSlotIcons() {
        return List.of("empty_armor_slot_helmet", "empty_slot_sword", "empty_armor_slot_chestplate", "empty_slot_pickaxe",
                        "empty_armor_slot_leggings", "empty_slot_axe", "empty_armor_slot_boots", "empty_slot_hoe", "empty_slot_shovel").stream()
                .map(icon -> vanilla("item/" + icon))
                .toList();
    }

    private static List<ResourceLocation> additionSlotIcons() {
        return List.of(vanilla("item/empty_slot_ingot"));
    }

    private static ResourceLocation vanilla(String path) {
        //? if >=1.21 {
        return ResourceLocation.withDefaultNamespace(path);
        //?} else {
        /^return new ResourceLocation(path);
        ^///?}
    }
    *///?}
    //?}
}
