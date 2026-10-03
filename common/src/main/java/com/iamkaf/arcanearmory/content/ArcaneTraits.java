package com.iamkaf.arcanearmory.content;

import com.iamkaf.amber.api.event.v1.events.common.EntityEvent;
import com.iamkaf.arcanearmory.ArcaneArmoryConstants;
import net.minecraft.ChatFormatting;
//? if >=1.20.5
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
//? if <1.19
/*import net.minecraft.network.chat.TranslatableComponent;*/
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
//? if >=1.19.4
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.BiConsumer;

/** One trait per equipment material: an on-hit effect, a full-set bonus, or an armor stat. */
public final class ArcaneTraits {
    private static final int SET_BONUS_INTERVAL = 20;
    /** Outlasts the refresh interval, and runs out soon after the set comes off. */
    private static final int SET_BONUS_TICKS = 40;
    /** Night Vision flickers in its last 200 ticks, so the refresh keeps it above that. */
    private static final int NIGHT_VISION_TICKS = 240;

    // 1.21.5 renamed these effects.
    //? if >=1.21.5 {
    private static final Holder<MobEffect> SLOWNESS = MobEffects.SLOWNESS;
    private static final Holder<MobEffect> HASTE = MobEffects.HASTE;
    private static final Holder<MobEffect> RESISTANCE = MobEffects.RESISTANCE;
    private static final Holder<MobEffect> JUMP_BOOST = MobEffects.JUMP_BOOST;
    private static final Holder<MobEffect> SPEED = MobEffects.SPEED;
    //?} else if >=1.20.5 {
    /*private static final Holder<MobEffect> SLOWNESS = MobEffects.MOVEMENT_SLOWDOWN;
    private static final Holder<MobEffect> HASTE = MobEffects.DIG_SPEED;
    private static final Holder<MobEffect> RESISTANCE = MobEffects.DAMAGE_RESISTANCE;
    private static final Holder<MobEffect> JUMP_BOOST = MobEffects.JUMP;
    private static final Holder<MobEffect> SPEED = MobEffects.MOVEMENT_SPEED;
    *///?} else {
    /*private static final MobEffect SLOWNESS = MobEffects.MOVEMENT_SLOWDOWN;
    private static final MobEffect HASTE = MobEffects.DIG_SPEED;
    private static final MobEffect RESISTANCE = MobEffects.DAMAGE_RESISTANCE;
    private static final MobEffect JUMP_BOOST = MobEffects.JUMP;
    private static final MobEffect SPEED = MobEffects.MOVEMENT_SPEED;
    *///?}

    /** Keyed by material id. The description is the English tooltip line. */
    private static final Map<String, Trait> TRAITS = Map.ofEntries(
            Map.entry("ruby", new OnHit("On hit: Sets the target on fire",
                    (player, target) -> setOnFire(target, 3))),
            Map.entry("amber", new OnHit("On hit: Slows the target",
                    (player, target) -> target.addEffect(new MobEffectInstance(SLOWNESS, 2 * 20, 1)))),
            Map.entry("bloodfire_garnet", new OnHit("On hit: Heals you",
                    (player, target) -> player.heal(1.0F))),
            Map.entry("star_corundum", new OnHit("On hit: Makes the target glow",
                    (player, target) -> target.addEffect(new MobEffectInstance(MobEffects.GLOWING, 5 * 20)))),
            Map.entry("voidium", new OnHit("On hit: Makes the target levitate",
                    (player, target) -> target.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 20)))),
            Map.entry("sapphire", new SetBonus("Set bonus: Haste", HASTE, 0, SET_BONUS_TICKS)),
            Map.entry("black_diamond", new SetBonus("Set bonus: Resistance", RESISTANCE, 0, SET_BONUS_TICKS)),
            Map.entry("topaz", new SetBonus("Set bonus: Luck", MobEffects.LUCK, 0, SET_BONUS_TICKS)),
            Map.entry("chrysoberyl", new SetBonus("Set bonus: Night Vision", MobEffects.NIGHT_VISION, 0, NIGHT_VISION_TICKS)),
            Map.entry("aquamarine", new SetBonus("Set bonus: Water Breathing", MobEffects.WATER_BREATHING, 0, SET_BONUS_TICKS)),
            Map.entry("aetheric_crystal", new SetBonus("Set bonus: Jump Boost", JUMP_BOOST, 0, SET_BONUS_TICKS)),
            Map.entry("coolpper", new SetBonus("Set bonus: Fire Resistance", MobEffects.FIRE_RESISTANCE, 0, SET_BONUS_TICKS)),
            Map.entry("arcanthium", new SetBonus("Set bonus: Speed", SPEED, 0, SET_BONUS_TICKS)),
            // The knockback resistance itself is an armor stat in ArcaneMaterials.
            Map.entry("titanium", new ArmorStat("Armor: Resists knockback"))
    );

    private ArcaneTraits() {
    }

    public static void init() {
        EntityEvent.AFTER_DAMAGE.register((target, source, baseDamageTaken, damageTaken, blocked) -> onDamage(target, source));
    }

    /** Called by each loader at the end of every server tick. */
    public static void refreshSetBonuses(MinecraftServer server) {
        if (server.getTickCount() % SET_BONUS_INTERVAL != 0) {
            return;
        }
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            SetBonus bonus = fullSetBonus(player);
            if (bonus != null && player.isAlive()) {
                // Ambient, like a beacon: no particles, and the icon does not blink while the set keeps it topped up.
                player.addEffect(new MobEffectInstance(bonus.effect(), bonus.ticks(), bonus.amplifier(), true, false, true));
            }
        }
    }

    /** Adds the trait line under the item name. Called by each loader's tooltip hook. */
    public static void appendTooltip(ItemStack stack, List<Component> lines) {
        String key = TraitItems.TOOLTIP_KEYS.get(stack.getItem());
        if (key != null) {
            lines.add(Math.min(1, lines.size()), translatable(key).withStyle(ChatFormatting.BLUE));
        }
    }

    /** Tooltip translation keys and their English text, for the language provider. */
    public static Map<String, String> englishTooltips() {
        Map<String, String> translations = new TreeMap<>();
        TRAITS.forEach((materialId, trait) -> translations.put(tooltipKey(materialId), trait.description()));
        return translations;
    }

    private static void onDamage(LivingEntity target, DamageSource source) {
        // Older Amber lines do not report the killing blow, so no line applies a trait on it.
        if (target.isDeadOrDying() || !isMeleeAttack(source) || !(source.getEntity() instanceof Player player)) {
            return;
        }
        OnHit trait = TraitItems.WEAPONS.get(player.getMainHandItem().getItem());
        if (trait != null) {
            trait.apply().accept(player, target);
        }
    }

    private static @Nullable SetBonus fullSetBonus(Player player) {
        SetBonus bonus = TraitItems.ARMOR.get(player.getItemBySlot(EquipmentSlot.HEAD).getItem());
        for (EquipmentSlot slot : List.of(EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET)) {
            if (TraitItems.ARMOR.get(player.getItemBySlot(slot).getItem()) != bonus) {
                return null;
            }
        }
        return bonus;
    }

    private static boolean isMeleeAttack(DamageSource source) {
        //? if >=1.19.4 {
        return source.is(DamageTypes.PLAYER_ATTACK);
        //?} else {
        /*return "player".equals(source.getMsgId());
        *///?}
    }

    private static void setOnFire(LivingEntity target, int seconds) {
        //? if >=1.20.5 {
        target.igniteForSeconds(seconds);
        //?} else {
        /*target.setSecondsOnFire(seconds);
        *///?}
    }

    private static MutableComponent translatable(String key) {
        //? if >=1.19 {
        return Component.translatable(key);
        //?} else {
        /*return new TranslatableComponent(key);
        *///?}
    }

    private static String tooltipKey(String materialId) {
        return "tooltip." + ArcaneArmoryConstants.MOD_ID + ".trait." + materialId;
    }

    private interface Trait {
        String description();
    }

    private record OnHit(String description, BiConsumer<Player, LivingEntity> apply) implements Trait {
    }

    //? if >=1.20.5 {
    private record SetBonus(String description, Holder<MobEffect> effect, int amplifier, int ticks) implements Trait {
    }
    //?} else {
    /*private record SetBonus(String description, MobEffect effect, int amplifier, int ticks) implements Trait {
    }
    *///?}

    private record ArmorStat(String description) implements Trait {
    }

    /** Looks items up by trait. Built on first use, after the items are registered. */
    private static final class TraitItems {
        private static final Map<Item, OnHit> WEAPONS = new IdentityHashMap<>();
        private static final Map<Item, SetBonus> ARMOR = new IdentityHashMap<>();
        private static final Map<Item, String> TOOLTIP_KEYS = new IdentityHashMap<>();

        static {
            for (ArcaneArmoryContent.RegisteredMaterial registered : ArcaneArmoryContent.registeredMaterials()) {
                String materialId = registered.material().id();
                Trait trait = TRAITS.get(materialId);
                if (trait == null) {
                    continue;
                }
                for (ArcaneArmoryContent.RegisteredItem registeredItem : registered.items()) {
                    Item item = registeredItem.item().get();
                    TOOLTIP_KEYS.put(item, tooltipKey(materialId));
                    if (trait instanceof OnHit onHit && isWeapon(registeredItem.id())) {
                        WEAPONS.put(item, onHit);
                    }
                    if (trait instanceof SetBonus bonus && isArmor(registeredItem.id())) {
                        ARMOR.put(item, bonus);
                    }
                }
            }
        }

        // Bows and shields do not carry on-hit traits.
        private static boolean isWeapon(String id) {
            return id.endsWith("_sword") || id.endsWith("_axe") || id.endsWith("_hammer")
                    || id.endsWith("_pickaxe") || id.endsWith("_shovel") || id.endsWith("_hoe");
        }

        private static boolean isArmor(String id) {
            return id.endsWith("_helmet") || id.endsWith("_chestplate") || id.endsWith("_leggings") || id.endsWith("_boots");
        }
    }
}
