package org.jlortiz.playercollars.item;

import com.mojang.datafixers.util.Either;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jlortiz.playercollars.PlayerCollarsMod;

import java.util.List;
import java.util.Optional;

public class PawsItem extends FootPawsItem {
    public PawsItem(ResourceKey<Item> key, int color, int pawColor) {
        super(key, color, pawColor);
    }

    public static boolean shouldPreventBlockInteraction(ItemStack stack, @NotNull BlockState block) {
        if (block.is(PlayerCollarsMod.PAWS_ALLOW_INTERACT)) return false;
        List<Either<TagKey<Block>, ResourceKey<Block>>> allowed = stack.get(PlayerCollarsMod.CAN_INTERACT_COMPONENT_TYPE);
        Optional<ResourceKey<Block>> key = block.typeHolder().unwrapKey();
        if (allowed == null || key.isEmpty()) return false;
        for (Either<TagKey<Block>, ResourceKey<Block>> entry : allowed) {
            if (entry.map(block::is, (y) -> y.equals(key.get()))) return false;
        }
        return true;
    }

    public static boolean shouldDrop(ItemStack pawsStack, ItemStack thing) {
        if (thing.isEmpty()) return false;
        List<Either<TagKey<Item>, ResourceKey<Item>>> slippery = pawsStack.get(PlayerCollarsMod.HELD_ITEMS_COMPONENT_TYPE);
        Optional<ResourceKey<Item>> key = thing.typeHolder().unwrapKey();
        if (slippery == null || key.isEmpty()) return false;
        for (Either<TagKey<Item>, ResourceKey<Item>> entry : slippery) {
            if (entry.map(thing::is, (y) -> y.equals(key.get()))) return false;
        }
        return true;
    }

    @Override
    public void getExtraTooltip(
        ItemStack stack,
        List<Component> tooltips,
        TooltipContext tooltipContext,
        TooltipFlag tooltipType) {
        super.getExtraTooltip(stack, tooltips, tooltipContext, tooltipType);
        if (stack.get(PlayerCollarsMod.HELD_ITEMS_COMPONENT_TYPE) != null) tooltips.add(Component.translatable("item.playercollars.paws.slippery"));
        if (stack.get(PlayerCollarsMod.CAN_INTERACT_COMPONENT_TYPE) != null) tooltips.add(Component.translatable("item.playercollars.paws.interaction"));
    }


    public static ResourceKey<Item> getRegistryKey(DyeColor c) {
        return ResourceKey.create(Registries.ITEM, PlayerCollarsMod.id(c.getName() + "_paws"));
    }
}
