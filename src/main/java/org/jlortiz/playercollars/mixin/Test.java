package org.jlortiz.playercollars.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.server.IntegratedServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(IntegratedServer.class)
public class Test {

    @WrapOperation(method = "initServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/server/IntegratedServer;setUsesAuthentication(Z)V"))
    public void no(IntegratedServer instance, boolean b, Operation<Void> original) {
        original.call(instance, false);
    }

}
