package com.iamkaf.arcanearmory.fabric;

import com.iamkaf.arcanearmory.fabric.datagen.ArcaneArmoryLanguageProvider;
import com.iamkaf.arcanearmory.fabric.datagen.ArcaneArmoryModelProvider;
import com.iamkaf.arcanearmory.fabric.datagen.ArcaneArmoryRecipeProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public final class ArcaneArmoryDatagen implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        //? if >=1.19.3 {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        pack.addProvider(ArcaneArmoryModelProvider::new);
        pack.addProvider(ArcaneArmoryRecipeProvider::new);
        pack.addProvider(ArcaneArmoryLanguageProvider::new);
        //?} else {
        fabricDataGenerator.addProvider(ArcaneArmoryModelProvider::new);
        fabricDataGenerator.addProvider(ArcaneArmoryLanguageProvider::new);
        //?}
    }
}
