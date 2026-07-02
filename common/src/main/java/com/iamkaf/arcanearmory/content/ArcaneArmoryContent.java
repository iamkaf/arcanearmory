package com.iamkaf.arcanearmory.content;

import com.iamkaf.amber.api.registry.v1.DeferredRegister;
import com.iamkaf.amber.api.registry.v1.RegistrySupplier;
import com.iamkaf.arcanearmory.ArcaneArmoryConstants;
//? if >=1.19.3 {
import net.minecraft.core.registries.Registries;
//?} else {
import net.minecraft.core.Registry;
//?}
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
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
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ArcaneArmoryConstants.MOD_ID, Registry.ITEM_REGISTRY);
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ArcaneArmoryConstants.MOD_ID, Registry.BLOCK_REGISTRY);
    //?}

    private static final List<RegisteredMaterial> REGISTERED_MATERIALS = new ArrayList<>();
    private static final Map<String, RegistrySupplier<Item>> REGISTERED_ITEMS = new LinkedHashMap<>();

    static {
        registerStandaloneBlock("doomflare_block", true);
        registerStandaloneBlock("aristea", true);
        registerStandaloneBlock("potted_aristea", false);
        for (ArcaneMaterial material : ArcaneMaterials.ALL) {
            REGISTERED_MATERIALS.add(registerMaterial(material));
        }
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

    public static void init() {
        BLOCKS.register();
        ITEMS.register();
        ArcaneArmoryConstants.LOG.info("Registered {} Arcane Armory materials", materialCount());
    }

    private static RegisteredMaterial registerMaterial(ArcaneMaterial material) {
        RegisteredMaterial registered = new RegisteredMaterial(material);

        for (String blockId : material.blockIds()) {
            RegistrySupplier<Block> block = BLOCKS.register(blockId, key -> new Block(blockProperties(key)));
            registered.blocks.add(new RegisteredBlock(blockId, block));
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

    private static void registerStandaloneBlock(String id, boolean withItem) {
        RegistrySupplier<Block> block = BLOCKS.register(id, key -> new Block(blockProperties(key)));
        if (withItem) {
            RegistrySupplier<Item> item = ITEMS.register(id, key -> new BlockItem(block.get(), itemProperties(key)));
            REGISTERED_ITEMS.put(id, item);
        }
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
            return Math.max(1, (int) (material.toolDurability() * 0.8F));
        }
        if (id.endsWith("_shield")) {
            return Math.max(1, (int) (material.toolDurability() * 0.9F));
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
        Item.Properties properties = new Item.Properties();
        //?}
        if (maxDamage > 0) {
            properties.durability(maxDamage);
        }
        return properties;
    }

    private static BlockBehaviour.Properties blockProperties(ResourceKey<Block> key) {
        //? if >=1.21.2 {
        return BlockBehaviour.Properties.ofFullCopy(Blocks.DIAMOND_BLOCK).setId(key);
        //?} else if >=1.21 {
        return BlockBehaviour.Properties.ofFullCopy(Blocks.DIAMOND_BLOCK);
        //?} else {
        return BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK);
        //?}
    }

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
