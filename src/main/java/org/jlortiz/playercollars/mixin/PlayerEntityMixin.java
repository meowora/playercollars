package org.jlortiz.playercollars.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.wispforest.accessories.api.AccessoriesCapability;
import io.wispforest.accessories.api.slot.SlotEntryReference;
import net.fabricmc.fabric.api.tag.convention.v2.TagUtil;
import net.minecraft.core.Holder;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.jlortiz.playercollars.PlayerCollarsMod;
import org.jlortiz.playercollars.item.PawsItem;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class PlayerEntityMixin extends LivingEntity {
    @Shadow @Final
    private Inventory inventory;

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> type, Level level) {
        super(type, level);
    }


    @Redirect(method = "getDestroySpeed", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getDestroySpeed(Lnet/minecraft/world/level/block/state/BlockState;)F"), require=0)
    private float getBlockBreakingSpeed(ItemStack instance, BlockState state) {
        float ret = instance.getDestroySpeed(state);
        AccessoriesCapability cap = AccessoriesCapability.get(this);
        if (cap == null) return ret;
        if (cap.getEquipped((x) -> x.is(PlayerCollarsMod.PAWS_TAG)).isEmpty()) return ret;
        if (TagUtil.isIn(BlockTags.MINEABLE_WITH_SHOVEL, state.getBlock())) {
            return ToolMaterial.IRON.speed();
        }
        return (ret - 1) * 0.125f + 1;
    }

    @WrapOperation(method="attack", at= @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getAttributeValue(Lnet/minecraft/core/Holder;)D", ordinal=0), require=0)
    private double getAttributeValue(Player instance, Holder holder, Operation<Double> original) {
        double ret = original.call(instance, holder);
        AccessoriesCapability cap = AccessoriesCapability.get(this);
        if (cap == null) return ret;
        if (cap.getEquipped((x) -> x.is(PlayerCollarsMod.PAWS_TAG)).isEmpty()) return ret;
        return (ret - 1) * 0.75f + 1;
    }

    @Inject(method = "createAttributes", at = @At("RETURN"))
    private static void playercollars$addAttributes(CallbackInfoReturnable<AttributeSupplier.Builder> cir) {
        cir.getReturnValue().add(PlayerCollarsMod.ATTR_LEASH_DISTANCE).add(PlayerCollarsMod.ATTR_CLICKER_DISTANCE);
    }

    @Inject(method = "aiStep", at= @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Inventory;tick()V", shift = At.Shift.AFTER))
    private void playercollars$dropPawItems(CallbackInfo ci) {
        AccessoriesCapability cap = AccessoriesCapability.get(this);
        if (cap == null) return;
        for (SlotEntryReference sr : cap.getEquipped((x) -> x.is(PlayerCollarsMod.PAWS_TAG))) {
            if (PawsItem.shouldDrop(sr.stack(), inventory.getSelectedItem())) {
                ItemStack stack = inventory.removeFromSelected(true);
                if (!stack.isEmpty()) drop(stack, true, true);
            }
            if (PawsItem.shouldDrop(sr.stack(), inventory.getItem(Inventory.SLOT_OFFHAND))){
                ItemStack stack = inventory.removeItemNoUpdate(Inventory.SLOT_OFFHAND);
                if (!stack.isEmpty()) drop(stack, true, true);
            }
        }
    }

    @WrapOperation(method="updatePlayerPose", at= @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;setPose(Lnet/minecraft/world/entity/Pose;)V"))
    private void playercollars$forceCrawl(Player instance, Pose pose, Operation<Void> original) {
        if (!instance.getAbilities().flying && (pose == Pose.CROUCHING || pose == Pose.STANDING)) {
            AccessoriesCapability cap = AccessoriesCapability.get(this);
            if (cap != null && !cap.getEquipped((x) -> x.is(PlayerCollarsMod.FOOT_PAWS_TAG)).isEmpty())
                pose = Pose.SWIMMING;
        }
        original.call(instance, pose);
    }
}
