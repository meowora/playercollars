package org.jlortiz.playercollars.network;

import io.netty.buffer.ByteBuf;
import io.wispforest.accessories.api.AccessoriesCapability;
import io.wispforest.accessories.api.slot.SlotEntryReference;
import io.wispforest.accessories.compat.config.client.ExtendedConfigScreen;
import net.fabricmc.fabric.api.menu.v1.ExtendedMenuProvider;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.ChatFormatting;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.*;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.jlortiz.playercollars.PlayerCollarsMod;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public record PacketOpenPawsConfig(UUID pawHolder, boolean heldItems) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<PacketOpenPawsConfig> ID = new CustomPacketPayload.Type<>(PlayerCollarsMod.id("paws_config"));
    public static final StreamCodec<ByteBuf, PacketOpenPawsConfig> CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC, PacketOpenPawsConfig::pawHolder,
            ByteBufCodecs.BOOL, PacketOpenPawsConfig::heldItems,
            PacketOpenPawsConfig::new);

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    public void handle(ServerPlayNetworking.Context context) {
        context.server().execute(() -> {
            Player pet = context.player().level().getPlayerByUUID(pawHolder);
            if (pet == null) return;
            AccessoriesCapability cap = AccessoriesCapability.get(pet);
            if (cap == null) return;

            ItemStack collarStack = PlayerCollarsMod.filterStacksByOwner(cap.getEquipped((y) -> y.is(PlayerCollarsMod.COLLAR_TAG)), context.player().getUUID(), pawHolder);
            if (collarStack == null) {
                context.player().sendOverlayMessage(Component.translatable("item.playercollars.paw_configurator.no_set_non_owner").withStyle(ChatFormatting.RED));
                return;
            }

            List<SlotEntryReference> pawsStack = cap.getEquipped((y) -> y.is(PlayerCollarsMod.PAWS_TAG));
            if (pawsStack.isEmpty()) {
                context.player().sendOverlayMessage(Component.translatable("item.playercollars.paw_configurator.no_paws").withStyle(
                    ChatFormatting.RED));
                return;
            }

            context.player().openMenu(new ExtendedMenuProvider() {
                @Override
                public AbstractContainerMenu createMenu(int syncId, Inventory playerInventory, Player player) {
                    ItemStack[] ps = new ItemStack[pawsStack.size()];
                    for (int i = 0; i < pawsStack.size(); i++)
                        ps[i] = pawsStack.get(i).stack();

                    PawsConfigScreenHandler sc = heldItems ?
                            new PawsConfigScreenHandler.PawsItemConfigScreenHandler(syncId, playerInventory,
                                    ps[0].get(PlayerCollarsMod.HELD_ITEMS_COMPONENT_TYPE)) :
                            new PawsConfigScreenHandler.PawsBlockConfigScreenHandler(syncId, playerInventory,
                                    ps[0].get(PlayerCollarsMod.CAN_INTERACT_COMPONENT_TYPE));
                    sc.setPawsStack(ps);
                    return sc;
                }

                @Override
                public Component getDisplayName() {
                    return Component.translatable(heldItems ? "gui.playercollars.paw_configurator.item.title" :
                            "gui.playercollars.paw_configurator.block.title", pet.getName());
                }

                @Override
                public Object getScreenOpeningData(ServerPlayer player) {
                    return Optional.ofNullable(pawsStack.get(0).stack().get(heldItems ?
                            PlayerCollarsMod.HELD_ITEMS_COMPONENT_TYPE :
                            PlayerCollarsMod.CAN_INTERACT_COMPONENT_TYPE)).orElse(List.of());
                }
            });
        });
    }
}
