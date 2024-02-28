package com.iamkaf.arcanearmory.material.config;

public class AANamer {
    private final AAMaterialConfiguration config;

    public AANamer(AAMaterialConfiguration config) {
        this.config = config;
    }

    public String ingot() {
        // more will be added later
        switch (config.type) {
            case GEM, CRYSTAL:
                return config.name;
            default:
                return config.name + "_ingot";
        }
    }

    public String rawMaterial() {
        return "raw_" + config.name;
    }

    public String nugget() {
        return config.name + "_nugget";
    }

    public String oreBlock() {
        return config.name + "_ore";
    }

    public String deepslateOreBlock() {
        return "deepslate_" + config.name + "_ore";
    }

    public String block() {
        return config.name + "_block";
    }

    public String rawBlock() {
        return "raw_" + config.name + "_block";
    }

    public String helmet() {
        return config.name + "_helmet";
    }

    public String chestplate() {
        return config.name + "_chestplate";
    }

    public String leggings() {
        return config.name + "_leggings";
    }

    public String boots() {
        return config.name + "_boots";
    }

    public String sword() {
        return config.name + "_sword";
    }

    public String shovel() {
        return config.name + "_shovel";
    }

    public String pickaxe() {
        return config.name + "_pickaxe";
    }

    public String axe() {
        return config.name + "_axe";
    }

    public String hoe() {
        return config.name + "_hoe";
    }

}
