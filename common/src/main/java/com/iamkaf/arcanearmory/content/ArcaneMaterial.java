package com.iamkaf.arcanearmory.content;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ArcaneMaterial {
    private final String id;
    private final String displayName;
    private final boolean ingot;
    private final boolean ore;
    private final boolean tools;
    private final boolean armor;
    private final boolean shield;
    private final int toolDurability;

    public ArcaneMaterial(String id, String displayName, boolean ingot, boolean ore, boolean tools, boolean armor, boolean shield,
            int toolDurability) {
        this.id = id;
        this.displayName = displayName;
        this.ingot = ingot;
        this.ore = ore;
        this.tools = tools;
        this.armor = armor;
        this.shield = shield;
        this.toolDurability = toolDurability;
    }

    public String id() {
        return id;
    }

    public String displayName() {
        return displayName;
    }

    public boolean ingot() {
        return ingot;
    }

    public boolean ore() {
        return ore;
    }

    public boolean tools() {
        return tools;
    }

    public boolean armor() {
        return armor;
    }

    public boolean shield() {
        return shield;
    }

    public int toolDurability() {
        return toolDurability;
    }

    public int attackDamageBonus() {
        if (toolDurability >= 2000) {
            return 2;
        }
        if (toolDurability >= 1561) {
            return 1;
        }
        return 0;
    }

    public float bowDamageBonus() {
        return attackDamageBonus() * 0.75F;
    }

    public List<String> itemIds() {
        List<String> ids = new ArrayList<>();
        ids.add(materialItemId());
        if (ore) {
            ids.add(rawMaterialItemId());
        }
        if (ingot) {
            ids.add(nuggetItemId());
        }
        if (tools) {
            ids.add(id + "_sword");
            ids.add(id + "_shovel");
            ids.add(id + "_pickaxe");
            ids.add(id + "_axe");
            ids.add(id + "_hoe");
            ids.add(id + "_hammer");
            ids.add(id + "_bow");
        }
        if (shield) {
            ids.add(id + "_shield");
        }
        if (armor) {
            ids.add(id + "_helmet");
            ids.add(id + "_chestplate");
            ids.add(id + "_leggings");
            ids.add(id + "_boots");
        }
        return Collections.unmodifiableList(ids);
    }

    public List<String> blockIds() {
        List<String> ids = new ArrayList<>();
        ids.add(id + "_block");
        if (ore) {
            ids.add(id + "_ore");
            ids.add("deepslate_" + id + "_ore");
            ids.add("raw_" + id + "_block");
        }
        return Collections.unmodifiableList(ids);
    }

    public String materialItemId() {
        return ingot ? id + "_ingot" : id;
    }

    public String rawMaterialItemId() {
        return "raw_" + id;
    }

    public String nuggetItemId() {
        return id + "_nugget";
    }
}
