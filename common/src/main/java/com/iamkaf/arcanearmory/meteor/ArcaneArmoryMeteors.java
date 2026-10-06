package com.iamkaf.arcanearmory.meteor;

import com.iamkaf.amber.api.registry.v1.DeferredRegister;
import com.iamkaf.arcanearmory.ArcaneArmoryConstants;
//? if >=1.19.3 {
import net.minecraft.core.registries.Registries;
//?} else {
/*import net.minecraft.core.Registry;
*///?}
import net.minecraft.world.level.levelgen.feature.Feature;
//? if >=26.3
/*import com.mojang.serialization.MapCodec;*/

/** Registers the meteor crater feature, and from 1.21.11 the meteors that fall at night. */
public final class ArcaneArmoryMeteors {
    //? if >=26.3 {
    /*private static final DeferredRegister<MapCodec<? extends Feature>> FEATURE_TYPES =
            DeferredRegister.create(ArcaneArmoryConstants.MOD_ID, Registries.FEATURE_TYPE);
    *///?} else if >=1.19.3 {
    private static final DeferredRegister<Feature<?>> FEATURE_TYPES =
            DeferredRegister.create(ArcaneArmoryConstants.MOD_ID, Registries.FEATURE);
    //?} else {
    /*private static final DeferredRegister<Feature<?>> FEATURE_TYPES =
            DeferredRegister.create(ArcaneArmoryConstants.MOD_ID, Registry.FEATURE_REGISTRY);
    *///?}

    static {
        //? if >=26.3 {
        /*FEATURE_TYPES.register("meteor_crater", () -> MeteorCraterFeature.CODEC);
        *///?} else {
        FEATURE_TYPES.register("meteor_crater", () -> new MeteorCraterFeature());
        //?}
    }

    private ArcaneArmoryMeteors() {
    }

    public static void init() {
        FEATURE_TYPES.register();
        //? if >=1.21.11
        LiveMeteors.init();
    }
}
