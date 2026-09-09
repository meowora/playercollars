package org.jlortiz.playercollars.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jlortiz.playercollars.PlayerCollarsMod;

public record PacketLookAtLerped(double x, double y, double z) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<PacketLookAtLerped> ID = new CustomPacketPayload.Type<>(PlayerCollarsMod.id("look_at"));
    public static final StreamCodec<ByteBuf, PacketLookAtLerped> CODEC = StreamCodec.composite(
        ByteBufCodecs.DOUBLE, PacketLookAtLerped::x,
        ByteBufCodecs.DOUBLE, PacketLookAtLerped::y,
        ByteBufCodecs.DOUBLE, PacketLookAtLerped::z,
            PacketLookAtLerped::new);

    public PacketLookAtLerped(Entity entity) {
        this(entity.getX(), entity.getEyeY(), entity.getZ());
    }

    public Vec3 vec() {
        return new Vec3(x, y, z);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
