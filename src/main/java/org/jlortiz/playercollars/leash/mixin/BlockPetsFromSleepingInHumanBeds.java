package org.jlortiz.playercollars.leash.mixin;

import io.wispforest.accessories.api.AccessoriesCapability;
import java.util.List;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jlortiz.playercollars.PlayerCollarsMod;
import org.jlortiz.playercollars.block.DogBedBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BedBlock.class)
public class BlockPetsFromSleepingInHumanBeds {

    @Inject(method = "useWithoutItem", at = @At("HEAD"))
    public void blockPets(
        BlockState state,
        Level level,
        BlockPos pos,
        Player player,
        BlockHitResult hitResult,
        CallbackInfoReturnable<InteractionResult> cir) {
        if (level.isClientSide()) return;
        if (((Object) this) instanceof DogBedBlock) return;
        if (!Optional.ofNullable(AccessoriesCapability.get(player)).map((x) -> x.getEquipped((y) -> y.is(PlayerCollarsMod.COLLAR_TAG))).map(List::isEmpty).orElse(true)) {
            player.sendOverlayMessage(Component.literal("Pets can't sleep in human beds, silly!").withStyle(
                ChatFormatting.RED));
            cir.setReturnValue(InteractionResult.FAIL);
        }
    }

}
