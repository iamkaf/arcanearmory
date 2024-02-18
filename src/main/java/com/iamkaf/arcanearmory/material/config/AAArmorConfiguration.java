package com.iamkaf.arcanearmory.material.config;

import net.minecraft.sound.SoundEvent;

/**
 * Configures how the tools of a Material are created.
 */
public class AAArmorConfiguration {
    public final int durability;
    public final int[] protection;
    public final float toughness;
    public final float knockbackResistance;
    public final int enchantability;
    public final SoundEvent equipSound;

    public AAArmorConfiguration(
            int durability,
            int[] protection,
            float toughness,
            float knockbackResistance,
            int enchantability,
            SoundEvent equipSound
    ) {
        this.durability = durability;
        this.protection = protection;
        this.toughness = toughness;
        this.knockbackResistance = knockbackResistance;
        this.enchantability = enchantability;
        this.equipSound = equipSound;
    }
}
