package org.jlortiz.playercollars;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import java.util.function.Consumer;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;

public record ClickerSetting(boolean makeLook) implements TooltipProvider {
    public static final ClickerSetting MAKE_LOOK = new ClickerSetting(true);
    public static final ClickerSetting DEFAULT = new ClickerSetting(false);

    public static final Codec<ClickerSetting> CODEC =
        Codec.BOOL.fieldOf("make_look").xmap(ClickerSetting::new, ClickerSetting::makeLook).codec();
    public static final StreamCodec<ByteBuf, ClickerSetting> STREAM_CODEC =
        ByteBufCodecs.BOOL.map(ClickerSetting::new, ClickerSetting::makeLook);

    @Override
    public void addToTooltip(
        Item.TooltipContext context,
        Consumer<Component> consumer,
        TooltipFlag flag,
        DataComponentGetter components) {
        if (makeLook) {
            consumer.accept(Component.translatable("item.playercollars.clicker.turn"));
        }
    }
}
