package com.iamkaf.arcanearmory.content;

public enum ArcaneArmoryItemKinds {
    MATERIAL(""),
    RAW_MATERIAL("Raw "),
    NUGGET("", " Nugget"),
    SWORD("", " Sword"),
    SHOVEL("", " Shovel"),
    PICKAXE("", " Pickaxe"),
    AXE("", " Axe"),
    HOE("", " Hoe"),
    HAMMER("", " Hammer"),
    BOW("", " Bow"),
    SHIELD("", " Shield"),
    HELMET("", " Helmet"),
    CHESTPLATE("", " Chestplate"),
    LEGGINGS("", " Leggings"),
    BOOTS("", " Boots");

    private final String prefix;
    private final String suffix;

    ArcaneArmoryItemKinds(String prefix) {
        this(prefix, "");
    }

    ArcaneArmoryItemKinds(String prefix, String suffix) {
        this.prefix = prefix;
        this.suffix = suffix;
    }

    public String displayName(String materialName) {
        return prefix + materialName + suffix;
    }
}
