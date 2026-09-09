package org.jlortiz.playercollars.leash.mixin;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.turtle.Turtle;
import net.minecraft.world.level.Level;
import net.minecraft.world.scores.Team;
import org.jlortiz.playercollars.leash.LeashProxyEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(Turtle.class)
public abstract class MixinTurtleEntity extends Animal {


    protected MixinTurtleEntity(EntityType<? extends Animal> type, Level level) {
        super(type, level);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("RETURN"))
    private void leashplayers$onReadCustomDataFromNbt(CallbackInfo info) {
        MinecraftServer server = level().getServer();
        if (server == null) return;

        Team team = server.getScoreboard().getPlayerTeam(getScoreboardName());
        if (team != null && Objects.equals(team.getName(), LeashProxyEntity.TEAM_NAME)) {
            dropLeash();
            setInvulnerable(false);
            kill((ServerLevel) level());
        }
    }
}