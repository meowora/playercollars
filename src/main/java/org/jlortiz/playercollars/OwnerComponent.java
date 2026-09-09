package org.jlortiz.playercollars;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;

public record OwnerComponent(UUID uuid, String name, Optional<UUID> owned, Optional<String> ownedName) implements
    TooltipProvider {
    public OwnerComponent(UUID uuid, String name) {
        this(uuid, name, Optional.empty(), Optional.empty());
    }

    @Override
    public void addToTooltip(
        Item.TooltipContext context,
        Consumer<Component> consumer,
        TooltipFlag flag,
        DataComponentGetter components) {

        consumer.accept(Component.translatable("item.playercollars.collar.owner", name()).withStyle(ChatFormatting.GRAY));
    }
}
