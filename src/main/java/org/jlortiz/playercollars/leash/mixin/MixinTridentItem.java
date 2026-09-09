package org.jlortiz.playercollars.leash.mixin;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import org.jlortiz.playercollars.leash.LeashImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TridentItem.class)
public class MixinTridentItem {
    /**
     * Prevent Riptide boosting when the player is leashed.
     * This lets the normal "use" (throwing) still proceed, but cancels the riptide branch
     * in onStoppedUsing by returning false when the trident has Riptide (f > 0.0F).
     */
    @Inject(
            method = "releaseUsing",
            at = @At("HEAD"),
            cancellable = true
    )
    private void onStoppedUsing(
        ItemStack itemStack,
        Level level,
        LivingEntity entity,
        int remainingTime,
        CallbackInfoReturnable<Boolean> cir) {
        if (!(entity instanceof Player player)) return;

        if (player instanceof LeashImpl leash && leash.leashplayers$getProxyLeashHolder() != null) {
            float f = EnchantmentHelper.getTridentSpinAttackStrength(itemStack, player);
            if (f > 0.0F) {
                cir.setReturnValue(false);
            }
        }
    }
}
