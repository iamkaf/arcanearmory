package com.iamkaf.arcanearmory.material;

import com.iamkaf.arcanearmory.material.config.*;
import com.iamkaf.arcanearmory.material.util.ColorUtil;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.yarn.constants.MiningLevels;
import net.minecraft.item.Item;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

import java.util.function.Supplier;

public class AAAutoloadMaterialBuilder {
    private Item material;
    private String name;
    private AAMaterialType type;
    private Supplier<Ingredient> repairIngredient;

    private int armorDurability;
    private int toolDurability;
    private float swordDamage;
    private float swordSwingSpeed;
    private float axeDamage;
    private float axeSwingSpeed;
    private float bonusDamage;
    private int miningLevel;
    private float miningSpeed;
    private int enchantability;
    private int[] protection;
    private float toughness;
    private float knockbackResistance;
    private SoundEvent equipSound;
    private int miningLevelRequired;
    private int veinsPerChunk;
    private int veinSize;
    private int minYOffset;
    private int maxYOffset;
    private float minDrops;
    private float maxDrops;
    private boolean spawnInOverworld;
    private boolean spawnInTheNether;
    private boolean spawnInTheEnd;
    private boolean createOre;
    private boolean createWeapons;
    private boolean createTools;
    private boolean createArmor;

    private AAAutoloadMaterialBuilder() {
    }

    public static AAAutoloadMaterialBuilder create() {
        return new AAAutoloadMaterialBuilder();
    }

    public AAAutoloadMaterialBuilder armorDurability(int durability) {
        this.armorDurability = durability;
        return this;
    }

    public AAAutoloadMaterialBuilder toolDurability(int durability) {
        this.toolDurability = durability;
        return this;
    }

    public AAAutoloadMaterialBuilder swordDamage(float swordDamage) {
        this.swordDamage = swordDamage;
        return this;
    }

    public AAAutoloadMaterialBuilder swordSwingSpeed(float swordSwingSpeed) {
        this.swordSwingSpeed = swordSwingSpeed;
        return this;
    }

    public AAAutoloadMaterialBuilder axeDamage(float axeDamage) {
        this.axeDamage = axeDamage;
        return this;
    }

    public AAAutoloadMaterialBuilder axeSwingSpeed(float axeSwingSpeed) {
        this.axeSwingSpeed = axeSwingSpeed;
        return this;
    }

    public AAAutoloadMaterialBuilder bonusDamage(float bonusDamage) {
        this.bonusDamage = bonusDamage;
        return this;
    }

    public AAAutoloadMaterialBuilder miningLevel(int miningLevel) {
        this.miningLevel = miningLevel;
        return this;
    }

    public AAAutoloadMaterialBuilder miningSpeed(float miningSpeed) {
        this.miningSpeed = miningSpeed;
        return this;
    }

    public AAAutoloadMaterialBuilder enchantability(int enchantability) {
        this.enchantability = enchantability;
        return this;
    }

    public AAAutoloadMaterialBuilder protection(int[] protection) {
        this.protection = protection;
        return this;
    }

    public AAAutoloadMaterialBuilder toughness(float toughness) {
        this.toughness = toughness;
        return this;
    }

    public AAAutoloadMaterialBuilder knockbackResistance(float knockbackResistance) {
        this.knockbackResistance = knockbackResistance;
        return this;
    }

    public AAAutoloadMaterialBuilder equipSound(SoundEvent equipSound) {
        this.equipSound = equipSound;
        return this;
    }

    public AAAutoloadMaterialBuilder miningLevelRequired(int miningLevelRequired) {
        this.miningLevelRequired = miningLevelRequired;
        return this;
    }

    public AAAutoloadMaterialBuilder veinsPerChunk(int veinsPerChunk) {
        this.veinsPerChunk = veinsPerChunk;
        return this;
    }

    public AAAutoloadMaterialBuilder veinSize(int veinSize) {
        this.veinSize = veinSize;
        return this;
    }

    public AAAutoloadMaterialBuilder minYOffset(int minYOffset) {
        this.minYOffset = minYOffset;
        return this;
    }

    public AAAutoloadMaterialBuilder maxYOffset(int maxYOffset) {
        this.maxYOffset = maxYOffset;
        return this;
    }

    public AAAutoloadMaterialBuilder minDrops(float minDrops) {
        this.minDrops = minDrops;
        return this;
    }

    public AAAutoloadMaterialBuilder maxDrops(float maxDrops) {
        this.maxDrops = maxDrops;
        return this;
    }

    public AAAutoloadMaterialBuilder spawnInOverworld(boolean spawnInOverworld) {
        this.spawnInOverworld = spawnInOverworld;
        return this;
    }

    public AAAutoloadMaterialBuilder spawnInTheNether(boolean spawnInTheNether) {
        this.spawnInTheNether = spawnInTheNether;
        return this;
    }

    public AAAutoloadMaterialBuilder spawnInTheEnd(boolean spawnInTheEnd) {
        this.spawnInTheEnd = spawnInTheEnd;
        return this;
    }

    public void checkDimensions() {
        int count = 0;

        if (spawnInOverworld) {
            count++;
        }

        if (spawnInTheNether) {
            count++;
        }

        if (spawnInTheEnd) {
            count++;
        }

        if (count > 1) {
            throw new IllegalStateException("Only one of spawnInOverworld, spawnInTheNether, spawnInTheEnd can be set to true");
        }
    }


    public AAAutoloadMaterialBuilder createOre(boolean createOre) {
        this.createOre = createOre;
        return this;
    }

    public AAAutoloadMaterialBuilder createWeapons(boolean createWeapons) {
        this.createWeapons = createWeapons;
        return this;
    }

    public AAAutoloadMaterialBuilder createTools(boolean createTools) {
        this.createTools = createTools;
        return this;
    }

    public AAAutoloadMaterialBuilder createArmor(boolean createArmor) {
        this.createArmor = createArmor;
        return this;
    }

    public AAAutoloadMaterialBuilder from(AAMaterialType type, String name) {
        this.name = name;
        this.type = type;
        material = new Item(new FabricItemSettings());
        this.repairIngredient = () -> Ingredient.ofItems(material);

        this.miningLevelRequired = 2;
        this.veinsPerChunk = 2;
        this.veinSize = 6;
        this.minYOffset = -64;
        this.maxYOffset = 16;
        this.minDrops = 1;
        this.maxDrops = 1;
        this.spawnInOverworld = true;
        this.spawnInTheNether = false;
        this.spawnInTheEnd = false;

        this.armorDurability = 6666;
        this.protection = new int[]{6, 6, 6, 6};
        this.toughness = 2f;
        this.knockbackResistance = 0.1f;
        this.enchantability = 10;
        this.equipSound = SoundEvents.ITEM_ARMOR_EQUIP_IRON;

        this.toolDurability = 1561;
        this.swordDamage = 6;
        this.swordSwingSpeed = 2f - 4;
        this.axeDamage = 8;
        this.axeSwingSpeed = 1.5f - 4;
        this.bonusDamage = 4;
        this.miningLevel = MiningLevels.DIAMOND;
        this.miningSpeed = 12;

        this.createOre = true;
        this.createWeapons = true;
        this.createTools = true;
        this.createArmor = true;

        return this;
    }

    public AAMaterialAutoload build() {
        return new AAMaterialAutoload(new AAMaterialConfiguration(name,
                material,
                type,
                ColorUtil.tint(),
                ColorUtil.tint(),
                new AABlockConfiguration(miningLevelRequired,
                        veinsPerChunk,
                        veinSize,
                        minYOffset,
                        maxYOffset,
                        minDrops,
                        maxDrops,
                        spawnInOverworld,
                        spawnInTheNether,
                        spawnInTheEnd
                ),
                new AAArmorConfiguration(armorDurability,
                        protection,
                        toughness,
                        knockbackResistance,
                        enchantability,
                        equipSound
                ),
                new AAToolConfiguration(toolDurability,
                        swordDamage,
                        swordSwingSpeed,
                        axeDamage,
                        axeSwingSpeed,
                        bonusDamage,
                        miningLevel,
                        miningSpeed,
                        enchantability
                ),
                new AAGenerationConfiguration(createOre, createWeapons, createTools, createArmor)
        ));
    }

    public static AAAutoloadMaterialBuilder copyOf(AAAutoloadMaterialBuilder builder, String name) {
        AAAutoloadMaterialBuilder newBuilder = AAAutoloadMaterialBuilder.create();

        newBuilder.name = name;
        newBuilder.material = new Item(new FabricItemSettings());
        newBuilder.type = builder.type;
        newBuilder.repairIngredient = () -> Ingredient.ofItems(newBuilder.material);

        newBuilder.armorDurability = builder.armorDurability;
        newBuilder.toolDurability = builder.toolDurability;
        newBuilder.swordDamage = builder.swordDamage;
        newBuilder.swordSwingSpeed = builder.swordSwingSpeed;
        newBuilder.axeDamage = builder.axeDamage;
        newBuilder.axeSwingSpeed = builder.axeSwingSpeed;
        newBuilder.bonusDamage = builder.bonusDamage;
        newBuilder.miningLevel = builder.miningLevel;
        newBuilder.miningSpeed = builder.miningSpeed;
        newBuilder.enchantability = builder.enchantability;
        newBuilder.protection = builder.protection;
        newBuilder.toughness = builder.toughness;
        newBuilder.knockbackResistance = builder.knockbackResistance;
        newBuilder.equipSound = builder.equipSound;
        newBuilder.miningLevelRequired = builder.miningLevelRequired;
        newBuilder.veinsPerChunk = builder.veinsPerChunk;
        newBuilder.veinSize = builder.veinSize;
        newBuilder.minYOffset = builder.minYOffset;
        newBuilder.maxYOffset = builder.maxYOffset;
        newBuilder.minDrops = builder.minDrops;
        newBuilder.maxDrops = builder.maxDrops;
        newBuilder.spawnInOverworld(builder.spawnInOverworld);
        newBuilder.spawnInTheNether(builder.spawnInTheNether);
        newBuilder.spawnInTheEnd(builder.spawnInTheEnd);
        newBuilder.createOre = builder.createOre;
        newBuilder.createWeapons = builder.createWeapons;
        newBuilder.createTools = builder.createTools;
        newBuilder.createArmor = builder.createArmor;

        return newBuilder;
    }

    public static AAAutoloadMaterialBuilder copyOf(AAMaterialConfiguration config, String name) {
        AAAutoloadMaterialBuilder newBuilder = AAAutoloadMaterialBuilder.create();

        newBuilder.name = name;
        newBuilder.material = new Item(new FabricItemSettings());
        newBuilder.type = config.type;
        newBuilder.repairIngredient = () -> Ingredient.ofItems(newBuilder.material);

        newBuilder.armorDurability = config.armorConfiguration.durability;
        newBuilder.toolDurability = config.toolConfiguration.durability;
        newBuilder.swordDamage = config.toolConfiguration.swordDamage;
        newBuilder.swordSwingSpeed = config.toolConfiguration.swordSwingSpeed + 4; // FIXME: speed
        newBuilder.axeDamage = config.toolConfiguration.axeDamage;
        newBuilder.axeSwingSpeed = config.toolConfiguration.axeSwingSpeed + 4; // FIXME: speed
        newBuilder.bonusDamage = config.toolConfiguration.bonusDamage;
        newBuilder.miningLevel = config.toolConfiguration.miningLevel;
        newBuilder.miningSpeed = config.toolConfiguration.miningSpeed;
        newBuilder.enchantability = config.toolConfiguration.enchantability;
        newBuilder.protection = config.armorConfiguration.protection;
        newBuilder.toughness = config.armorConfiguration.toughness;
        newBuilder.knockbackResistance = config.armorConfiguration.knockbackResistance;
        newBuilder.equipSound = config.armorConfiguration.equipSound;
        newBuilder.miningLevelRequired = config.oreConfiguration.miningLevelRequired;
        newBuilder.veinsPerChunk = config.oreConfiguration.veinsPerChunk;
        newBuilder.veinSize = config.oreConfiguration.veinSize;
        newBuilder.minYOffset = config.oreConfiguration.minYOffset;
        newBuilder.maxYOffset = config.oreConfiguration.maxYOffset;
        newBuilder.minDrops = config.oreConfiguration.minDrops;
        newBuilder.maxDrops = config.oreConfiguration.maxDrops;
        newBuilder.spawnInOverworld(config.oreConfiguration.spawnInOverworld);
        newBuilder.spawnInTheNether(config.oreConfiguration.spawnInTheNether);
        newBuilder.spawnInTheEnd(config.oreConfiguration.spawnInTheEnd);
        newBuilder.createOre = config.generationConfiguration.ore;
        newBuilder.createWeapons = config.generationConfiguration.weapons;
        newBuilder.createTools = config.generationConfiguration.tools;
        newBuilder.createArmor = config.generationConfiguration.armor;

        return newBuilder;
    }
}
