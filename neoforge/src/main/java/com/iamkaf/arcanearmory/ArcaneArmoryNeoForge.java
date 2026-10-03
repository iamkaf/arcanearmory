package com.iamkaf.arcanearmory;

//? if <26
import com.iamkaf.arcanearmory.content.ArcaneArmoryTradeOffers;
import com.iamkaf.arcanearmory.content.ArcaneArmoryCreativeTab;
import com.iamkaf.arcanearmory.content.ArcaneArmoryFuels;
import com.iamkaf.arcanearmory.content.ArcaneTraits;
import com.iamkaf.amber.api.event.v1.events.common.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
//? if >=1.21.11 && <26 {
import net.minecraft.world.entity.npc.villager.VillagerProfession;
//?} else if <26 {
import net.minecraft.world.entity.npc.VillagerProfession;
//?}
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.event.furnace.FurnaceFuelBurnTimeEvent;
//? if <26
import net.neoforged.neoforge.event.village.VillagerTradesEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

@Mod(ArcaneArmoryConstants.MOD_ID)
public class ArcaneArmoryNeoForge {
    public ArcaneArmoryNeoForge(IEventBus eventBus) {
        //? if >=1.21.7 {
        ArcaneArmoryMod.init();
        //?} else {
        ArcaneArmoryMod.init(eventBus);
        //?}
        //? if <1.21.2
        eventBus.addListener(ArcaneArmoryNeoForgeClient::clientSetup);
        eventBus.addListener(ArcaneArmoryNeoForge::buildCreativeTab);
        NeoForge.EVENT_BUS.addListener(ArcaneArmoryNeoForge::fuelBurnTime);
        NeoForge.EVENT_BUS.addListener((ItemTooltipEvent event) -> ArcaneTraits.appendTooltip(event.getItemStack(), event.getToolTip()));
        ServerTickEvents.END_SERVER_TICK.register(() -> {
            MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
            if (server != null) {
                ArcaneTraits.refreshSetBonuses(server);
            }
        });
        //? if <26
        NeoForge.EVENT_BUS.addListener(ArcaneArmoryNeoForge::villagerTrades);
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

    private static void buildCreativeTab(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey().equals(ArcaneArmoryCreativeTab.KEY)) {
            ArcaneArmoryCreativeTab.items().forEach(event::accept);
        }
    }

    private static void fuelBurnTime(FurnaceFuelBurnTimeEvent event) {
        int burnTime = ArcaneArmoryFuels.burnTime(event.getItemStack());
        if (burnTime > 0) {
            event.setBurnTime(burnTime);
        }
    }
}
