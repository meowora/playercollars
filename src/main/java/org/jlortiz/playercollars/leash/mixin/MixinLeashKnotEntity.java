package org.jlortiz.playercollars.leash.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Interaction;
import net.minecraft.world.entity.decoration.BlockAttachedEntity;
import net.minecraft.world.entity.decoration.LeashFenceKnotEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jlortiz.playercollars.PlayerCollarsMod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class MixinLeashKnotEntity {
    @Shadow
    private Level level;

    @Inject(method = "dropAllLeashConnections", at = @At("HEAD"), cancellable = true)
    public void test(Player player, CallbackInfoReturnable<Boolean> cir) {
        if (!level.isClientSide() && PlayerCollarsMod.blockLeashKnotBreak((ServerLevel) level, player, (Entity) (Object) this)) {
            cir.setReturnValue(false);
        }
    }


}
