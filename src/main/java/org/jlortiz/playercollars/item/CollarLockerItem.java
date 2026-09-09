package org.jlortiz.playercollars.item;

import io.wispforest.accessories.api.AccessoriesCapability;
import io.wispforest.accessories.api.slot.SlotEntryReference;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.jlortiz.playercollars.PlayerCollarsMod;

import java.util.List;

public class CollarLockerItem extends Item {
    public static final ResourceKey<Item>
        REGISTRY_KEY = ResourceKey.create(Registries.ITEM, PlayerCollarsMod.id("collar_locker"));
    public CollarLockerItem() {
        super(new Properties().stacksTo(1).setId(REGISTRY_KEY));
    }

    @Override
    public InteractionResult interactLivingEntity(
        ItemStack itemStack,
        Player user,
        LivingEntity target,
        InteractionHand type) {
        if (!(target instanceof Player player) || player.level().isClientSide()) return InteractionResult.PASS;
        AccessoriesCapability cap = AccessoriesCapability.get(player);
        if (cap == null) return InteractionResult.PASS;

        ItemStack collarStack = PlayerCollarsMod.filterStacksByOwner(cap.getEquipped((x) -> x.is(PlayerCollarsMod.COLLAR_TAG)), user.getUUID(), player.getUUID());
        if (collarStack == null) {
            user.sendOverlayMessage(Component.translatable("item.playercollars.collar_locker.no_set_non_owner").withStyle(
                ChatFormatting.RED));
            return InteractionResult.FAIL;
        }
        if (collarStack.get(PlayerCollarsMod.OWNER_COMPONENT_TYPE).owned().isEmpty()) {
            user.sendOverlayMessage(Component.translatable("item.playercollars.collar_locker.no_set_non_deed").withStyle(ChatFormatting.RED));
            return InteractionResult.FAIL;
        }

        Holder<Enchantment> binding = ((ServerPlayer) user).level().registryAccess()
                .lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.BINDING_CURSE);
        boolean shouldLock = !EnchantmentHelper.has(collarStack, EnchantmentEffectComponents.PREVENT_ARMOR_CHANGE);
        List<SlotEntryReference> ls = cap.getEquipped(
                (y) -> y.is(PlayerCollarsMod.COLLAR_TAG) ||
                        y.is(PlayerCollarsMod.PAWS_TAG) ||
                        y.is(PlayerCollarsMod.FOOT_PAWS_TAG)
        );

        for (SlotEntryReference p : ls) {
            ItemStack is = p.stack();
            if (!is.isEnchanted()) {
                if (shouldLock) {
                    ItemEnchantments.Mutable ench = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
                    ench.set(binding, 1);
                    EnchantmentHelper.setEnchantments(is, ench.toImmutable());
                }
                continue;
            }
            EnchantmentHelper.updateEnchantments(is, (ench) -> {
                if (shouldLock) ench.set(binding, 1);
                else ench.removeIf((e) -> e.value().effects().has(EnchantmentEffectComponents.PREVENT_ARMOR_CHANGE));
            });
        }
        player.sendOverlayMessage(Component.translatable(shouldLock ? "item.playercollars.collar_locker.locked" : "item.playercollars.collar_locker.unlocked"));
        user.sendOverlayMessage(Component.translatable(shouldLock ? "item.playercollars.collar_locker.locked" : "item.playercollars.collar_locker.unlocked"));
        player.level().playSound(null, target.getX(), target.getY(), target.getZ(), shouldLock ? SoundEvents.ARMOR_EQUIP_WOLF.value() : SoundEvents.ARMOR_UNEQUIP_WOLF, SoundSource.PLAYERS);

        return InteractionResult.SUCCESS;
    }
}
