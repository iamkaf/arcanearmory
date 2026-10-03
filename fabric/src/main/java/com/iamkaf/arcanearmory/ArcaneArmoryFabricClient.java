package com.iamkaf.arcanearmory;

import com.iamkaf.arcanearmory.content.ArcaneArmoryContent;
import net.fabricmc.api.ClientModInitializer;
//? if <1.21.2 {
/*import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
*///?}
//? if >=1.21.6 && <26 {
/*import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
*///?} else if <1.21.6 {
/*import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.renderer.RenderType;
*///?}
//? if <1.19 {
/*import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
*///?}

public class ArcaneArmoryFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        //? if <1.21.2
        /*registerLegacyItemPredicates();*/
        //? if <26
        /*registerCutoutBlocks();*/
        //? if <1.19
        /*registerLegacyArmorRenderer();*/
    }

    // Fabric API reads namespaced armor material names from 1.19 on. Before that, vanilla builds
    // "textures/models/armor/arcanearmory:ruby_layer_1.png" and crashes, so render the armor here.
    //? if <1.19 {
    /*private static HumanoidModel<LivingEntity> innerArmor;
    private static HumanoidModel<LivingEntity> outerArmor;

    private static void registerLegacyArmorRenderer() {
        ArmorRenderer renderer = (matrices, buffers, stack, entity, slot, light, contextModel) -> {
            if (innerArmor == null) {
                innerArmor = new HumanoidModel<>(Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.PLAYER_INNER_ARMOR));
                outerArmor = new HumanoidModel<>(Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR));
            }
            boolean legs = slot == EquipmentSlot.LEGS;
            HumanoidModel<LivingEntity> model = legs ? innerArmor : outerArmor;
            contextModel.copyPropertiesTo(model);
            model.setAllVisible(false);
            model.head.visible = slot == EquipmentSlot.HEAD;
            model.hat.visible = slot == EquipmentSlot.HEAD;
            model.body.visible = slot == EquipmentSlot.CHEST || legs;
            model.rightArm.visible = slot == EquipmentSlot.CHEST;
            model.leftArm.visible = slot == EquipmentSlot.CHEST;
            model.rightLeg.visible = legs || slot == EquipmentSlot.FEET;
            model.leftLeg.visible = legs || slot == EquipmentSlot.FEET;
            String[] name = ((ArmorItem) stack.getItem()).getMaterial().getName().split(":", 2);
            ResourceLocation texture = new ResourceLocation(name[0], "textures/models/armor/" + name[1] + "_layer_" + (legs ? 2 : 1) + ".png");
            ArmorRenderer.renderPart(matrices, buffers, light, stack, model, texture);
        };
        for (ArcaneArmoryContent.RegisteredMaterial registered : ArcaneArmoryContent.registeredMaterials()) {
            for (ArcaneArmoryContent.RegisteredItem registeredItem : registered.items()) {
                if (registeredItem.item().get() instanceof ArmorItem armor) {
                    ArmorRenderer.register(renderer, armor);
                }
            }
        }
    }
    *///?}

    // From 26.1 the game picks cutout rendering from the texture itself.
    //? if >=1.21.6 && <26 {
    /*private static void registerCutoutBlocks() {
        for (String id : ArcaneArmoryContent.CUTOUT_BLOCKS) {
            BlockRenderLayerMap.putBlock(ArcaneArmoryContent.block(id).orElseThrow().get(), ChunkSectionLayer.CUTOUT);
        }
    }
    *///?} else if <1.21.6 {
    /*private static void registerCutoutBlocks() {
        for (String id : ArcaneArmoryContent.CUTOUT_BLOCKS) {
            BlockRenderLayerMap.INSTANCE.putBlock(ArcaneArmoryContent.block(id).orElseThrow().get(), RenderType.cutout());
        }
    }
    *///?}

    //? if <1.21.2 {
    /*private static void registerLegacyItemPredicates() {
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
                        /^return (float) (stack.getUseDuration() - entity.getUseItemRemainingTicks()) / 20.0F;^/
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
        /^return new ResourceLocation(path);^/
        //?}
    }
    *///?}
}
