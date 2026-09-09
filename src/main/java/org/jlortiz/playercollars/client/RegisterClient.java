package org.jlortiz.playercollars.client;

import io.wispforest.accessories.api.client.AccessoriesRendererRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.jlortiz.playercollars.PlayerCollarsMod;
import org.jlortiz.playercollars.client.screen.PawsConfigScreen;
import org.jlortiz.playercollars.item.FootPawsItem;
import org.jlortiz.playercollars.network.PacketLookAtLerped;

@Environment(EnvType.CLIENT)
public class RegisterClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        AccessoriesRendererRegistry.registerRenderer(PlayerCollarsMod.COLLAR_ITEM, CollarRenderer::new);
        AccessoriesRendererRegistry.registerRenderer(PlayerCollarsMod.TAGLESS_COLLAR_ITEM, CollarRenderer::new);
        for (FootPawsItem p : PlayerCollarsMod.PAWS_ITEMS) {
            AccessoriesRendererRegistry.registerRenderer(p, PawRenderer::new);
        }
        for (FootPawsItem p : PlayerCollarsMod.FOOT_PAWS_ITEMS) {
            AccessoriesRendererRegistry.registerRenderer(p, FootPawRenderer::new);
        }
        ClientPlayNetworking.registerGlobalReceiver(
            PacketLookAtLerped.ID,
            (payload, context) -> context.client().execute(() -> RotationLerpHandler.beginClickTurn(payload.vec())));
        LevelRenderEvents.END_MAIN.register(RotationLerpHandler::turnTowardsClick);
        MenuScreens.register(PlayerCollarsMod.PAWS_BLOCK_CONFIG_SCREEN_HANDLER, PawsConfigScreen<Block>::new);
        MenuScreens.register(PlayerCollarsMod.PAWS_ITEM_CONFIG_SCREEN_HANDLER, PawsConfigScreen<Item>::new);
    }
}
