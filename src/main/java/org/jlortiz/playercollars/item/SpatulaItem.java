package org.jlortiz.playercollars.item;

import io.wispforest.accessories.api.AccessoriesCapability;
import io.wispforest.accessories.api.slot.SlotEntryReference;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.*;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import org.jlortiz.playercollars.PlayerCollarsMod;

public class SpatulaItem extends Item {
    public static final ResourceKey<Item>
        REGISTRY_KEY = ResourceKey.create(Registries.ITEM, PlayerCollarsMod.id("golden_spatula"));
    public SpatulaItem() {
        super(new Item.Properties().stacksTo(1).durability(8).setId(REGISTRY_KEY));
    }

    @Override
    public InteractionResult use(Level level, Player user, InteractionHand hand) {
        if (user.isCrouching()) {
            InteractionResult res = interactLivingEntity(user.getItemInHand(hand), user, user, hand);
            if (res.consumesAction()) return InteractionResult.SUCCESS;
        }
        return super.use(level, user, hand);
    }

    @Override
    public InteractionResult interactLivingEntity(
        ItemStack itemStack,
        Player player,
        LivingEntity target,
        InteractionHand type) {

        ServerLevel world = null;
        if (!player.level().isClientSide())
            world = (ServerLevel) player.level();

        int count = 0;
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (!slot.isArmor()) continue;
            ItemStack is = target.getItemBySlot(slot);
            if (EnchantmentHelper.has(is, EnchantmentEffectComponents.PREVENT_ARMOR_CHANGE)) {
                count++;
                if (world != null)
                    target.spawnAtLocation(world, is);
                target.setItemSlot(slot, ItemStack.EMPTY);
            }
        }

        AccessoriesCapability cap = AccessoriesCapability.get(target);
        if (cap != null) {
            for (SlotEntryReference p : cap.getAllEquipped()) {
                if (EnchantmentHelper.has(p.stack(), EnchantmentEffectComponents.PREVENT_ARMOR_CHANGE)) {
                    count++;
                    if (world != null)
                        target.spawnAtLocation(world, p.stack());
                    p.reference().setStack(ItemStack.EMPTY);
                }
            }
        }

        if (count == 0) return InteractionResult.PASS;
        itemStack.hurtAndBreak(count, player, type.asEquipmentSlot());
        target.playSound(SoundEvents.WOLF_ARMOR_BREAK.value());
        return InteractionResult.SUCCESS;
    }
}
