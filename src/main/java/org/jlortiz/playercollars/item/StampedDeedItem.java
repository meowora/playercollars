package org.jlortiz.playercollars.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import org.jlortiz.playercollars.OwnerComponent;
import org.jlortiz.playercollars.PlayerCollarsMod;

import java.util.List;
import org.jspecify.annotations.Nullable;

public class StampedDeedItem extends Item {
    public static final ResourceKey<Item> REGISTRY_KEY = ResourceKey.create(Registries.ITEM, PlayerCollarsMod.id("stamped_deed_of_ownership"));

    public StampedDeedItem() {
        super(new Item.Properties().stacksTo(1).setId(REGISTRY_KEY));
    }

    @Override
    public Component getName(ItemStack stack) {
        OwnerComponent owner = stack.get(PlayerCollarsMod.OWNER_COMPONENT_TYPE);
        if (owner == null || owner.ownedName().isEmpty()) return Component.translatable("item.playercollars.stamped_deed_of_ownership.invalid");
        return Component.translatable("item.playercollars.stamped_deed_of_ownership", owner.ownedName().get());
    }


    @Override
    public @Nullable ItemStackTemplate getCraftingRemainder(ItemStack stack) {
        return ItemStackTemplate.fromStack(stack);
    }
}
