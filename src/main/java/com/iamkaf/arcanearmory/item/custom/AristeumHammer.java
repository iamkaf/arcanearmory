package com.iamkaf.arcanearmory.item.custom;

import com.iamkaf.arcanearmory.material.ModMaterials;
import dev.draylar.magna.item.HammerItem;

public class AristeumHammer extends HammerItem {
    public AristeumHammer(
            Settings settings
    ) {
        super(
                ModMaterials.ARISTEUM.SWORD.getMaterial(),
                (int) ModMaterials.ARISTEUM.PICKAXE.getAttackDamage(),
                ModMaterials.ARISTEUM.toolConfiguration.axeSwingSpeed,
                settings
        );
    }
}
