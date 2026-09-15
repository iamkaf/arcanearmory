package com.iamkaf.arcanearmory;

//? if <26
import com.iamkaf.arcanearmory.content.ArcaneArmoryTradeOffers;
import com.iamkaf.arcanearmory.content.ArcaneArmoryFuels;
//? if >=1.21.6 {
//? if <1.21.11
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.furnace.FurnaceFuelBurnTimeEvent;
//?} else {
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.furnace.FurnaceFuelBurnTimeEvent;
//?}
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
        registerFuelEvents();
        registerTradeEvents();
    }
    //?} else {
    public ArcaneArmoryForge() {
        var modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ArcaneArmoryMod.init(modEventBus);
        //? if <1.21.2
        modEventBus.addListener(ArcaneArmoryForgeClient::clientSetup);
        registerFuelEvents();
        registerTradeEvents();
    }
    //?}

    private static void registerFuelEvents() {
        //? if >=1.21.6 {
        FurnaceFuelBurnTimeEvent.BUS.addListener(ArcaneArmoryForge::fuelBurnTime);
        //?} else {
        MinecraftForge.EVENT_BUS.addListener(ArcaneArmoryForge::fuelBurnTime);
        //?}
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
