package org.jlortiz.playercollars.item;

import io.wispforest.accessories.api.AccessoriesCapability;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Unit;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantable;
import net.minecraft.world.level.Level;
import org.jlortiz.playercollars.ClickerSetting;
import org.jlortiz.playercollars.PlayerCollarsMod;
import org.jlortiz.playercollars.network.PacketLookAtLerped;

import java.util.List;

public class ClickerItem extends Item {
    public static final ResourceKey<Item>
        REGISTRY_KEY = ResourceKey.create(Registries.ITEM, PlayerCollarsMod.id("clicker"));
    public ClickerItem() {
        super(new Item.Properties().stacksTo(1).setId(REGISTRY_KEY)
                .component(DataComponents.ENCHANTABLE, new Enchantable(45)));
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        player.startUsingItem(hand);
        if (!level.isClientSide()) {
            ItemStack is = player.getItemInHand(hand);
            var setting = is.getOrDefault(PlayerCollarsMod.CLICKER_SETTING, ClickerSetting.DEFAULT);
            if (player.isCrouching()) {
                if (setting.makeLook()) {
                    is.set(PlayerCollarsMod.CLICKER_SETTING, ClickerSetting.DEFAULT);
                    player.sendOverlayMessage(Component.translatable("item.playercollars.clicker.turn_disable"));
                } else {
                    is.set(PlayerCollarsMod.CLICKER_SETTING, ClickerSetting.MAKE_LOOK);
                    player.sendOverlayMessage(Component.translatable("item.playercollars.clicker.turn_enable"));
                }
                return InteractionResult.CONSUME;
            }

            double distance = player.getAttributeValue(PlayerCollarsMod.ATTR_CLICKER_DISTANCE);
            if (distance > 0 && setting.makeLook()) {
                List<ServerPlayer> plrs = ((ServerLevel) level).getPlayers((p) -> !p.is(player) && p.closerThan(player, distance));
                PacketLookAtLerped packet = new PacketLookAtLerped(player);
                for (ServerPlayer p : plrs) {
                    AccessoriesCapability cap = AccessoriesCapability.get(p);
                    if (cap != null) {
                        ItemStack collar = PlayerCollarsMod.filterStacksByOwner(cap.getEquipped((x) -> x.is(PlayerCollarsMod.COLLAR_TAG)), player.getUUID(), p.getUUID());
                        if (collar != null) {
                            ServerPlayNetworking.send(p, packet);
                        }
                    }
                }
            }
            level.playSound(null, player, PlayerCollarsMod.CLICKER_ON, SoundSource.PLAYERS, 1, 1);
        }
        return InteractionResult.FAIL;
    }

    @Override
    public int getUseDuration(ItemStack itemStack, LivingEntity user) {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean releaseUsing(ItemStack itemStack, Level level, LivingEntity entity, int remainingTime) {
        if (!level.isClientSide()) {
            level.playSound(null, entity, PlayerCollarsMod.CLICKER_OFF, SoundSource.PLAYERS, 1, 1);
        }
        return false;
    }
}
