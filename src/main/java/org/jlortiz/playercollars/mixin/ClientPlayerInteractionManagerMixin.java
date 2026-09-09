package org.jlortiz.playercollars.mixin;

import io.wispforest.accessories.api.AccessoriesCapability;
import io.wispforest.accessories.api.slot.SlotEntryReference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jlortiz.playercollars.PlayerCollarsMod;
import org.jlortiz.playercollars.item.PawsItem;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MultiPlayerGameMode.class)
public class ClientPlayerInteractionManagerMixin {

    @Shadow @Final private Minecraft minecraft;

    @Unique
    private static boolean shouldPawsBlock(LivingEntity player, BlockState block) {
        AccessoriesCapability cap = AccessoriesCapability.get(player);
        if (cap == null) return false;
        for (SlotEntryReference sr : cap.getEquipped((x) -> x.is(PlayerCollarsMod.PAWS_TAG))) {
            if (PawsItem.shouldPreventBlockInteraction(sr.stack(), block)) {
                return true;
            }
        }
        return false;
    }

    @Inject(method="useItemOn", at=@At("HEAD"), cancellable = true)
    private void playercollars$cancelPawInteractions(
        LocalPlayer player,
        InteractionHand hand,
        BlockHitResult blockHit,
        CallbackInfoReturnable<InteractionResult> cir) {
        if (player.isSpectator()) return;
        BlockState block = this.minecraft.level.getBlockState(blockHit.getBlockPos());
        if (shouldPawsBlock(player, block)) cir.setReturnValue(InteractionResult.PASS);
    }

    @Inject(method = "startDestroyBlock", at = @At(value = "HEAD"), cancellable = true)
    private void playercollars$cancelPawBreak(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        BlockState block = this.minecraft.level.getBlockState(pos);
        if (shouldPawsBlock(minecraft.player, block)) cir.setReturnValue(false);
    }
}
