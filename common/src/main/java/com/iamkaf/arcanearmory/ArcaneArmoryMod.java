package com.iamkaf.arcanearmory;

import com.iamkaf.amber.api.core.v2.AmberInitializer;
import com.iamkaf.arcanearmory.content.ArcaneArmoryContent;
import com.iamkaf.arcanearmory.content.ArcaneArmoryLoot;
import com.iamkaf.arcanearmory.content.ArcaneHammerEvents;
import org.jetbrains.annotations.Nullable;

public final class ArcaneArmoryMod {
    private ArcaneArmoryMod() {
    }

    public static void init() {
        init(null);
    }

    public static void init(@Nullable Object eventBus) {
        ArcaneArmoryConstants.LOG.info("Initializing {}...", ArcaneArmoryConstants.MOD_NAME);
        AmberInitializer.initialize(ArcaneArmoryConstants.MOD_ID);
        ArcaneArmoryContent.init();
        ArcaneHammerEvents.init();
        ArcaneArmoryLoot.init();
    }
}
