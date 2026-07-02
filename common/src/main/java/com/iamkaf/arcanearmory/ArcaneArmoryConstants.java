package com.iamkaf.arcanearmory;

//? if >=1.21.11 {
import net.minecraft.resources.Identifier;
//?} else {
import net.minecraft.resources.ResourceLocation;
//?}
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ArcaneArmoryConstants {
    public static final String MOD_ID = "arcanearmory";
    public static final String MOD_NAME = "Arcane Armory";
    public static final Logger LOG = LoggerFactory.getLogger(MOD_NAME);

    private ArcaneArmoryConstants() {
    }

    //? if >=1.21.11 {
    public static Identifier resource(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    //?} else if >=1.21 {
    public static ResourceLocation resource(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    //?} else {
    public static ResourceLocation resource(String path) {
        return new ResourceLocation(MOD_ID, path);
    //?}
    }
}
