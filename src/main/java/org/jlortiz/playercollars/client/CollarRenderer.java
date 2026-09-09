package org.jlortiz.playercollars.client;

import com.mojang.authlib.minecraft.client.MinecraftClient;
import com.mojang.blaze3d.vertex.PoseStack;
import io.wispforest.accessories.api.AccessoriesStorageLookup;
import io.wispforest.accessories.api.client.AccessoryRenderState;
import io.wispforest.accessories.api.client.renderers.AccessoryRenderer;
import io.wispforest.accessories.api.slot.SlotPath;
import io.wispforest.accessories.api.slot.SlotReference;
import io.wispforest.owo.braid.core.BraidGraphics;
import java.util.List;
import java.util.Objects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.component.DataComponents;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jlortiz.playercollars.PlayerCollarsMod;
import org.joml.Quaternionf;

public class CollarRenderer implements AccessoryRenderer {
    ContextKey<ItemStackRenderState> COLLAR = new ContextKey<>(PlayerCollarsMod.id("collar"));
    ContextKey<Boolean> HAS_CHESTPLATE = new ContextKey<>(PlayerCollarsMod.id("has_chestplate"));

    @Override
    public void extractRenderState(
        ItemStack stack,
        SlotPath path,
        AccessoriesStorageLookup storageLookup,
        LivingEntity entity,
        LivingEntityRenderState entityState,
        AccessoryRenderState accessoryState) {
        AccessoryRenderer.super.extractRenderState(stack, path, storageLookup, entity, entityState, accessoryState);

        boolean hasChestplate = false;
        for (ItemStack is : List.of(
            entity.getItemBySlot(EquipmentSlot.HEAD),
            entity.getItemBySlot(EquipmentSlot.BODY),
            entity.getItemBySlot(EquipmentSlot.LEGS),
            entity.getItemBySlot(EquipmentSlot.FEET)
        )) {
            if (is.is(ItemTags.CHEST_ARMOR)) {
                hasChestplate = true;
                break;
            }
        }


        accessoryState.setStateData(HAS_CHESTPLATE, hasChestplate);

        var collar = new ItemStackRenderState();
        stack = stack.copy();
        stack.remove(DataComponents.ENCHANTMENT_GLINT_OVERRIDE);
        stack.remove(DataComponents.ENCHANTMENTS);


        Minecraft.getInstance().getItemModelResolver().updateForLiving(collar, stack, ItemDisplayContext.FIXED, entity);
        accessoryState.setStateData(COLLAR, collar);
    }

    @Override
    public <S extends LivingEntityRenderState> void render(
        AccessoryRenderState accessoryState,
        S entityState,
        EntityModel<S> model,
        PoseStack matrices,
        SubmitNodeCollector collector) {
        model.setupAnim(entityState);
        try {
            ModelPart body = ((PlayerModel) model).body;
            var hasChestplate = accessoryState.getStateData(HAS_CHESTPLATE);
            matrices.translate(0, 0.20, -0.15);
            matrices.scale(-0.6f , -.6f, .6f);

            accessoryState.getStateData(COLLAR).submit(matrices, collector, LightCoordsUtil.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 0);
        } catch (ClassCastException ignored) {}
    }
}
