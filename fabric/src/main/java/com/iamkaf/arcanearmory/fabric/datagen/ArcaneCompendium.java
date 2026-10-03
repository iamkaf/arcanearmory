package com.iamkaf.arcanearmory.fabric.datagen;

import com.iamkaf.arcanearmory.ArcaneArmoryConstants;
import com.iamkaf.arcanearmory.content.ArcaneMaterial;
import com.iamkaf.arcanearmory.content.ArcaneMaterials;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The Arcane Compendium, Arcane Armory's optional Modonomicon guide book. This is the one English source
 * for its text: {@link ArcaneArmoryBookProvider} writes the book data with translation keys, and the
 * language provider writes {@link #english()}.
 */
final class ArcaneCompendium {
    static final String ID = "arcane_compendium";
    static final String BOOK_ID = ArcaneArmoryConstants.MOD_ID + ":" + ID;
    static final String KEY = "book." + ArcaneArmoryConstants.MOD_ID + "." + ID;
    static final String NAME = "Arcane Compendium";
    static final String TOOLTIP = "A guide to Arcane Armory";
    static final String DESCRIPTION = "Arcane Armory's materials and their traits, where to find them, and the gear they make.";

    private static final Map<String, String> FOUND = Map.ofEntries(
            Map.entry("ruby", "Ore deep underground, below Y 12."),
            Map.entry("sapphire", "Ore deep underground, below Y 12."),
            Map.entry("black_diamond", "Rare ore at the bottom of the world, below Y -32."),
            Map.entry("topaz", "Ore deep underground, below Y 12."),
            Map.entry("chrysoberyl", "Ore underground, between Y -12 and 12."),
            Map.entry("aquamarine", "Ore beneath oceans and beaches, and in lush caves."),
            Map.entry("star_corundum", "Ore found only inside meteors."),
            Map.entry("bloodfire_garnet", "Ore throughout the Nether."),
            Map.entry("aetheric_crystal", "Common ore between Y 0 and 64."),
            Map.entry("coolpper", "Common ore between Y 0 and 64. Craft the ore straight into two ingots."),
            Map.entry("titanium", "Ore below Y 42."),
            Map.entry("amber", "Ore lining the inside of amber geodes, deep underground."),
            Map.entry("arcanthium", "An alloy: Titanium Ingot, Aetheric Crystal, Arcanthe or a Wither Rose, and Pink Dye make two ingots."),
            Map.entry("voidium", "An alloy: Void Obsidian Fragment, Bloodfire Garnet, and a Netherite Ingot make two ingots.")
    );

    private static final Map<String, String> TRAITS = Map.ofEntries(
            Map.entry("ruby", "**On hit:** sets the target on fire for 3 seconds."),
            Map.entry("amber", "**On hit:** slows the target for 2 seconds."),
            Map.entry("bloodfire_garnet", "**On hit:** heals you half a heart."),
            Map.entry("star_corundum", "**On hit:** makes the target glow for 5 seconds."),
            Map.entry("voidium", "**On hit:** makes the target float up for a second."),
            Map.entry("sapphire", "**Full set:** Haste."),
            Map.entry("black_diamond", "**Full set:** Resistance."),
            Map.entry("topaz", "**Full set:** Luck."),
            Map.entry("chrysoberyl", "**Full set:** Night Vision."),
            Map.entry("aquamarine", "**Full set:** Water Breathing."),
            Map.entry("aetheric_crystal", "**Full set:** Jump Boost."),
            Map.entry("coolpper", "**Full set:** Fire Resistance."),
            Map.entry("arcanthium", "**Full set:** Speed."),
            Map.entry("titanium", "**Armor:** each piece adds knockback resistance, like netherite.")
    );

    /** The crafting recipes shown under each material, where its sword and chestplate would not say enough. */
    private static final Map<String, List<String>> MATERIAL_RECIPES = Map.of(
            "coolpper", List.of("coolpper_ingot_from_alloying", "coolpper_sword"),
            "arcanthium", List.of("arcanthium_from_alloying", "arcanthium_sword"),
            // Voidium gear comes from the smithing table, so show the alloy and the one crafted piece.
            "voidium", List.of("voidium_from_alloying", "voidium_shield")
    );

    private ArcaneCompendium() {
    }

    static List<Category> categories() {
        return List.of(materials(), equipment(), world());
    }

    /** Every translation key the book uses, with its English text. */
    static Map<String, String> english() {
        Map<String, String> english = new LinkedHashMap<>();
        english.put(KEY + ".name", NAME);
        english.put(KEY + ".tooltip", TOOLTIP);
        english.put(KEY + ".description", DESCRIPTION);
        for (Category category : categories()) {
            english.put(category.key() + ".name", category.name());
            english.put(category.key() + ".description", category.description());
            for (Entry entry : category.entries()) {
                english.put(entry.key() + ".name", entry.name());
                english.put(entry.key() + ".description", entry.description());
                for (Page page : entry.pages()) {
                    if (!page.title().isEmpty()) {
                        english.put(page.key(entry) + ".title", page.title());
                    }
                    if (!page.text().isEmpty()) {
                        english.put(page.key(entry) + ".text", markdown(page.text()));
                    }
                }
            }
        }
        return english;
    }

    private static Category materials() {
        List<Entry> entries = new ArrayList<>();
        entries.add(new Entry("materials", "overview", "Arcane Materials", "What every material makes", aa("aetheric_crystal"), List.of(
                Page.text("intro", "Arcane Materials", """
                        Arcane Armory adds 14 materials for gear. Each makes a sword, pickaxe, axe, shovel, hoe, \
                        hammer, bow, and four armor pieces, and some also make a shield. Most are crafted like their \
                        vanilla counterparts; Voidium is upgraded from Black Diamond.

                        Every material has one trait, shown in each item's tooltip."""),
                Page.text("traits", "Traits", """
                        **On hit** traits work when you strike something with that material's weapon or tool in \
                        your main hand.

                        **Full set** bonuses need all four armor pieces of the same material."""))));
        for (ArcaneMaterial material : ArcaneMaterials.ALL) {
            if (material.tools()) {
                entries.add(material(material));
            }
        }
        return new Category("materials", "Materials", "Each material, its trait, and where it comes from", aa("ruby"), entries);
    }

    private static Entry material(ArcaneMaterial material) {
        String id = material.id();
        String found = require(FOUND, id, "location");
        String trait = require(TRAITS, id, "trait");
        List<String> recipes = MATERIAL_RECIPES.getOrDefault(id, List.of(id + "_sword", id + "_chestplate"));
        return new Entry("materials", id, material.displayName(), trait.replace("**", ""), aa(material.materialItemId()), List.of(
                Page.spotlight("about", material.displayName(), aa(material.materialItemId()), found + "\n\n" + trait),
                Page.crafting("recipes", aa(recipes.get(0)), aa(recipes.get(1)))));
    }

    private static Category equipment() {
        List<Entry> entries = new ArrayList<>();
        entries.add(new Entry("equipment", "hammers", "Hammers", "Mine a 3x3 square at once", aa("ruby_hammer"), List.of(
                Page.text("about", "Hammers", """
                        A hammer mines a 3x3 square facing the side you hit. It breaks only blocks it could mine \
                        on its own, and they drop as if the hammer mined each one, so Fortune and Silk Touch apply.

                        Sneak to break a single block."""),
                Page.crafting("recipes", aa("ruby_hammer"), aa("titanium_hammer")))));
        entries.add(new Entry("equipment", "bows", "Bows", "A bow for every material", aa("ruby_bow"), List.of(
                Page.text("about", "Bows", """
                        Every material makes a bow. Arrows from a stronger material's bow hit harder.

                        A bow lasts four fifths as long as its material's tools and draws like a vanilla bow."""),
                Page.crafting("recipes", aa("ruby_bow"), aa("aetheric_crystal_bow")))));
        entries.add(new Entry("equipment", "shields", "Shields", "Five materials make shields", aa("ruby_shield"), List.of(
                Page.text("about", "Shields", """
                        Ruby, Coolpper, Titanium, Arcanthium, and Voidium make shields. They block like a vanilla \
                        shield, wear down as they block, and an axe hit disables them for a moment.

                        A shield lasts nine tenths as long as its material's tools."""),
                Page.crafting("recipes", aa("ruby_shield"), aa("titanium_shield")))));
        //? if >=1.20 {
        entries.add(new Entry("equipment", "trims", "Armor Trims", "Arcane colors for any armor", "minecraft:coast_armor_trim_smithing_template", List.of(
                Page.text("about", "Armor Trims", """
                        Every Arcane gem and ingot that makes gear also trims armor at a smithing table, each in \
                        its own color. So do [](item://arcanearmory:frost_diamond), \
                        [](item://arcanearmory:solarflare_gem), and [](item://arcanearmory:shadow_crystal).

                        Trims show on any armor you wear, and on the icons of Arcane armor."""))));
        entries.add(new Entry("equipment", "voidium_upgrade", "Voidium Upgrade", "Upgrade Black Diamond gear to Voidium", aa("voidium_ingot"), List.of(
                Page.text("about", "Voidium Upgrade", """
                        Voidium gear is not crafted. At a smithing table, combine a **Voidium Upgrade Smithing \
                        Template**, a Black Diamond tool, weapon, or armor piece, and a \
                        [](item://arcanearmory:voidium_ingot). Enchantments carry over.

                        Templates turn up in End City chests."""),
                Page.text("templates", "Templates", """
                        Copy a template with 7 [](item://arcanearmory:void_obsidian_fragment) and \
                        [](item://minecraft:end_stone).

                        Black Diamond has no shield, so the Voidium Shield is still crafted."""),
                Page.smithing("upgrades", aa("voidium_sword_smithing"), aa("voidium_chestplate_smithing")),
                Page.crafting("template_recipe", aa("voidium_upgrade_smithing_template")))));
        //?} else {
        /*entries.add(new Entry("equipment", "voidium_upgrade", "Voidium Upgrade", "Upgrade Black Diamond gear to Voidium", aa("voidium_ingot"), List.of(
                Page.text("about", "Voidium Upgrade", """
                        Voidium gear is not crafted. At a smithing table, combine a Black Diamond tool, weapon, \
                        or armor piece with a [](item://arcanearmory:voidium_ingot). Enchantments carry over.

                        Black Diamond has no shield, so the Voidium Shield is still crafted."""),
                Page.smithing("upgrades", aa("voidium_sword_smithing"), aa("voidium_chestplate_smithing")))));
        *///?}
        entries.add(new Entry("equipment", "doomflare", "Doomflare Block", "Handle with care", aa("doomflare_block"), List.of(
                Page.text("about", "Doomflare Block", """
                        Harmless until an explosion destroys it. Then it explodes with the force of ten TNT, \
                        setting off any Doomflare Blocks nearby."""),
                Page.crafting("recipe", aa("doomflare_block_from_alloying")))));
        return new Category("equipment", "Equipment", "Hammers, bows, shields, and the smithing table", aa("ruby_hammer"), entries);
    }

    private static Category world() {
        List<Entry> entries = new ArrayList<>();
        entries.add(new Entry("world", "gems_and_fragments", "Gems and Fragments", "Finds that make no gear of their own", aa("frost_diamond"), List.of(
                Page.text("about", "Gems and Fragments", """
                        - [](item://arcanearmory:frost_diamond): snowy and frozen biomes, below Y 16.
                        - [](item://arcanearmory:solarflare_gem): deserts and badlands, below Y -32.
                        - [](item://arcanearmory:shadow_crystal): the Nether, between Y 32 and 64.
                        - [](item://arcanearmory:doom_fragment): the Nether, rarely.
                        - [](item://arcanearmory:void_obsidian_fragment): End stone in the End.
                        """),
                Page.text("uses", "Uses", """
                        Frost Diamond crafts into 16 Ice. A Solarflare Gem is a furnace fuel that smelts 12 items.

                        The rest go into Doomflare Blocks and Voidium."""),
                Page.crafting("recipe", aa("ice")))));
        entries.add(new Entry("world", "meteors", "Meteors", "The only source of Star Corundum", aa("star_corundum_ore"), List.of(
                Page.text("craters", "Meteors", """
                        Rarely, the Overworld surface holds a small crater with a boulder of **Meteorite** around a \
                        core of [](item://arcanearmory:star_corundum_ore). Meteors are the only source of Star Corundum."""),
                Page.text("falling", "Falling Meteors", """
                        On Minecraft 1.21.11 and newer, some nights a meteor streaks across the sky and lands as a \
                        fresh boulder, at least 48 blocks from any player."""))));
        entries.add(new Entry("world", "arcanthe", "Arcanthe", "A flower that never grows wild", aa("arcanthe"), List.of(
                Page.spotlight("about", "Arcanthe", aa("arcanthe"), """
                        A pink flower with a glowing teal tip. It never grows in the world, but it can be planted \
                        and potted.

                        It goes into Arcanthium, where a Wither Rose works in its place."""),
                Page.crafting("recipe", aa("arcanthium_from_alloying")))));
        return new Category("world", "World", "Where the rarer finds come from", aa("deepslate_aetheric_crystal_ore"), entries);
    }

    // Modonomicon's markdown joins paragraphs onto one line; a backslash before each line break keeps the gap.
    private static String markdown(String text) {
        return text.replace("\n\n", "\\\n\\\n");
    }

    private static String require(Map<String, String> values, String materialId, String what) {
        String value = values.get(materialId);
        if (value == null) {
            throw new IllegalStateException("The Arcane Compendium has no " + what + " for " + materialId);
        }
        return value;
    }

    private static String aa(String path) {
        return ArcaneArmoryConstants.MOD_ID + ":" + path;
    }

    record Category(String id, String name, String description, String icon, List<Entry> entries) {
        String key() {
            return KEY + "." + id;
        }
    }

    record Entry(String category, String id, String name, String description, String icon, List<Page> pages) {
        String key() {
            return KEY + "." + category + "." + id;
        }
    }

    /**
     * One page. Text and spotlight pages carry English text; recipe pages carry recipe IDs.
     */
    record Page(String id, Type type, String title, String text, String item, List<String> recipes) {
        static Page text(String id, String title, String text) {
            return new Page(id, Type.TEXT, title, text, "", List.of());
        }

        static Page spotlight(String id, String title, String item, String text) {
            return new Page(id, Type.SPOTLIGHT, title, text, item, List.of());
        }

        static Page crafting(String id, String... recipes) {
            return new Page(id, Type.CRAFTING, "", "", "", List.of(recipes));
        }

        static Page smithing(String id, String... recipes) {
            return new Page(id, Type.SMITHING, "", "", "", List.of(recipes));
        }

        String key(Entry entry) {
            return entry.key() + "." + id;
        }
    }

    enum Type {
        TEXT,
        SPOTLIGHT,
        CRAFTING,
        SMITHING
    }
}
