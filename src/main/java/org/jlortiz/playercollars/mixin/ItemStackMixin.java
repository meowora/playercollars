package org.jlortiz.playercollars.mixin;

import java.util.function.Consumer;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.component.TooltipProvider;
import org.jlortiz.playercollars.PlayerCollarsMod;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Shadow
    public abstract <T extends TooltipProvider> void addToTooltip(
        DataComponentType<T> type,
        Item.TooltipContext context,
        TooltipDisplay display,
        Consumer<Component> consumer,
        TooltipFlag flag);

    @Inject(method = "addDetailsToTooltip", at = @At(
        value = "FIELD",
        target = "Lnet/minecraft/core/component/DataComponents;PROFILE:Lnet/minecraft/core/component/DataComponentType;"
    ))
    public void tooltip(
        Item.TooltipContext context,
        TooltipDisplay display,
        @Nullable Player player,
        TooltipFlag tooltipFlag,
        Consumer<Component> builder,
        CallbackInfo ci) {
        this.addToTooltip(PlayerCollarsMod.OWNER_COMPONENT_TYPE, context, display, builder, tooltipFlag);
        this.addToTooltip(PlayerCollarsMod.CLICKER_SETTING, context, display, builder, tooltipFlag);
    }

}
