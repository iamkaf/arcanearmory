package com.iamkaf.arcanearmory;

//? if <1.21.2 {
import com.iamkaf.arcanearmory.content.ArcaneArmoryContent;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
//?} else {
import net.fabricmc.api.ClientModInitializer;
//?}

public class ArcaneArmoryFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        //? if <1.21.2
        registerLegacyItemPredicates();
    }

    //? if <1.21.2 {
    private static void registerLegacyItemPredicates() {
        for (ArcaneArmoryContent.RegisteredMaterial registered : ArcaneArmoryContent.registeredMaterials()) {
            for (ArcaneArmoryContent.RegisteredItem registeredItem : registered.items()) {
                String id = registeredItem.id();
                Item item = registeredItem.item().get();
                if (id.endsWith("_bow")) {
                    FabricModelPredicateProviderRegistry.register(item, property("pull"), (stack, level, entity, seed) -> {
                        if (entity == null || entity.getUseItem() != stack) {
                            return 0.0F;
                        }
                        //? if >=1.21 {
                        return (float) (stack.getUseDuration(entity) - entity.getUseItemRemainingTicks()) / 20.0F;
                        //?} else {
                        /*return (float) (stack.getUseDuration() - entity.getUseItemRemainingTicks()) / 20.0F;*/
                        //?}
                    });
                    FabricModelPredicateProviderRegistry.register(item, property("pulling"), (stack, level, entity, seed) ->
                            entity != null && entity.isUsingItem() && entity.getUseItem() == stack ? 1.0F : 0.0F);
                } else if (id.endsWith("_shield")) {
                    FabricModelPredicateProviderRegistry.register(item, property("blocking"), (stack, level, entity, seed) ->
                            entity != null && entity.isUsingItem() && entity.getUseItem() == stack ? 1.0F : 0.0F);
                }
            }
        }
    }

    private static ResourceLocation property(String path) {
        //? if >=1.21 {
        return ResourceLocation.withDefaultNamespace(path);
        //?} else {
        /*return new ResourceLocation(path);*/
        //?}
    }
    //?}
}
