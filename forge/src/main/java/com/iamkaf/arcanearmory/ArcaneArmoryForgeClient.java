package com.iamkaf.arcanearmory;

//? if <1.21.2 {
import com.iamkaf.arcanearmory.content.ArcaneArmoryContent;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
//?}

public final class ArcaneArmoryForgeClient {
    private ArcaneArmoryForgeClient() {
    }

    //? if <1.21.2 {
    public static void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(ArcaneArmoryForgeClient::registerLegacyItemPredicates);
    }

    private static void registerLegacyItemPredicates() {
        for (ArcaneArmoryContent.RegisteredMaterial registered : ArcaneArmoryContent.registeredMaterials()) {
            for (ArcaneArmoryContent.RegisteredItem registeredItem : registered.items()) {
                String id = registeredItem.id();
                Item item = registeredItem.item().get();
                if (id.endsWith("_bow")) {
                    ItemProperties.register(item, property("pull"), (stack, level, entity, seed) -> {
                        if (entity == null || entity.getUseItem() != stack) {
                            return 0.0F;
                        }
                        //? if >=1.21 {
                        return (float) (stack.getUseDuration(entity) - entity.getUseItemRemainingTicks()) / 20.0F;
                        //?} else {
                        /*return (float) (stack.getUseDuration() - entity.getUseItemRemainingTicks()) / 20.0F;*/
                        //?}
                    });
                    ItemProperties.register(item, property("pulling"), (stack, level, entity, seed) ->
                            entity != null && entity.isUsingItem() && entity.getUseItem() == stack ? 1.0F : 0.0F);
                } else if (id.endsWith("_shield")) {
                    ItemProperties.register(item, property("blocking"), (stack, level, entity, seed) ->
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
