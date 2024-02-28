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

public class AAMaterialBuilder {
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

    private AAMaterialBuilder() {
    }

    public static AAMaterialBuilder create() {
        return new AAMaterialBuilder();
    }

    public static AAMaterialBuilder create(AAMaterialType type, String name) {
        var newBuilder = create();
        newBuilder.name = name;
        newBuilder.type = type;
        newBuilder.material = new Item(new FabricItemSettings());
        newBuilder.repairIngredient = () -> Ingredient.ofItems(newBuilder.material);

        newBuilder.miningLevelRequired = 2;
        newBuilder.veinsPerChunk = 2;
        newBuilder.veinSize = 6;
        newBuilder.minYOffset = -64;
        newBuilder.maxYOffset = 16;
        newBuilder.minDrops = 1;
        newBuilder.maxDrops = 1;
        newBuilder.spawnInOverworld = true;
        newBuilder.spawnInTheNether = false;
        newBuilder.spawnInTheEnd = false;

        newBuilder.armorDurability = 6666;
        newBuilder.protection = new int[]{6, 6, 6, 6};
        newBuilder.toughness = 2f;
        newBuilder.knockbackResistance = 0.1f;
        newBuilder.enchantability = 10;
        newBuilder.equipSound = SoundEvents.ITEM_ARMOR_EQUIP_IRON;

        newBuilder.toolDurability = 1561;
        newBuilder.swordDamage = 6;
        newBuilder.swordSwingSpeed = 2f - 4;
        newBuilder.axeDamage = 8;
        newBuilder.axeSwingSpeed = 1.5f - 4;
        newBuilder.bonusDamage = 4;
        newBuilder.miningLevel = MiningLevels.DIAMOND;
        newBuilder.miningSpeed = 12;

        newBuilder.createOre = true;
        newBuilder.createWeapons = true;
        newBuilder.createTools = true;
        newBuilder.createArmor = true;

        return newBuilder;
    }

    public static AAMaterialBuilder copyOf(AAMaterialBuilder builder, String name) {
        AAMaterialBuilder newBuilder = AAMaterialBuilder.create();

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

    public static AAMaterialBuilder copyOf(AAMaterialConfiguration config, String name) {
        AAMaterialBuilder newBuilder = AAMaterialBuilder.create();

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

    public AAMaterialBuilder type(AAMaterialType type) {
        this.type = type;
        return this;
    }

    public AAMaterialBuilder armorDurability(int durability) {
        this.armorDurability = durability;
        return this;
    }

    public AAMaterialBuilder toolDurability(int durability) {
        this.toolDurability = durability;
        return this;
    }

    public AAMaterialBuilder swordDamage(float swordDamage) {
        this.swordDamage = swordDamage;
        return this;
    }

    public AAMaterialBuilder swordSwingSpeed(float swordSwingSpeed) {
        this.swordSwingSpeed = swordSwingSpeed;
        return this;
    }

    public AAMaterialBuilder axeDamage(float axeDamage) {
        this.axeDamage = axeDamage;
        return this;
    }

    public AAMaterialBuilder axeSwingSpeed(float axeSwingSpeed) {
        this.axeSwingSpeed = axeSwingSpeed;
        return this;
    }

    public AAMaterialBuilder bonusDamage(float bonusDamage) {
        this.bonusDamage = bonusDamage;
        return this;
    }

    public AAMaterialBuilder miningLevel(int miningLevel) {
        this.miningLevel = miningLevel;
        return this;
    }

    public AAMaterialBuilder miningSpeed(float miningSpeed) {
        this.miningSpeed = miningSpeed;
        return this;
    }

    public AAMaterialBuilder enchantability(int enchantability) {
        this.enchantability = enchantability;
        return this;
    }

    public AAMaterialBuilder protection(int[] protection) {
        this.protection = protection;
        return this;
    }

    public AAMaterialBuilder toughness(float toughness) {
        this.toughness = toughness;
        return this;
    }

    public AAMaterialBuilder knockbackResistance(float knockbackResistance) {
        this.knockbackResistance = knockbackResistance;
        return this;
    }

    public AAMaterialBuilder equipSound(SoundEvent equipSound) {
        this.equipSound = equipSound;
        return this;
    }

    public AAMaterialBuilder miningLevelRequired(int miningLevelRequired) {
        this.miningLevelRequired = miningLevelRequired;
        return this;
    }

    public AAMaterialBuilder veinsPerChunk(int veinsPerChunk) {
        this.veinsPerChunk = veinsPerChunk;
        return this;
    }

    public AAMaterialBuilder veinSize(int veinSize) {
        this.veinSize = veinSize;
        return this;
    }

    public AAMaterialBuilder minYOffset(int minYOffset) {
        this.minYOffset = minYOffset;
        return this;
    }

    public AAMaterialBuilder maxYOffset(int maxYOffset) {
        this.maxYOffset = maxYOffset;
        return this;
    }

    public AAMaterialBuilder minDrops(float minDrops) {
        this.minDrops = minDrops;
        return this;
    }

    public AAMaterialBuilder maxDrops(float maxDrops) {
        this.maxDrops = maxDrops;
        return this;
    }

    public AAMaterialBuilder spawnInOverworld(boolean spawnInOverworld) {
        this.spawnInOverworld = spawnInOverworld;
        return this;
    }

    public AAMaterialBuilder spawnInTheNether(boolean spawnInTheNether) {
        this.spawnInTheNether = spawnInTheNether;
        return this;
    }

    public AAMaterialBuilder spawnInTheEnd(boolean spawnInTheEnd) {
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
            throw new IllegalStateException(
                    "Only one of spawnInOverworld, spawnInTheNether, spawnInTheEnd can be set to true");
        }
    }

    public AAMaterialBuilder createOre(boolean createOre) {
        this.createOre = createOre;
        return this;
    }

    public AAMaterialBuilder createWeapons(boolean createWeapons) {
        this.createWeapons = createWeapons;
        return this;
    }

    public AAMaterialBuilder createTools(boolean createTools) {
        this.createTools = createTools;
        return this;
    }

    public AAMaterialBuilder createArmor(boolean createArmor) {
        this.createArmor = createArmor;
        return this;
    }

    public AAMaterial build() {
        return new AAMaterial(new AAMaterialConfiguration(name,
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
}
