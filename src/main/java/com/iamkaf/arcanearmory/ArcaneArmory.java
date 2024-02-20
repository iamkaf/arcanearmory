package com.iamkaf.arcanearmory;

import com.iamkaf.arcanearmory.block.ModBlocks;
import com.iamkaf.arcanearmory.item.ModItemGroups;
import com.iamkaf.arcanearmory.item.ModItems;
import com.iamkaf.arcanearmory.material.ModMaterials;
import com.iamkaf.arcanearmory.sound.ModSounds;
import com.iamkaf.arcanearmory.util.ModCustomTrades;
import com.iamkaf.arcanearmory.util.ModLootTableModifiers;
import com.iamkaf.arcanearmory.world.gen.ModWorldGeneration;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ArcaneArmory implements ModInitializer {
    public static final String MOD_ID = "arcanearmory";
    public static final Logger LOGGER = LoggerFactory.getLogger(ArcaneArmory.MOD_ID);

    public static Identifier ID(String name) {
        return new Identifier(MOD_ID, name);
    }

    @Override
    public void onInitialize() {
        ModItemGroups.registerItemGroups();
        ModItems.registerModItems();
        ModBlocks.registerModBlocks();
        ModLootTableModifiers.modifyLootTables();
        ModCustomTrades.registerCustomTrades();
        ModSounds.registerSounds();
        ModMaterials.initializeArcaneArmoryMaterials();
        ModWorldGeneration.generateModWorldGen();
    }
}