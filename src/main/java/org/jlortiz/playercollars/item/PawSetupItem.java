package org.jlortiz.playercollars.item;

import com.mojang.authlib.minecraft.client.MinecraftClient;
import io.wispforest.accessories.api.AccessoriesCapability;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jlortiz.playercollars.PlayerCollarsMod;
import org.jlortiz.playercollars.client.screen.PawsSelectScreen;
import org.jspecify.annotations.Nullable;

public class PawSetupItem extends Item {
    public static final ResourceKey<Item> REGISTRY_KEY = ResourceKey.create(Registries.ITEM, PlayerCollarsMod.id("paw_configurator"));

    public PawSetupItem() {
        super(new Item.Properties().stacksTo(1).setId(REGISTRY_KEY));
    }

    @Override
    public @Nullable ItemStackTemplate getCraftingRemainder(ItemStack stack) {
        return ItemStackTemplate.fromStack(stack);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack is = player.getItemInHand(hand);
        if (!player.isCrouching() || !level.isClientSide()) return InteractionResult.PASS;
        return interactLivingEntity(is, player, player, hand);
    }

    @Override
    public InteractionResult interactLivingEntity(
        ItemStack itemStack,
        Player user,
        LivingEntity target,
        InteractionHand type) {
        if (!(target instanceof Player player) || !user.level().isClientSide()) return InteractionResult.PASS;
        AccessoriesCapability cap = AccessoriesCapability.get(player);
        if (cap == null) return InteractionResult.PASS;

        ItemStack collarStack = PlayerCollarsMod.filterStacksByOwner(cap.getEquipped((x) -> x.is(PlayerCollarsMod.COLLAR_TAG)), user.getUUID(), player.getUUID());
        if (collarStack == null) {
            user.sendOverlayMessage(Component.translatable("item.playercollars.paw_configurator.no_set_non_owner").withStyle(ChatFormatting.RED));
            return InteractionResult.FAIL;
        }
        Minecraft.getInstance().gui.setScreen(new PawsSelectScreen(player));
        return InteractionResult.SUCCESS;
    }
}
