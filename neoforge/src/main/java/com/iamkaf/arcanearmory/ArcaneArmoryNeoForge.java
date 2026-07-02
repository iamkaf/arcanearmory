package com.iamkaf.arcanearmory;

//? if <26
import com.iamkaf.arcanearmory.content.ArcaneArmoryTradeOffers;
import com.iamkaf.arcanearmory.content.ArcaneArmoryFuels;
//? if >=1.21.11 && <26 {
import net.minecraft.world.entity.npc.villager.VillagerProfession;
//?} else if <26 {
import net.minecraft.world.entity.npc.VillagerProfession;
//?}
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.furnace.FurnaceFuelBurnTimeEvent;
//? if <26
import net.neoforged.neoforge.event.village.VillagerTradesEvent;
import net.neoforged.fml.common.Mod;

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
        NeoForge.EVENT_BUS.addListener(ArcaneArmoryNeoForge::fuelBurnTime);
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

    private static void fuelBurnTime(FurnaceFuelBurnTimeEvent event) {
        int burnTime = ArcaneArmoryFuels.burnTime(event.getItemStack());
        if (burnTime > 0) {
            event.setBurnTime(burnTime);
        }
    }
}
