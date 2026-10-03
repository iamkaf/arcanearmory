package com.iamkaf.arcanearmory;

//? if <26
import com.iamkaf.arcanearmory.content.ArcaneArmoryTradeOffers;
import com.iamkaf.arcanearmory.content.ArcaneArmoryFuels;
import com.iamkaf.arcanearmory.content.ArcaneArmoryCreativeTab;
import com.iamkaf.arcanearmory.content.ArcaneTraits;
import com.iamkaf.amber.api.event.v1.events.common.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
//? if >=1.20
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
//? if <1.19.3 {
/*import net.minecraft.core.NonNullList;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
*///?} else if <1.20 {
/*import net.minecraftforge.event.CreativeModeTabEvent;
*///?}
//? if >=1.21.6 {
//? if <1.21.11
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.furnace.FurnaceFuelBurnTimeEvent;
//?} else {
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.furnace.FurnaceFuelBurnTimeEvent;
//?}
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
//? if <26 {
//? if >=1.21.11 {
import net.minecraft.world.entity.npc.villager.VillagerProfession;
//?} else {
import net.minecraft.world.entity.npc.VillagerProfession;
//?}
import net.minecraftforge.event.village.VillagerTradesEvent;
//?}
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.server.ServerLifecycleHooks;

@Mod(ArcaneArmoryConstants.MOD_ID)
public class ArcaneArmoryForge {
    //? if >=1.21.1 {
    public ArcaneArmoryForge(FMLJavaModLoadingContext ctx) {
        //? if >=1.21.7 {
        ArcaneArmoryMod.init();
        //?} elif >=1.21.6 {
        ArcaneArmoryMod.init(ctx.getModBusGroup());
        //?} else {
        ArcaneArmoryMod.init(ctx.getModEventBus());
        //?}
        //? if <1.21.2
        ctx.getModEventBus().addListener(ArcaneArmoryForgeClient::clientSetup);
        //? if >=1.21.6 {
        BuildCreativeModeTabContentsEvent.BUS.addListener(ArcaneArmoryForge::buildCreativeTab);
        //?} else {
        /*ctx.getModEventBus().addListener(ArcaneArmoryForge::buildCreativeTab);
        *///?}
        registerFuelEvents();
        registerTradeEvents();
        registerTraitEvents();
    }
    //?} else {
    public ArcaneArmoryForge() {
        var modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ArcaneArmoryMod.init(modEventBus);
        //? if <1.21.2
        modEventBus.addListener(ArcaneArmoryForgeClient::clientSetup);
        //? if >=1.20 {
        modEventBus.addListener(ArcaneArmoryForge::buildCreativeTab);
        //?} else if >=1.19.3 {
        /*modEventBus.addListener(ArcaneArmoryForge::registerCreativeTab);
        *///?} else {
        /*registerCreativeTab();
        *///?}
        //? if <1.19
        /*ArcaneArmoryLegacyForgeWorldgen.register();*/
        registerFuelEvents();
        registerTradeEvents();
        registerTraitEvents();
    }
    //?}

    //? if >=1.20 {
    private static void buildCreativeTab(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey().equals(ArcaneArmoryCreativeTab.KEY)) {
            ArcaneArmoryCreativeTab.items().forEach(stack -> event.accept(stack::getItem));
        }
    }
    //?}

    //? if <1.19.3 {
    /*private static void registerCreativeTab() {
        new CreativeModeTab(ArcaneArmoryCreativeTab.ID) {
            @Override
            public ItemStack makeIcon() {
                return ArcaneArmoryCreativeTab.icon();
            }

            @Override
            public void fillItemList(NonNullList<ItemStack> items) {
                items.addAll(ArcaneArmoryCreativeTab.items());
            }
        };
    }
    *///?} else if <1.20 {
    /*private static void registerCreativeTab(CreativeModeTabEvent.Register event) {
        event.registerCreativeModeTab(ArcaneArmoryConstants.resource(ArcaneArmoryCreativeTab.ID), builder -> builder
                .title(ArcaneArmoryCreativeTab.title())
                .icon(ArcaneArmoryCreativeTab::icon)
                .displayItems((parameters, output) -> ArcaneArmoryCreativeTab.items().forEach(output::accept)));
    }
    *///?}

    private static void registerFuelEvents() {
        //? if >=1.21.6 {
        FurnaceFuelBurnTimeEvent.BUS.addListener(ArcaneArmoryForge::fuelBurnTime);
        //?} else {
        MinecraftForge.EVENT_BUS.addListener(ArcaneArmoryForge::fuelBurnTime);
        //?}
    }

    private static void registerTraitEvents() {
        ServerTickEvents.END_SERVER_TICK.register(() -> {
            MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
            if (server != null) {
                ArcaneTraits.refreshSetBonuses(server);
            }
        });
        //? if >=1.21.6 {
        ItemTooltipEvent.BUS.addListener(event -> ArcaneTraits.appendTooltip(event.getItemStack(), event.getToolTip()));
        //?} else {
        /*MinecraftForge.EVENT_BUS.addListener((ItemTooltipEvent event) -> ArcaneTraits.appendTooltip(event.getItemStack(), event.getToolTip()));
        *///?}
    }

    private static void registerTradeEvents() {
        //? if >=1.21.11 && <26 {
        VillagerTradesEvent.BUS.addListener(ArcaneArmoryForge::villagerTrades);
        //?} else if <26 {
        MinecraftForge.EVENT_BUS.addListener(ArcaneArmoryForge::villagerTrades);
        //?}
    }

    //? if <26 {
    private static void villagerTrades(VillagerTradesEvent event) {
        if (event.getType() == VillagerProfession.WEAPONSMITH) {
            //? if >=1.21.11 {
            event.getTrades().get(1).add((level, entity, random) -> ArcaneArmoryTradeOffers.coolpperAxeForEmeralds());
            //?} else {
            event.getTrades().get(1).add((entity, random) -> ArcaneArmoryTradeOffers.coolpperAxeForEmeralds());
            //?}
        }
    }
    //?}

    private static void fuelBurnTime(FurnaceFuelBurnTimeEvent event) {
        int burnTime = ArcaneArmoryFuels.burnTime(event.getItemStack());
        if (burnTime > 0) {
            event.setBurnTime(burnTime);
        }
    }
}
