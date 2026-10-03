package com.iamkaf.arcanearmory.content;

import com.iamkaf.amber.api.registry.v1.RegistrySupplier;
import com.iamkaf.arcanearmory.ArcaneArmoryConstants;
import net.minecraft.network.chat.Component;
//? if <1.19
/*import net.minecraft.network.chat.TranslatableComponent;*/
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
//? if >=1.20 {
import com.iamkaf.amber.api.registry.v1.DeferredRegister;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
//?}

import java.util.ArrayList;
import java.util.List;

/**
 * The Arcane Armory creative tab. From 1.20 the tab is a registry entry whose contents each loader adds
 * through its own event, since 26.1 hides the vanilla output type. Older lines build the whole tab in
 * each loader entrypoint from {@link #title()}, {@link #icon()}, and {@link #items()}.
 */
public final class ArcaneArmoryCreativeTab {
    public static final String ID = ArcaneArmoryConstants.MOD_ID;
    public static final String TITLE_KEY = "itemGroup." + ArcaneArmoryConstants.MOD_ID;

    //? if >=1.20 {
    public static final ResourceKey<CreativeModeTab> KEY =
            ResourceKey.create(Registries.CREATIVE_MODE_TAB, ArcaneArmoryConstants.resource(ID));

    private static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(ArcaneArmoryConstants.MOD_ID, Registries.CREATIVE_MODE_TAB);

    static {
        TABS.register(ID, key -> CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
                .title(title())
                .icon(ArcaneArmoryCreativeTab::icon)
                .build());
    }
    //?}

    private ArcaneArmoryCreativeTab() {
    }

    public static void init() {
        //? if >=1.20
        TABS.register();
    }

    public static Component title() {
        //? if >=1.19 {
        return Component.translatable(TITLE_KEY);
        //?} else {
        /*return new TranslatableComponent(TITLE_KEY);
        *///?}
    }

    public static ItemStack icon() {
        return new ItemStack(item("deepslate_aetheric_crystal_ore"));
    }

    /** Each material's family in order, then the items that belong to no material. */
    public static List<ItemStack> items() {
        List<ItemStack> stacks = new ArrayList<>();
        for (ArcaneArmoryContent.RegisteredMaterial registered : ArcaneArmoryContent.registeredMaterials()) {
            ArcaneMaterial material = registered.material();
            stacks.add(new ItemStack(item(material.materialItemId())));
            for (ArcaneArmoryContent.RegisteredBlock block : registered.blocks()) {
                stacks.add(new ItemStack(item(block.id())));
            }
            for (ArcaneArmoryContent.RegisteredItem entry : registered.items()) {
                if (!entry.id().equals(material.materialItemId())) {
                    stacks.add(new ItemStack(entry.item().get()));
                }
            }
        }
        stacks.add(new ItemStack(item("amber_ingot")));
        stacks.add(new ItemStack(item("arcanthe")));
        stacks.add(new ItemStack(item("doomflare_block")));
        //? if >=1.20
        stacks.add(new ItemStack(item(VoidiumUpgrade.TEMPLATE_ID)));
        return stacks;
    }

    private static Item item(String id) {
        return ArcaneArmoryContent.item(id)
                .map(RegistrySupplier::get)
                .orElseThrow(() -> new IllegalStateException("Missing Arcane Armory item " + id));
    }
}
