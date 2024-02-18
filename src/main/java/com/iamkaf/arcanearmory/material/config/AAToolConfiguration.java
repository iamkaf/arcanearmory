package com.iamkaf.arcanearmory.material.config;

/**
 * Configures how the tools of a Material are created.
 */
public class AAToolConfiguration {
    public final int durability;
    public final float swordDamage;
    public final float swordSwingSpeed;
    public final float axeDamage;
    public final float axeSwingSpeed;
    public final float bonusDamage;
    public final int miningLevel;
    public final float miningSpeed;
    public final int enchantability;

    public AAToolConfiguration(
            int durability,
            float swordDamage,
            float swordSwingSpeed,
            float axeDamage,
            float axeSwingSpeed,
            float bonusDamage,
            int miningLevel,
            float miningSpeed,
            int enchantability
    ) {
        this.durability = durability;
        this.swordDamage = swordDamage;
        this.swordSwingSpeed = swordSwingSpeed - 4;
        this.axeDamage = axeDamage;
        this.axeSwingSpeed = axeSwingSpeed - 4;
        this.bonusDamage = bonusDamage;
        this.miningLevel = miningLevel;
        this.miningSpeed = miningSpeed;
        this.enchantability = enchantability;
    }
}
