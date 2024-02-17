package com.iamkaf.arcanearmory.sound;

import com.iamkaf.arcanearmory.ArcaneArmory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class ModSounds {
    public static final SoundEvent UWU = registerSoundEvent("uwu");

    private static SoundEvent registerSoundEvent(String name) {
        Identifier id = new Identifier(ArcaneArmory.MOD_ID, name);
        return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
    }

    public static void registerSounds() {
        ArcaneArmory.LOGGER.info("Registering sounds for " + ArcaneArmory.MOD_ID);
    }
}
