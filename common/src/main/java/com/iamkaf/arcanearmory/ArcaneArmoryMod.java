package com.iamkaf.arcanearmory;

import com.iamkaf.arcanearmory.content.ArcaneArmoryContent;
import com.iamkaf.arcanearmory.content.ArcaneArmoryCreativeTab;
import com.iamkaf.arcanearmory.content.ArcaneArmoryLoot;
import com.iamkaf.arcanearmory.content.ArcaneTraits;
import com.iamkaf.arcanearmory.meteor.ArcaneArmoryMeteors;
import org.jetbrains.annotations.Nullable;

public final class ArcaneArmoryMod {
    private ArcaneArmoryMod() {
    }

    public static void init() {
        init(null);
    }

    public static void init(@Nullable Object eventBus) {
        ArcaneArmoryConstants.LOG.info("Initializing {}...", ArcaneArmoryConstants.MOD_NAME);
        ArcaneArmoryContent.init();
        ArcaneArmoryCreativeTab.init();
        ArcaneArmoryLoot.init();
        ArcaneTraits.init();
        ArcaneArmoryMeteors.init();
    }
}
