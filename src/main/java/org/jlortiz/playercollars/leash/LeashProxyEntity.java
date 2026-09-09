package org.jlortiz.playercollars.leash;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.turtle.Turtle;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Team;
import org.jetbrains.annotations.NotNull;
import org.joml.Math;

import java.util.Objects;
import org.jspecify.annotations.Nullable;

public final class LeashProxyEntity extends Turtle {
    private final LivingEntity target;
    private static final EntityDimensions DIMENSIONS = EntityDimensions.fixed(Mth.EPSILON, Mth.EPSILON);

    private boolean proxyUpdate() {
        if (proxyIsRemoved()) return false;

        if (target == null) return true;
        if (target.level() != level() || !target.isAlive()) return true;

        Vec3 posActual = this.position();
        Vec3 posTarget = switch (target.getPose()) {
            // No point in making cases for SPIN_ATTACK since leashed players can't use it
            case CROUCHING: yield new Vec3(0.0D, 1.1D, -0.15D);
            case SWIMMING: yield Vec3.directionFromRotation(0, target.yBodyRot).scale(0.35).add(0, 0.2, -0.1);
            case FALL_FLYING: yield new Vec3(0, 1.3, -0.15).xRot(-Math.toRadians(90 + target.getXRot()))
                    .yRot(-Math.toRadians(target.yBodyRot));
            case SLEEPING: if (target.getBedOrientation() != null)
                    yield target.getBedOrientation().getUnitVec3().scale(-0.2f).add(0, 0.1, -0.15);
            default: yield new Vec3(0.0D, 1.3D, -0.15D);
        };
        posTarget = posTarget.scale(target.getScale()).add(target.position());

        if (!Objects.equals(posActual, posTarget)) {
            setRot(0.0F, 0.0F);
            setPos(posTarget.x, posTarget.y, posTarget.z);
            setBoundingBox(DIMENSIONS.makeBoundingBox(target.position()));
        }

        return false;
    }

    @NotNull
    public LivingEntity getLeashTarget() {
        return target;
    }

    @Override
    public void tick() {
        setInvisible(true);
        if (this.level().isClientSide()) return;
        if (proxyUpdate() && !proxyIsRemoved()) {
            proxyRemove();
        }
    }

    public boolean proxyIsRemoved() {
        return this.isRemoved();
    }

    public void proxyRemove() {
        super.remove(RemovalReason.DISCARDED);
    }

    @Override
    public void remove(RemovalReason reason) {
    }

    public static final String TEAM_NAME = "leashplayersimpl";

    public LeashProxyEntity(@NotNull LivingEntity target) {
        super(EntityTypes.TURTLE, target.level());
        this.target = target;

        setHealth(1.0F);
        setInvulnerable(true);
        setBaby(true);
        setInvisible(true);
        noPhysics = true;

        MinecraftServer server = target.level().getServer();
        if (server != null) {
            ServerScoreboard scoreboard = server.getScoreboard();

            var team = scoreboard.getPlayerTeam(TEAM_NAME);
            if (team == null) {
                team = scoreboard.addPlayerTeam(TEAM_NAME);
            }
            if (team.getCollisionRule() != Team.CollisionRule.NEVER) {
                team.setCollisionRule(Team.CollisionRule.NEVER);
            }

            scoreboard.addPlayerToTeam(target.getScoreboardName(), team);
        }
        proxyUpdate();
    }

    @Override
    public float getHealth() {
        return 1.0F;
    }


    @Override
    public void dropLeash() {
    }

    @Override
    public void removeLeash() {
    }

    @Override
    public boolean canBeLeashed() {
        return false;
    }

    @Override
    protected void registerGoals() {
    }


    @Override
    public void push(Entity entity) {
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putString("Team", TEAM_NAME);
    }

    @Override
    protected void doPush(Entity entity) {

    }

    @Override
    public void playerTouch(Player player) {
    }
}