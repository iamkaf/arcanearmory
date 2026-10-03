package com.iamkaf.arcanearmory;

//? if <1.19 {
/*import com.iamkaf.arcanearmory.content.ArcaneOreBiomes;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BiomeLoadingEvent;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/^*
 * Forge 1.18.2 has no biome modifiers, so the bundled feature JSON is decoded here and added to biomes
 * as they load. Each placed feature inlines its configured feature, so no registry lookup is needed.
 ^/
final class ArcaneArmoryLegacyForgeWorldgen {
    private static final List<String> OVERWORLD_ORES = List.of(
            "aetheric_crystal_ore_placed",
            "black_diamond_ore_placed",
            "chrysoberyl_ore_placed",
            "coolpper_ore_placed",
            "ruby_ore_placed",
            "sapphire_ore_placed",
            "titanium_ore_placed",
            "topaz_ore_placed"
    );
    private static final List<String> NETHER_ORES = List.of(
            "nether_bloodfire_garnet_ore_placed",
            "nether_doom_fragment_ore_placed",
            "nether_shadow_crystal_ore_placed"
    );
    private static final List<String> END_ORES = List.of("end_void_obsidian_fragment_ore_placed");
    private static final String AMBER_GEODE = "amber_geode_placed";

    // Biome filters compare placed features by instance, so every biome must share one instance.
    private static final Map<String, Holder<PlacedFeature>> PLACED = new HashMap<>();

    private ArcaneArmoryLegacyForgeWorldgen() {
    }

    static void register() {
        MinecraftForge.EVENT_BUS.addListener(ArcaneArmoryLegacyForgeWorldgen::addFeatures);
    }

    private static void addFeatures(BiomeLoadingEvent event) {
        Biome.BiomeCategory category = event.getCategory();
        if (category == Biome.BiomeCategory.NETHER) {
            add(event, GenerationStep.Decoration.UNDERGROUND_ORES, NETHER_ORES);
        } else if (category == Biome.BiomeCategory.THEEND) {
            add(event, GenerationStep.Decoration.UNDERGROUND_ORES, END_ORES);
        } else if (category != Biome.BiomeCategory.NONE) {
            add(event, GenerationStep.Decoration.UNDERGROUND_ORES, OVERWORLD_ORES);
            String biome = String.valueOf(event.getName());
            ArcaneOreBiomes.BY_ORE.forEach((ore, biomes) -> {
                if (biomes.contains(biome)) {
                    add(event, GenerationStep.Decoration.UNDERGROUND_ORES, List.of(ArcaneOreBiomes.placedFeature(ore)));
                }
            });
            add(event, GenerationStep.Decoration.LOCAL_MODIFICATIONS, List.of(AMBER_GEODE));
        }
    }

    private static void add(BiomeLoadingEvent event, GenerationStep.Decoration step, List<String> features) {
        for (String feature : features) {
            event.getGeneration().addFeature(step, placed(feature));
        }
    }

    private static synchronized Holder<PlacedFeature> placed(String id) {
        return PLACED.computeIfAbsent(id, ArcaneArmoryLegacyForgeWorldgen::decode);
    }

    private static Holder<PlacedFeature> decode(String id) {
        JsonObject placed = read("placed_feature/" + id).getAsJsonObject();
        String feature = placed.get("feature").getAsString();
        placed.add("feature", read("configured_feature/" + feature.substring(feature.indexOf(':') + 1)));
        PlacedFeature value = PlacedFeature.DIRECT_CODEC.parse(JsonOps.INSTANCE, placed)
                .getOrThrow(false, error -> {
                    throw new IllegalStateException("Invalid Arcane Armory feature " + id + ": " + error);
                });
        return Holder.direct(value);
    }

    private static JsonElement read(String path) {
        String resource = "/data/" + ArcaneArmoryConstants.MOD_ID + "/worldgen/" + path + ".json";
        try (InputStream stream = ArcaneArmoryLegacyForgeWorldgen.class.getResourceAsStream(resource)) {
            if (stream == null) {
                throw new IllegalStateException("Missing Arcane Armory worldgen resource " + resource);
            }
            try (Reader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
                return JsonParser.parseReader(reader);
            }
        } catch (IOException exception) {
            throw new IllegalStateException("Could not read " + resource, exception);
        }
    }
}
*///?}
