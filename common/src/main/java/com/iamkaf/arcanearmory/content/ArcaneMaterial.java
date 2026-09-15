package com.iamkaf.arcanearmory.content;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public final class ArcaneMaterial {
    private final String id;
    private final String displayName;
    private final boolean ingot;
    private final boolean ore;
    private final boolean shield;
    private final Optional<ToolStats> toolStats;
    private final Optional<ArmorStats> armorStats;

    public ArcaneMaterial(String id, String displayName, boolean ingot, boolean ore, boolean shield,
            ToolStats toolStats, ArmorStats armorStats) {
        this.id = id;
        this.displayName = displayName;
        this.ingot = ingot;
        this.ore = ore;
        this.shield = shield;
        this.toolStats = Optional.ofNullable(toolStats);
        this.armorStats = Optional.ofNullable(armorStats);
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
        return toolStats.isPresent();
    }

    public boolean armor() {
        return armorStats.isPresent();
    }

    public boolean shield() {
        return shield;
    }

    public int toolDurability() {
        return requireToolStats().durability();
    }

    public ToolStats requireToolStats() {
        return toolStats.orElseThrow(() -> new IllegalStateException(id + " does not define tools"));
    }

    public ArmorStats requireArmorStats() {
        return armorStats.orElseThrow(() -> new IllegalStateException(id + " does not define armor"));
    }

    public int bowDurability() {
        return Math.max(1, (int) (requireToolStats().durability() * 0.8F));
    }

    public float bowDamage() {
        ToolStats stats = requireToolStats();
        return stats.axeDamage() + stats.attackDamageBonus();
    }

    public int shieldDurability() {
        return Math.max(1, (int) (requireToolStats().durability() * 0.9F));
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
        if (tools()) {
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
        if (armor()) {
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

    public record ToolStats(
            int durability,
            float swordDamage,
            float axeDamage,
            float attackDamageBonus,
            int miningLevel,
            float miningSpeed,
            int enchantmentValue
    ) {
    }

    public record ArmorStats(
            int baseDurability,
            int helmetProtection,
            int chestplateProtection,
            int leggingsProtection,
            int bootsProtection,
            int enchantmentValue,
            float toughness,
            float knockbackResistance
    ) {
        public int durability(ArmorPiece piece) {
            return baseDurability * piece.durabilityMultiplier() / 55;
        }

        public int protection(ArmorPiece piece) {
            return switch (piece) {
                case HELMET -> helmetProtection;
                case CHESTPLATE -> chestplateProtection;
                case LEGGINGS -> leggingsProtection;
                case BOOTS -> bootsProtection;
            };
        }
    }

    public enum ArmorPiece {
        HELMET(11),
        CHESTPLATE(16),
        LEGGINGS(15),
        BOOTS(13);

        private final int durabilityMultiplier;

        ArmorPiece(int durabilityMultiplier) {
            this.durabilityMultiplier = durabilityMultiplier;
        }

        public int durabilityMultiplier() {
            return durabilityMultiplier;
        }
    }
}
