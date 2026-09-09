package org.jlortiz.playercollars.item;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.wispforest.accessories.api.AccessoriesCapability;
import io.wispforest.accessories.api.slot.SlotEntryReference;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraft.world.phys.Vec3;
import org.jlortiz.playercollars.OwnerComponent;
import org.jlortiz.playercollars.PlayerCollarsMod;

import java.util.List;

public record RegenerationEnchantmentEffect(LevelBasedValue level) implements EnchantmentEntityEffect {
    public static final MapCodec<RegenerationEnchantmentEffect> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                LevelBasedValue.CODEC.fieldOf("level").forGetter(RegenerationEnchantmentEffect::level)
            ).apply(instance, RegenerationEnchantmentEffect::new));

    @Override
    public void apply(
        ServerLevel serverLevel,
        int enchantmentLevel,
        EnchantedItemInUse item,
        Entity entity,
        Vec3 position) {
        if (item.owner() == null) return;
        AccessoriesCapability cap = AccessoriesCapability.get(item.owner());
        if (cap == null) return;
        List<SlotEntryReference> ls = cap.getEquipped((x) -> x.is(PlayerCollarsMod.COLLAR_TAG));
        for (SlotEntryReference p : ls) {
            OwnerComponent oc = p.stack().get(PlayerCollarsMod.OWNER_COMPONENT_TYPE);
            if (oc != null) {
                var own = serverLevel.getPlayerByUUID(oc.uuid());
                if (own != null && own.distanceTo(item.owner()) < 16) {
                    item.owner().addEffect(new MobEffectInstance(MobEffects.REGENERATION, 40, 1, false, false, false));
                    return;
                }
            }
        }
    }

    @Override
    public MapCodec<? extends EnchantmentEntityEffect> codec() {
        return CODEC;
    }
}
