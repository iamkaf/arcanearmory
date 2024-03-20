package com.iamkaf.arcanearmory.block;

import com.iamkaf.arcanearmory.ArcaneArmory;
import com.iamkaf.arcanearmory.block.custom.DoomflareBlock;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowerBlock;
import net.minecraft.block.FlowerPotBlock;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlocks {
    public static final Block ARISTEA = registerBlock("aristea",
            new FlowerBlock(StatusEffects.SPEED,
                    10,
                    FabricBlockSettings.copyOf(Blocks.ALLIUM).nonOpaque().noCollision()
            )
    );
    public static final Block POTTED_ARISTEA = Registry.register(Registries.BLOCK,
            new Identifier(ArcaneArmory.MOD_ID, "potted_aristea"),
            new FlowerPotBlock(ARISTEA,
                    FabricBlockSettings.copyOf(Blocks.POTTED_ALLIUM).nonOpaque()
            )
    );

    public static final Block DOOMFLARE_BLOCK = registerBlock("doomflare_block",
            new DoomflareBlock(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK))
    );

    private static Block registerBlock(String name, Block block) {
        registerBlockItem(name, block);
        return Registry.register(Registries.BLOCK,
                new Identifier(ArcaneArmory.MOD_ID, name),
                block
        );
    }

    private static Item registerBlockItem(String name, Block block) {
        return Registry.register(Registries.ITEM,
                new Identifier(ArcaneArmory.MOD_ID, name),
                new BlockItem(block, new FabricItemSettings())
        );
    }

    public static void registerModBlocks() {
        ArcaneArmory.LOGGER.info("Registering Mod Blocks for " + ArcaneArmory.MOD_ID);
    }
}
