package com.iamkaf.arcanearmory.material.config;

public class AAGenerationConfiguration {
    public final boolean ore;
    public final boolean weapons;
    public final boolean tools;
    public final boolean armor;

    public AAGenerationConfiguration(boolean ore, boolean weapons, boolean tools, boolean armor) {
        this.ore = ore;
        this.weapons = weapons;
        this.tools = tools;
        this.armor = armor;
    }
}
