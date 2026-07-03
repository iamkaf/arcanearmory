package com.iamkaf.arcanearmory.fabric.datagen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import net.minecraft.data.DataProvider;
//? if >=1.19.3 {
import net.minecraft.data.CachedOutput;
//? if >=26.1 {
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
//?} else {
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
//?}
//?} else {
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.data.HashCache;
//?}

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public final class ArcaneArmoryDataProvider implements DataProvider {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final Path output;
    private final Path root;
    private final String minecraftVersion;

    //? if >=26.1 {
    public ArcaneArmoryDataProvider(FabricPackOutput output) {
        this.output = output.getOutputFolder();
        this.root = findRepositoryRoot(this.output);
        this.minecraftVersion = findMinecraftVersion(this.output);
    }
    //?} else if >=1.19.3 {
    public ArcaneArmoryDataProvider(FabricDataOutput output) {
        this.output = output.getOutputFolder();
        this.root = findRepositoryRoot(this.output);
        this.minecraftVersion = findMinecraftVersion(this.output);
    }
    //?} else {
    public ArcaneArmoryDataProvider(FabricDataGenerator dataGenerator) {
        this.output = dataGenerator.getOutputFolder();
        this.root = findRepositoryRoot(this.output);
        this.minecraftVersion = findMinecraftVersion(this.output);
    }
    //?}

    //? if >=1.19.3 {
    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        List<CompletableFuture<?>> futures = new ArrayList<>();
        try {
            copyTemplateTree(futures, cache, root.resolve("datagen/common/data"), output.resolve("data"), true);
            copyLoaderTemplateTrees(futures, cache);
        } catch (IOException exception) {
            CompletableFuture<?> failed = new CompletableFuture<>();
            failed.completeExceptionally(exception);
            return failed;
        }
        return CompletableFuture.allOf(futures.toArray(CompletableFuture<?>[]::new));
    }
    //?} else {
    @Override
    public void run(HashCache cache) throws IOException {
        copyTemplateTree(cache, root.resolve("datagen/common/data"), output.resolve("data"), true);
        copyLoaderTemplateTrees(cache);
    }
    //?}

    @Override
    public String getName() {
        return "Arcane Armory generated data";
    }

    //? if >=1.19.3 {
    private void copyLoaderTemplateTrees(List<CompletableFuture<?>> futures, CachedOutput cache) throws IOException {
        for (String loader : List.of("fabric", "forge", "neoforge")) {
            if (!isLoaderEnabled(loader)) {
                continue;
            }
            Path source = root.resolve("datagen/loader").resolve(loader).resolve("data");
            Path target = root.resolve("versions").resolve(minecraftVersion).resolve(loader).resolve("src/main/generated/data");
            copyTemplateTree(futures, cache, source, target, false);
        }
    }

    private void copyTemplateTree(List<CompletableFuture<?>> futures, CachedOutput cache, Path sourceRoot, Path targetRoot,
            boolean commonData) throws IOException {
        if (!Files.isDirectory(sourceRoot)) {
            return;
        }
        try (Stream<Path> paths = Files.walk(sourceRoot)) {
            for (Path source : paths.filter(Files::isRegularFile).toList()) {
                String relative = normalize(sourceRoot.relativize(source), commonData);
                if (relative == null) {
                    continue;
                }
                saveJson(futures, cache, source, targetRoot.resolve(relative));
            }
        }
    }

    private static void saveJson(List<CompletableFuture<?>> futures, CachedOutput cache, Path source, Path target) throws IOException {
        try (Reader reader = Files.newBufferedReader(source)) {
            JsonElement json = GSON.fromJson(reader, JsonElement.class);
            futures.add(DataProvider.saveStable(cache, json, target));
        }
    }
    //?} else {
    private void copyLoaderTemplateTrees(HashCache cache) throws IOException {
        for (String loader : List.of("fabric", "forge", "neoforge")) {
            if (!isLoaderEnabled(loader)) {
                continue;
            }
            Path source = root.resolve("datagen/loader").resolve(loader).resolve("data");
            Path target = root.resolve("versions").resolve(minecraftVersion).resolve(loader).resolve("src/main/generated/data");
            copyTemplateTree(cache, source, target, false);
        }
    }

    private void copyTemplateTree(HashCache cache, Path sourceRoot, Path targetRoot, boolean commonData) throws IOException {
        if (!Files.isDirectory(sourceRoot)) {
            return;
        }
        try (Stream<Path> paths = Files.walk(sourceRoot)) {
            for (Path source : paths.filter(Files::isRegularFile).toList()) {
                String relative = normalize(sourceRoot.relativize(source), commonData);
                if (relative == null) {
                    continue;
                }
                saveJson(cache, source, targetRoot.resolve(relative));
            }
        }
    }

    private static void saveJson(HashCache cache, Path source, Path target) throws IOException {
        try (Reader reader = Files.newBufferedReader(source)) {
            JsonElement json = GSON.fromJson(reader, JsonElement.class);
            DataProvider.save(GSON, cache, json, target);
        }
    }
    //?}

    private static String normalize(Path relativePath, boolean commonData) {
        String relative = relativePath.toString().replace('\\', '/');
        if (!commonData) {
            return relative;
        }
        if (relative.startsWith("arcanearmory/recipes/")) {
            return null;
        }
        //? if <26 {
        if (relative.startsWith("arcanearmory/villager_trade/") || relative.startsWith("minecraft/tags/villager_trade/")) {
            return null;
        }
        //?}
        //? if <1.20 {
        if (relative.equals("minecraft/tags/items/trimmable_armor.json")) {
            return null;
        }
        //?}
        //? if >=1.21 {
        relative = replacePrefix(relative, "arcanearmory/advancements/", "arcanearmory/advancement/");
        relative = replacePrefix(relative, "arcanearmory/loot_tables/", "arcanearmory/loot_table/");
        //?}
        return relative;
    }

    private static String replacePrefix(String value, String oldPrefix, String newPrefix) {
        if (value.startsWith(oldPrefix)) {
            return newPrefix + value.substring(oldPrefix.length());
        }
        return value;
    }

    private boolean isLoaderEnabled(String loader) throws IOException {
        Path propertiesPath = root.resolve("versions").resolve(minecraftVersion).resolve("gradle.properties");
        Properties properties = new Properties();
        try (Reader reader = Files.newBufferedReader(propertiesPath)) {
            properties.load(reader);
        }
        String enabledLoaders = properties.getProperty("project.enabled-loaders", "");
        for (String enabled : enabledLoaders.split(",")) {
            if (enabled.trim().equals(loader)) {
                return true;
            }
        }
        return false;
    }

    private static Path findRepositoryRoot(Path start) {
        Path current = start.toAbsolutePath();
        while (current != null) {
            if (Files.isRegularFile(current.resolve("settings.gradle.kts")) && Files.isDirectory(current.resolve("versions"))) {
                return current;
            }
            current = current.getParent();
        }
        throw new IllegalStateException("Could not find Arcane Armory repository root from " + start);
    }

    private static String findMinecraftVersion(Path output) {
        Path absolute = output.toAbsolutePath();
        for (int index = 0; index < absolute.getNameCount() - 1; index++) {
            if ("versions".equals(absolute.getName(index).toString())) {
                return absolute.getName(index + 1).toString().toLowerCase(Locale.ROOT);
            }
        }
        throw new IllegalStateException("Could not find Minecraft version from datagen output path " + output);
    }
}
