package org.jlortiz.playercollars.client;


import com.mojang.authlib.minecraft.client.MinecraftClient;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import io.wispforest.accessories.api.AccessoriesStorageLookup;
import io.wispforest.accessories.api.client.AccessoriesRenderStateKeys;
import io.wispforest.accessories.api.client.AccessoryRenderState;
import io.wispforest.accessories.api.client.renderers.AccessoryRenderer;
import io.wispforest.accessories.api.client.rendering.Side;
import io.wispforest.accessories.api.slot.SlotPath;
import io.wispforest.accessories.api.slot.SlotReference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.Lightmap;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jlortiz.playercollars.PlayerCollarsMod;
import org.joml.Quaternionf;

public class FootPawRenderer implements AccessoryRenderer {
    ContextKey<ItemStackRenderState> LEFT = new ContextKey<>(PlayerCollarsMod.id("left_paw"));
    ContextKey<ItemStackRenderState> RIGHT = new ContextKey<>(PlayerCollarsMod.id("right_paw"));

    private static void renderForLeg(ItemStackRenderState stack, PoseStack matrices, PlayerModel model, boolean left, SubmitNodeCollector collector) {
        matrices.pushPose();
        AccessoryRenderer.transformToFace(matrices, left ? model.leftLeg : model.rightLeg, Side.BOTTOM);
        matrices.rotateAround(new Quaternionf().rotateXYZ((float) -Math.PI / 2, 0, 0), 0, 0, 0);
        matrices.translate(0, 0, 0.125);
        matrices.scale(0.75f, 0.75f, 0.75f);

        stack.submit(matrices, collector, LightCoordsUtil.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 0);

        matrices.popPose();
    }

    @Override
    public void extractRenderState(
        ItemStack stack,
        SlotPath path,
        AccessoriesStorageLookup storageLookup,
        LivingEntity entity,
        LivingEntityRenderState entityState,
        AccessoryRenderState accessoryState) {
        AccessoryRenderer.super.extractRenderState(stack, path, storageLookup, entity, entityState, accessoryState);
        stack = stack.copy();
        stack.remove(DataComponents.ENCHANTMENT_GLINT_OVERRIDE);
        stack.remove(DataComponents.ENCHANTMENTS);
        var left = new ItemStackRenderState();
        Minecraft.getInstance().getItemModelResolver().updateForLiving(left, stack, ItemDisplayContext.FIXED, entity);
        accessoryState.setStateData(LEFT, left);
        var right = new ItemStackRenderState();
        Minecraft.getInstance().getItemModelResolver().updateForLiving(right, stack, ItemDisplayContext.FIXED, entity);
        accessoryState.setStateData(RIGHT, right);
    }

    @Override
    public <S extends LivingEntityRenderState> void render(
        AccessoryRenderState accessoryState,
        S entityState,
        EntityModel<S> model,
        PoseStack matrices,
        SubmitNodeCollector collector) {
        if (!(model instanceof PlayerModel models)) return;

        renderForLeg(accessoryState.getStateData(LEFT), matrices, models, true, collector);
        renderForLeg(accessoryState.getStateData(RIGHT), matrices, models, false, collector);
    }
}
