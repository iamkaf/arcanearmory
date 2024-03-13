package com.iamkaf.arcanearmory.util;

import com.iamkaf.arcanearmory.ArcaneArmory;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class ModTags {
    public static class Blocks {

        private static TagKey<Block> createTag(String name) {
            return TagKey.of(RegistryKeys.BLOCK, new Identifier(ArcaneArmory.MOD_ID, name));
        }
    }

    public static class Items {
        public static final TagKey<Item> ARISTEAS = createCommonTag("aristeas");
        public static final TagKey<Item> TOOLS = createCommonTag("tools");
        public static final TagKey<Item> SWORDS = createCommonTag("swords");
        public static final TagKey<Item> TOOL_SWORDS = createCommonTag("tools/swords");
        public static final TagKey<Item> SHOVELS = createCommonTag("shovels");
        public static final TagKey<Item> TOOL_SHOVELS = createCommonTag("tools/shovels");
        public static final TagKey<Item> AXES = createCommonTag("axes");
        public static final TagKey<Item> TOOL_AXES = createCommonTag("tools/axes");
        public static final TagKey<Item> PICKAXES = createCommonTag("pickaxes");
        public static final TagKey<Item> TOOL_PICKAXES = createCommonTag("tools/pickaxes");
        public static final TagKey<Item> HOES = createCommonTag("hoes");
        public static final TagKey<Item> TOOL_HOES = createCommonTag("tools/hoes");
        public static final TagKey<Item> ARMORS = createCommonTag("armors");
        public static final TagKey<Item> ARMOR = createCommonTag("armor");
        public static final TagKey<Item> HELMETS = createCommonTag("helmets");
        public static final TagKey<Item> CHESTPLATES = createCommonTag("chestplates");
        public static final TagKey<Item> LEGGINGS = createCommonTag("leggings");
        public static final TagKey<Item> BOOTS = createCommonTag("boots");
        public static final TagKey<Item> SHIELDS = createCommonTag("shields");

        public static final TagKey<Item> PLANKS = createCommonTag("planks");
        public static final TagKey<Item> MINECRAFT_PLANKS = createTag("minecraft", "planks");

        private static TagKey<Item> createModTag(String name) {
            return createTag(ArcaneArmory.MOD_ID, name);
        }

        private static TagKey<Item> createCommonTag(String name) {
            return createTag("c", name);
        }

        private static TagKey<Item> createTag(String namespace, String name) {
            return TagKey.of(RegistryKeys.ITEM, new Identifier(namespace, name));
        }
    }
}
