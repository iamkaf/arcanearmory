package com.iamkaf.arcanearmory.content;

import com.iamkaf.amber.api.registry.v1.DeferredRegister;
import com.iamkaf.amber.api.registry.v1.RegistrySupplier;
import com.iamkaf.arcanearmory.ArcaneArmoryConstants;
//? if >=1.19.3 {
import net.minecraft.core.registries.Registries;
//?} else {
/*import net.minecraft.core.Registry;
*///?}
//? if >=1.20.5
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class ArcaneArmoryContent {
    //? if >=1.19.3 {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ArcaneArmoryConstants.MOD_ID, Registries.ITEM);
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ArcaneArmoryConstants.MOD_ID, Registries.BLOCK);
    //?} else {
    /*public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ArcaneArmoryConstants.MOD_ID, Registry.ITEM_REGISTRY);
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ArcaneArmoryConstants.MOD_ID, Registry.BLOCK_REGISTRY);
    *///?}

    // Arcanthe carries the original release's suspicious stew effect: Speed for ten seconds.
    //? if >=1.20.5 {
    private static final float ARCANTHE_STEW_SECONDS = 10.0F;
    //?} else {
    /*private static final int ARCANTHE_STEW_SECONDS = 10;
    *///?}

    /** Blocks with transparent texture pixels that need the cutout render layer before 26.1. */
    public static final List<String> CUTOUT_BLOCKS = List.of("arcanthe", "potted_arcanthe");

    private static final List<RegisteredMaterial> REGISTERED_MATERIALS = new ArrayList<>();
    private static final Map<String, RegistrySupplier<Item>> REGISTERED_ITEMS = new LinkedHashMap<>();
    private static final Map<String, RegistrySupplier<Block>> REGISTERED_BLOCKS = new LinkedHashMap<>();

    static {
        RegistrySupplier<Block> doomflare = BLOCKS.register("doomflare_block",
                key -> new DoomflareBlock(copyOf(Blocks.IRON_BLOCK, key)));
        REGISTERED_BLOCKS.put("doomflare_block", doomflare);
        REGISTERED_ITEMS.put("doomflare_block", ITEMS.register("doomflare_block",
                key -> new DoomflareBlockItem(doomflare.get(), itemProperties(key))));

        RegistrySupplier<Block> arcanthe = BLOCKS.register("arcanthe",
                key -> new FlowerBlock(arcantheStewEffect(), ARCANTHE_STEW_SECONDS, copyOf(Blocks.ALLIUM, key)));
        REGISTERED_BLOCKS.put("arcanthe", arcanthe);
        REGISTERED_ITEMS.put("arcanthe", ITEMS.register("arcanthe",
                key -> new BlockItem(arcanthe.get(), itemProperties(key))));
        REGISTERED_BLOCKS.put("potted_arcanthe", BLOCKS.register("potted_arcanthe",
                key -> new FlowerPotBlock(arcanthe.get(), copyOf(Blocks.POTTED_ALLIUM, key))));

        RegistrySupplier<Block> meteorite = BLOCKS.register("meteorite",
                key -> new Block(copyOf(Blocks.DEEPSLATE_IRON_ORE, key)));
        REGISTERED_BLOCKS.put("meteorite", meteorite);
        REGISTERED_ITEMS.put("meteorite", ITEMS.register("meteorite",
                key -> new BlockItem(meteorite.get(), itemProperties(key))));

        for (ArcaneMaterial material : ArcaneMaterials.ALL) {
            REGISTERED_MATERIALS.add(registerMaterial(material));
        }

        // The original alloy forge turned raw amber into this ingot; nothing consumes it.
        REGISTERED_ITEMS.put("amber_ingot", ITEMS.register("amber_ingot", key -> new Item(itemProperties(key))));

        //? if >=1.20 {
        REGISTERED_ITEMS.put(VoidiumUpgrade.TEMPLATE_ID, ITEMS.register(VoidiumUpgrade.TEMPLATE_ID,
                key -> VoidiumUpgrade.template(itemProperties(key))));
        //?}
    }

    private ArcaneArmoryContent() {
    }

    public static List<RegisteredMaterial> registeredMaterials() {
        return Collections.unmodifiableList(REGISTERED_MATERIALS);
    }

    public static int materialCount() {
        return REGISTERED_MATERIALS.size();
    }

    public static Optional<RegistrySupplier<Item>> item(String id) {
        return Optional.ofNullable(REGISTERED_ITEMS.get(id));
    }

    public static Optional<RegistrySupplier<Block>> block(String id) {
        return Optional.ofNullable(REGISTERED_BLOCKS.get(id));
    }

    public static void init() {
        BLOCKS.register();
        ITEMS.register();
        ArcaneArmoryConstants.LOG.info("Registered {} Arcane Armory materials", materialCount());
    }

    private static RegisteredMaterial registerMaterial(ArcaneMaterial material) {
        RegisteredMaterial registered = new RegisteredMaterial(material);

        for (String blockId : material.blockIds()) {
            Block template = blockTemplate(material, blockId);
            RegistrySupplier<Block> block = BLOCKS.register(blockId, key -> new Block(copyOf(template, key)));
            registered.blocks.add(new RegisteredBlock(blockId, block));
            REGISTERED_BLOCKS.put(blockId, block);
            RegistrySupplier<Item> item = ITEMS.register(blockId, key -> new BlockItem(block.get(), itemProperties(key)));
            REGISTERED_ITEMS.put(blockId, item);
        }

        for (String itemId : material.itemIds()) {
            RegistrySupplier<Item> item = ITEMS.register(itemId, key -> createItem(material, itemId, key));
            registered.items.add(new RegisteredItem(itemId, item));
            REGISTERED_ITEMS.put(itemId, item);
        }

        return registered;
    }

    // Matches the original release: ores behave like iron ore, raw blocks like raw iron, storage like diamond.
    private static Block blockTemplate(ArcaneMaterial material, String blockId) {
        if (blockId.equals("deepslate_" + material.id() + "_ore")) {
            return Blocks.DEEPSLATE_IRON_ORE;
        }
        if (blockId.equals(material.id() + "_ore")) {
            return Blocks.IRON_ORE;
        }
        if (blockId.equals("raw_" + material.id() + "_block")) {
            return Blocks.RAW_IRON_BLOCK;
        }
        return Blocks.DIAMOND_BLOCK;
    }

    private static Item createItem(ArcaneMaterial material, String id, ResourceKey<Item> key) {
        Item.Properties properties = itemProperties(key, maxDamage(material, id));
        return ArcaneArmoryItemFactory.create(material, id, properties);
    }

    private static int maxDamage(ArcaneMaterial material, String id) {
        if (id.endsWith("_hammer")) {
            return material.toolDurability();
        }
        if (id.endsWith("_bow")) {
            return material.bowDurability();
        }
        if (id.endsWith("_shield")) {
            return material.shieldDurability();
        }
        return 0;
    }

    private static Item.Properties itemProperties(ResourceKey<Item> key) {
        return itemProperties(key, 0);
    }

    private static Item.Properties itemProperties(ResourceKey<Item> key, int maxDamage) {
        //? if >=1.21.2 {
        Item.Properties properties = new Item.Properties().setId(key);
        //?} else {
        /*Item.Properties properties = new Item.Properties();
        *///?}
        //? if >=26.3 {
        /*String path = key.identifier().getPath();
        if (path.equals("solarflare_gem") || path.equals("solarflare_gem_block")) {
            properties.cookingFuel(ResourceKey.create(Registries.CONTEXT_INT_PROVIDER, key.identifier()));
        }
        *///?}
        //? if >=1.21.5 {
        String itemId = key.identifier().getPath();
        for (ArcaneTrimMaterials.Trim trim : ArcaneTrimMaterials.ALL) {
            if (trim.ingredientId().equals(itemId)) {
                properties.trimMaterial(ResourceKey.create(Registries.TRIM_MATERIAL, ArcaneArmoryConstants.resource(trim.id())));
            }
        }
        //?}
        if (maxDamage > 0) {
            properties.durability(maxDamage);
        }
        return properties;
    }

    private static BlockBehaviour.Properties copyOf(Block template, ResourceKey<Block> key) {
        //? if >=1.21.2 {
        return BlockBehaviour.Properties.ofFullCopy(template).setId(key);
        //?} else if >=1.21 {
        /*return BlockBehaviour.Properties.ofFullCopy(template);
        *///?} else {
        /*return BlockBehaviour.Properties.copy(template);
        *///?}
    }

    //? if >=1.20.5 {
    private static Holder<MobEffect> arcantheStewEffect() {
        //? if >=1.21.5 {
        return MobEffects.SPEED;
        //?} else {
        /*return MobEffects.MOVEMENT_SPEED;
        *///?}
    }
    //?} else {
    /*private static MobEffect arcantheStewEffect() {
        return MobEffects.MOVEMENT_SPEED;
    }
    *///?}

    public static final class RegisteredMaterial {
        private final ArcaneMaterial material;
        private final List<RegisteredItem> items = new ArrayList<>();
        private final List<RegisteredBlock> blocks = new ArrayList<>();

        private RegisteredMaterial(ArcaneMaterial material) {
            this.material = material;
        }

        public ArcaneMaterial material() {
            return material;
        }

        public List<RegisteredItem> items() {
            return Collections.unmodifiableList(items);
        }

        public List<RegisteredBlock> blocks() {
            return Collections.unmodifiableList(blocks);
        }
    }

    public record RegisteredItem(String id, RegistrySupplier<Item> item) {
    }

    public record RegisteredBlock(String id, RegistrySupplier<Block> block) {
    }
}
