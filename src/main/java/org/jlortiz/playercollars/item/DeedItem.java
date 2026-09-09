package org.jlortiz.playercollars.item;

import com.mojang.authlib.minecraft.client.MinecraftClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jlortiz.playercollars.OwnerComponent;
import org.jlortiz.playercollars.PlayerCollarsMod;
import org.jlortiz.playercollars.client.screen.DeedItemScreen;

public class DeedItem extends Item {
    public static final ResourceKey<Item>
        REGISTRY_KEY = ResourceKey.create(Registries.ITEM, PlayerCollarsMod.id("deed_of_ownership"));

    public DeedItem() {
        super(new Properties().stacksTo(1).setId(REGISTRY_KEY));
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack is = player.getItemInHand(hand);
        if (level.isClientSide()) {
            OwnerComponent owner = is.get(PlayerCollarsMod.OWNER_COMPONENT_TYPE);
            if (owner != null && owner.owned().isEmpty()) {
                if (owner.uuid().equals(player.getUUID())) {
                    player.sendOverlayMessage(Component.translatable("item.playercollars.deed_of_ownership.no_self_own"));
                    return InteractionResult.PASS;
                }
                openTheScreen(is, player);
                return InteractionResult.CONSUME;
            }
        } else if (is.get(PlayerCollarsMod.OWNER_COMPONENT_TYPE) == null) {
            is.set(PlayerCollarsMod.OWNER_COMPONENT_TYPE, new OwnerComponent(player.getUUID(), player.getName().getString()));
            player.sendOverlayMessage(Component.translatable("item.playercollars.deed_of_ownership.filled_out"));
            return InteractionResult.CONSUME;
        }
        return InteractionResult.PASS;
    }

    @Environment(EnvType.CLIENT)
    private void openTheScreen(ItemStack is, Player plr) {
        Minecraft.getInstance().gui.setScreen(new DeedItemScreen(is, plr));
    }

    @Override
    public Component getName(ItemStack stack) {
        if (stack.get(PlayerCollarsMod.OWNER_COMPONENT_TYPE) != null)
            return Component.translatable("item.playercollars.deed_of_ownership.filled");
        return super.getName(stack);
    }
}
