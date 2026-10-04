package com.iamkaf.arcanearmory.content;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
//? if <1.19
/*import net.minecraft.network.chat.TranslatableComponent;*/
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
//? if >=1.21.5 {
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.function.Consumer;
//?} else {
/*import java.util.List;
*///?}
//? if <1.20.5 {
/*import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
*///?}
import net.minecraft.world.level.block.Block;

public class DoomflareBlockItem extends BlockItem {
    public static final String TOOLTIP_KEY = "block.arcanearmory.doomflare_block.tooltip";

    public DoomflareBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    //? if >=1.21.5 {
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display,
            Consumer<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, display, tooltip, flag);
        tooltip.accept(warning());
    }
    //?} else if >=1.20.5 {
    /*@Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        tooltip.add(warning());
    }
    *///?} else {
    /*@Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(warning());
    }
    *///?}

    private static Component warning() {
        //? if >=1.19 {
        return Component.translatable(TOOLTIP_KEY).withStyle(ChatFormatting.GOLD);
        //?} else {
        /*return new TranslatableComponent(TOOLTIP_KEY).withStyle(ChatFormatting.GOLD);
        *///?}
    }
}
